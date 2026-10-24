from dataclasses import FrozenInstanceError

import pytest

from project_tasks.models import (
    AssigneeWorkload,
    DevelopmentTask,
    ProjectTask,
    ScoredTask,
    TestingTask,
)


def make_development_task(**overrides: object) -> DevelopmentTask:
    values: dict[str, object] = {
        "name": "Website design",
        "assignee": "Anna",
        "hours": 5,
        "status": "in_progress",
        "priority": "high",
    }
    values.update(overrides)
    return DevelopmentTask(**values)  # type: ignore[arg-type]


def test_properties_normalize_text_values() -> None:
    task = DevelopmentTask(
        "  Website design  ",
        "  Anna  ",
        5,
        " IN_PROGRESS ",
        " HIGH ",
    )

    assert task.name == "Website design"
    assert task.assignee == "Anna"
    assert task.status == "in_progress"
    assert task.priority == "high"


@pytest.mark.parametrize(
    ("field", "value"),
    [
        ("name", ""),
        ("name", "   "),
        ("assignee", ""),
        ("assignee", "   "),
        ("hours", -1),
        ("hours", 2.5),
        ("hours", True),
        ("status", "unknown"),
        ("priority", "urgent"),
    ],
)
def test_setters_reject_invalid_values(field: str, value: object) -> None:
    task = make_development_task()

    with pytest.raises(ValueError):
        setattr(task, field, value)


def test_assignee_workload_is_immutable() -> None:
    workload = AssigneeWorkload("Anna", 2, 8)

    with pytest.raises(FrozenInstanceError):
        workload.total_hours = 10  # type: ignore[misc]


def test_string_and_repr_represent_task() -> None:
    task = make_development_task()

    assert str(task) == "Website design (high, in_progress)"
    assert repr(task).startswith("DevelopmentTask(")
    assert "assignee='Anna'" in repr(task)


def test_equal_tasks_have_same_values_and_type() -> None:
    first = make_development_task()
    second = make_development_task()

    assert first == second
    assert first != make_development_task(hours=6)
    assert first != TestingTask("Website design", "Anna", 5, "in_progress", "high")


def test_sort_key_orders_by_score_then_name_and_assignee() -> None:
    tasks = [
        DevelopmentTask("Zebra", "Anna", 2, "todo", "low"),
        DevelopmentTask("Apple", "Anna", 2, "todo", "high"),
        DevelopmentTask("Apple", "Bohdan", 2, "todo", "high"),
    ]

    assert sorted(tasks, key=lambda task: task.sort_key()) == [
        tasks[1],
        tasks[2],
        tasks[0],
    ]


def test_subclasses_have_different_priority_scores() -> None:
    development = DevelopmentTask("Build API", "Anna", 4, "todo", "high")
    testing = TestingTask("Test API", "Anna", 4, "todo", "high")

    assert development.priority_score() == 6
    assert testing.priority_score() == 4


def test_tasks_satisfy_scored_task_protocol() -> None:
    assert isinstance(make_development_task(), ScoredTask)
    assert isinstance(
        TestingTask("Test API", "Anna", 2, "todo", "medium"),
        ScoredTask,
    )


def test_base_task_requires_subclass_priority_score() -> None:
    task = ProjectTask("Task", "Anna", 1, "todo", "low")

    with pytest.raises(NotImplementedError):
        task.priority_score()
