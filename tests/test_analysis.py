from pathlib import Path

from project_tasks.analysis import analyze_tasks
from project_tasks.loader import load_tasks
from project_tasks.models import AssigneeWorkload, DevelopmentTask, TestingTask


def sample_tasks() -> list[DevelopmentTask | TestingTask]:
    return [
        DevelopmentTask("Website design", "Anna", 5, "in_progress", "high"),
        DevelopmentTask("Database setup", "Anna", 3, "done", "medium"),
        TestingTask("Fix login bug", "Oleh", 2, "todo", "high"),
    ]


def test_analyze_tasks_excludes_done_by_default() -> None:
    result = analyze_tasks(sample_tasks())

    assert result["total_tasks"] == 2
    assert result["total_hours"] == 7
    assert result["hours_by_assignee"] == {"Anna": 5, "Oleh": 2}
    assert result["assignees"] == ["Anna", "Oleh"]
    assert len(result["high_priority_tasks"]) == 2


def test_analyze_tasks_can_include_done() -> None:
    result = analyze_tasks(sample_tasks(), include_done=True)

    assert result["total_tasks"] == 3
    assert result["total_hours"] == 10


def test_analyze_tasks_can_filter_by_status() -> None:
    result = analyze_tasks(sample_tasks(), False, "done")

    assert result["total_tasks"] == 1
    assert result["total_hours"] == 3


def test_analyze_tasks_supports_extra_options() -> None:
    result = analyze_tasks(sample_tasks(), min_hours=3)

    assert result["total_tasks"] == 1
    assert result["total_hours"] == 5


def test_analyze_tasks_supports_multiple_statuses() -> None:
    result = analyze_tasks(sample_tasks(), False, "done", "todo")

    assert result["total_tasks"] == 2
    assert result["total_hours"] == 5


def test_analyze_tasks_counts_duplicate_tasks_but_keeps_unique_assignees() -> None:
    tasks = sample_tasks()
    tasks.append(tasks[0])

    result = analyze_tasks(tasks)

    assert result["total_tasks"] == 3
    assert result["total_hours"] == 12
    assert result["assignees"] == ["Anna", "Oleh"]


def test_analyze_tasks_calculates_assignee_workload() -> None:
    result = analyze_tasks(sample_tasks())

    assert result["workload"] == [
        AssigneeWorkload("Anna", 1, 5),
        AssigneeWorkload("Oleh", 1, 2),
    ]


def test_analyze_tasks_calculates_polymorphic_priority_scores() -> None:
    result = analyze_tasks(sample_tasks(), include_done=True)

    scores = [(task.name, score) for task, score in result["priority_scores"]]

    assert scores == [
        ("Website design", 6),
        ("Database setup", 4),
        ("Fix login bug", 4),
    ]


def test_object_model_matches_lab6_baseline() -> None:
    """Перевіряє збереження результатів аналізу з лабораторної №6."""
    project_root = Path(__file__).resolve().parents[1]
    tasks = load_tasks(project_root / "data" / "tasks.csv")

    result = analyze_tasks(tasks)

    assert result["total_tasks"] == 6
    assert result["total_hours"] == 26
    assert result["hours_by_assignee"] == {
        "Anna": 9,
        "Ivan": 4,
        "Oleh": 13,
    }
    assert result["assignees"] == ["Anna", "Ivan", "Oleh"]
    assert len(result["high_priority_tasks"]) == 4
