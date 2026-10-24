from typing import Any

from project_tasks.models import AssigneeWorkload, ProjectTask, ScoredTask


def analyze_tasks(
    tasks: list[ProjectTask],
    include_done: bool = False,
    *statuses: str,
    **options: Any,
) -> dict[str, Any]:
    """Filter task objects and calculate summary statistics."""
    selected_statuses = {status.lower() for status in statuses}

    if selected_statuses:
        filtered = [task for task in tasks if task.status.lower() in selected_statuses]
    elif include_done:
        filtered = list(tasks)
    else:
        filtered = [task for task in tasks if task.status != "done"]

    min_hours = options.get("min_hours")
    if min_hours is not None:
        filtered = [task for task in filtered if task.hours >= int(min_hours)]

    hours_by_assignee: dict[str, int] = {}
    task_counts: dict[str, int] = {}

    for task in filtered:
        assignee = task.assignee
        hours_by_assignee[assignee] = hours_by_assignee.get(assignee, 0) + task.hours
        task_counts[assignee] = task_counts.get(assignee, 0) + 1

    assignees = sorted(hours_by_assignee)
    total_hours = sum(task.hours for task in filtered)

    high_priority_tasks = [task for task in filtered if task.priority == "high"]

    priority_scores = [
        (task, task.priority_score()) for task in filtered if isinstance(task, ScoredTask)
    ]

    workload = [
        AssigneeWorkload(
            assignee=assignee,
            task_count=task_counts[assignee],
            total_hours=hours_by_assignee[assignee],
        )
        for assignee in assignees
    ]

    return {
        "tasks": filtered,
        "total_tasks": len(filtered),
        "total_hours": total_hours,
        "hours_by_assignee": dict(sorted(hours_by_assignee.items())),
        "assignees": assignees,
        "high_priority_tasks": high_priority_tasks,
        "workload": workload,
        "priority_scores": priority_scores,
    }
