from pathlib import Path

from project_tasks.analysis import analyze_tasks
from project_tasks.reporting import write_report


def test_write_report_creates_readable_report(tmp_path: Path) -> None:
    tasks = [
        {
            "name": "Website design",
            "assignee": "Anna",
            "hours": 5,
            "status": "in_progress",
            "priority": "high",
        },
        {
            "name": "Database setup",
            "assignee": "Anna",
            "hours": 3,
            "status": "done",
            "priority": "medium",
        },
        {
            "name": "Fix login bug",
            "assignee": "Oleh",
            "hours": 2,
            "status": "todo",
            "priority": "high",
        },
    ]

    analysis = analyze_tasks(tasks)
    output_path = tmp_path / "reports" / "report.txt"

    write_report(analysis, output_path)

    report = output_path.read_text(encoding="utf-8")

    assert "Total active tasks: 2" in report
    assert "Total hours: 7" in report
    assert "Anna: 5 hours" in report
    assert "Oleh: 2 hours" in report
    assert "Website design" in report
    assert "Fix login bug" in report
    assert "Database setup" not in report


def test_write_report_handles_empty_analysis(tmp_path: Path) -> None:
    analysis = analyze_tasks([])
    output_path = tmp_path / "empty_report.txt"

    write_report(analysis, output_path)

    report = output_path.read_text(encoding="utf-8")

    assert "Total active tasks: 0" in report
    assert "Total hours: 0" in report
    assert "None" in report
