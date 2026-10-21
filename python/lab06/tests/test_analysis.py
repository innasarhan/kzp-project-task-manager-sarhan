from project_tasks.analysis import analyze_tasks


def sample_tasks() -> list[dict[str, str | int]]:
    return [
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
    tasks.append(tasks[0].copy())

    result = analyze_tasks(tasks)

    assert result["total_tasks"] == 3
    assert result["total_hours"] == 12
    assert result["assignees"] == ["Anna", "Oleh"]
