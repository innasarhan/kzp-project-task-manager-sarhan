from pathlib import Path

from project_tasks.analysis import analyze_tasks
from project_tasks.models import DevelopmentTask, TestingTask
from project_tasks.reporting import write_report


def test_write_report_creates_readable_report(tmp_path: Path) -> None:
    tasks = [
        DevelopmentTask("Website design", "Anna", 5, "in_progress", "high"),
        DevelopmentTask("Database setup", "Anna", 3, "done", "medium"),
        TestingTask("Fix login bug", "Oleh", 2, "todo", "high"),
    ]

    analysis = analyze_tasks(tasks)
    output_path = tmp_path / "reports" / "report.txt"

    write_report(analysis, output_path)

    report = output_path.read_text(encoding="utf-8")

    assert "PROJECT TASKS REPORT" in report
    assert "Total active tasks: 2" in report
    assert "Total hours: 7" in report
    assert "Anna: 5 hours" in report
    assert "Oleh: 2 hours" in report
    assert "Anna, Oleh" in report
    assert "- Website design | Anna | 5 hours | in_progress" in report
    assert "- Fix login bug | Oleh | 2 hours | todo" in report
    assert "Database setup" not in report


def test_write_report_handles_empty_analysis(tmp_path: Path) -> None:
    analysis = analyze_tasks([])
    output_path = tmp_path / "empty_report.txt"

    write_report(analysis, output_path)

    report = output_path.read_text(encoding="utf-8")

    assert "Total active tasks: 0" in report
    assert "Total hours: 0" in report
    assert "UNIQUE ASSIGNEES" in report
    assert "None" in report
    assert "HIGH PRIORITY TASKS" in report
