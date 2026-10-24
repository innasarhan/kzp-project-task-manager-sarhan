import csv
from pathlib import Path

from project_tasks.errors import DataError
from project_tasks.models import DevelopmentTask, ProjectTask, TestingTask


REQUIRED_COLUMNS = {"name", "assignee", "hours", "status", "priority"}


def load_tasks(path: str | Path) -> list[ProjectTask]:
    """Load CSV rows as validated task objects with row-specific errors."""
    csv_path = Path(path)

    try:
        with csv_path.open("r", encoding="utf-8", newline="") as file:
            reader = csv.DictReader(file, strict=True)

            if reader.fieldnames is None:
                raise DataError("CSV file is empty or has no header.")

            missing_columns = REQUIRED_COLUMNS - set(reader.fieldnames)
            if missing_columns:
                raise DataError(f"Missing required columns: {', '.join(sorted(missing_columns))}")

            tasks: list[ProjectTask] = []

            for row_number, row in enumerate(reader, start=2):
                if None in row:
                    raise DataError(f"Too many values in CSV row {row_number}.")

                for column in sorted(REQUIRED_COLUMNS):
                    if row.get(column) is None:
                        raise DataError(f"Missing value in row {row_number}, field '{column}'.")

                name = row["name"].strip()
                assignee = row["assignee"].strip()
                raw_hours = row["hours"].strip()
                status = row["status"].strip().lower()
                priority = row["priority"].strip().lower()

                fields = {
                    "name": name,
                    "assignee": assignee,
                    "status": status,
                    "priority": priority,
                }

                for field_name, value in fields.items():
                    if not value:
                        raise DataError(f"Empty value in row {row_number}, field '{field_name}'.")

                try:
                    hours = int(raw_hours)
                except ValueError as error:
                    raise DataError(
                        f"Invalid value in row {row_number}, "
                        f"field 'hours': {raw_hours!r} is not an integer."
                    ) from error

                if hours < 0:
                    raise DataError(
                        f"Invalid value in row {row_number}, "
                        f"field 'hours': {hours} cannot be negative."
                    )

                try:
                    if "test" in name.casefold():
                        task_type = TestingTask
                    else:
                        task_type = DevelopmentTask

                    task = task_type(
                        name=name,
                        assignee=assignee,
                        hours=hours,
                        status=status,
                        priority=priority,
                    )
                except ValueError as error:
                    raise DataError(f"Invalid task in CSV row {row_number}: {error}") from error

                tasks.append(task)

    except OSError as error:
        raise DataError(f"Cannot read CSV file: {csv_path}") from error
    except csv.Error as error:
        raise DataError(f"Invalid CSV format in {csv_path}: {error}") from error

    return tasks
