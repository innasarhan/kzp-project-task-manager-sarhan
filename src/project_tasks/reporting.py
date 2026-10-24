from pathlib import Path
from typing import Any

from project_tasks.models import ProjectTask


def write_report(
    analysis: dict[str, Any],
    output_path: str | Path,
) -> None:
    """Write analysis results to a UTF-8 text report."""
    lines = [
        "PROJECT TASKS REPORT",
        "====================",
        f"Total active tasks: {analysis['total_tasks']}",
        f"Total hours: {analysis['total_hours']}",
        "",
        "HOURS BY ASSIGNEE",
        "-----------------",
    ]

    for assignee, hours in analysis["hours_by_assignee"].items():
        lines.append(f"{assignee}: {hours} hours")

    lines.extend(
        [
            "",
            "UNIQUE ASSIGNEES",
            "----------------",
            ", ".join(analysis["assignees"]) or "None",
            "",
            "HIGH PRIORITY TASKS",
            "-------------------",
        ]
    )

    high_priority_tasks: list[ProjectTask] = analysis["high_priority_tasks"]

    if high_priority_tasks:
        for task in high_priority_tasks:
            lines.append(f"- {task.name} | {task.assignee} | {task.hours} hours | {task.status}")
    else:
        lines.append("None")

    report_path = Path(output_path)
    report_path.parent.mkdir(parents=True, exist_ok=True)

    with report_path.open("w", encoding="utf-8", newline="") as file:
        file.write("\n".join(lines) + "\n")
