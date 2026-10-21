from typing import Any


def analyze_tasks(
    tasks: list[dict[str, Any]],
    include_done: bool = False,
    *statuses: str,
    **options: Any,
) -> dict[str, Any]:
    """Filter tasks and calculate summary statistics."""
    selected_statuses = {status.lower() for status in statuses}

    if selected_statuses:
        filtered = [task for task in tasks if str(task["status"]).lower() in selected_statuses]
    elif include_done:
        filtered = [task for task in tasks]
    else:
        filtered = [task for task in tasks if task["status"] != "done"]

    min_hours = options.get("min_hours")
    if min_hours is not None:
        filtered = [task for task in filtered if int(task["hours"]) >= int(min_hours)]

    hours_by_assignee: dict[str, int] = {}
    for task in filtered:
        assignee = str(task["assignee"])
        hours_by_assignee[assignee] = hours_by_assignee.get(assignee, 0) + int(task["hours"])

    assignees = {str(task["assignee"]) for task in filtered}
    total_hours = sum(int(task["hours"]) for task in filtered)

    high_priority_tasks = [task for task in filtered if str(task["priority"]).lower() == "high"]

    return {
        "tasks": filtered,
        "total_tasks": len(filtered),
        "total_hours": total_hours,
        "hours_by_assignee": dict(sorted(hours_by_assignee.items())),
        "assignees": sorted(assignees),
        "high_priority_tasks": high_priority_tasks,
    }
