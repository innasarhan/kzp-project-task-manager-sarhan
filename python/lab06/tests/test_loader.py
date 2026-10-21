from pathlib import Path

import pytest

from project_tasks.errors import DataError
from project_tasks.loader import load_tasks


def test_load_tasks_reads_valid_csv(tmp_path: Path) -> None:
    csv_file = tmp_path / "tasks.csv"
    csv_file.write_text(
        "name,assignee,hours,status,priority\nWebsite design,Anna,5,in_progress,high\n",
        encoding="utf-8",
    )

    tasks = load_tasks(csv_file)

    assert tasks == [
        {
            "name": "Website design",
            "assignee": "Anna",
            "hours": 5,
            "status": "in_progress",
            "priority": "high",
        }
    ]


def test_load_tasks_rejects_invalid_hours(tmp_path: Path) -> None:
    csv_file = tmp_path / "tasks.csv"
    csv_file.write_text(
        "name,assignee,hours,status,priority\nWebsite design,Anna,abc,todo,high\n",
        encoding="utf-8",
    )

    with pytest.raises(
        DataError,
        match=r"row 2, field 'hours': 'abc' is not an integer",
    ):
        load_tasks(csv_file)


def test_load_tasks_rejects_missing_column(tmp_path: Path) -> None:
    csv_file = tmp_path / "tasks.csv"
    csv_file.write_text(
        "name,assignee,hours,status\nWebsite design,Anna,5,todo\n",
        encoding="utf-8",
    )

    with pytest.raises(DataError, match="Missing required columns"):
        load_tasks(csv_file)


def test_load_tasks_rejects_missing_value(tmp_path: Path) -> None:
    csv_file = tmp_path / "tasks.csv"
    csv_file.write_text(
        "name,assignee,hours,status,priority\nWebsite design,Anna,5,todo\n",
        encoding="utf-8",
    )

    with pytest.raises(DataError, match="Missing value in row 2"):
        load_tasks(csv_file)


def test_load_tasks_rejects_completely_empty_file(tmp_path: Path) -> None:
    csv_file = tmp_path / "empty.csv"
    csv_file.write_text("", encoding="utf-8")

    with pytest.raises(DataError, match="CSV file is empty or has no header"):
        load_tasks(csv_file)


def test_load_tasks_returns_empty_list_for_header_only(tmp_path: Path) -> None:
    csv_file = tmp_path / "empty_tasks.csv"
    csv_file.write_text(
        "name,assignee,hours,status,priority\n",
        encoding="utf-8",
    )

    assert load_tasks(csv_file) == []


def test_load_tasks_rejects_extra_values_in_row(tmp_path: Path) -> None:
    csv_file = tmp_path / "extra_values.csv"
    csv_file.write_text(
        "name,assignee,hours,status,priority\nWebsite design,Anna,5,in_progress,high,unexpected\n",
        encoding="utf-8",
    )

    with pytest.raises(DataError, match="Too many values in CSV row 2"):
        load_tasks(csv_file)


def test_load_tasks_rejects_malformed_csv(tmp_path: Path) -> None:
    csv_file = tmp_path / "malformed.csv"
    csv_file.write_text(
        'name,assignee,hours,status,priority\n"Website design"oops,Anna,5,in_progress,high\n',
        encoding="utf-8",
    )

    with pytest.raises(DataError, match="Invalid CSV format"):
        load_tasks(csv_file)
