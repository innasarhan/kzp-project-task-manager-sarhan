from dataclasses import dataclass
from typing import Protocol, runtime_checkable


@runtime_checkable
class ScoredTask(Protocol):
    """Спільний інтерфейс задач, які мають оцінку пріоритету."""

    name: str
    assignee: str
    hours: int
    status: str
    priority: str

    def priority_score(self) -> int:
        """Повертає числову оцінку пріоритету задачі."""
        ...


@dataclass(frozen=True)
class AssigneeWorkload:
    """Незмінний підсумок завантаження одного виконавця."""

    assignee: str
    task_count: int
    total_hours: int


class ProjectTask:
    """Базовий клас задачі проєкту."""

    VALID_STATUSES = {"todo", "in_progress", "done"}
    VALID_PRIORITIES = {"low", "medium", "high"}
    PRIORITY_WEIGHTS = {"low": 1, "medium": 2, "high": 3}

    def __init__(
        self,
        name: str,
        assignee: str,
        hours: int,
        status: str,
        priority: str,
    ) -> None:
        self.name = name
        self.assignee = assignee
        self.hours = hours
        self.status = status
        self.priority = priority

    @property
    def name(self) -> str:
        return self._name

    @name.setter
    def name(self, value: str) -> None:
        if not isinstance(value, str) or not value.strip():
            raise ValueError("Task name cannot be empty.")
        self._name = value.strip()

    @property
    def assignee(self) -> str:
        return self._assignee

    @assignee.setter
    def assignee(self, value: str) -> None:
        if not isinstance(value, str) or not value.strip():
            raise ValueError("Assignee cannot be empty.")
        self._assignee = value.strip()

    @property
    def hours(self) -> int:
        return self._hours

    @hours.setter
    def hours(self, value: int) -> None:
        if isinstance(value, bool) or not isinstance(value, int):
            raise ValueError("Hours must be an integer.")
        if value < 0:
            raise ValueError("Hours cannot be negative.")
        self._hours = value

    @property
    def status(self) -> str:
        return self._status

    @status.setter
    def status(self, value: str) -> None:
        if not isinstance(value, str):
            raise ValueError("Status must be a string.")
        normalized = value.strip().lower()
        if normalized not in self.VALID_STATUSES:
            raise ValueError(f"Invalid status: {value!r}.")
        self._status = normalized

    @property
    def priority(self) -> str:
        return self._priority

    @priority.setter
    def priority(self, value: str) -> None:
        if not isinstance(value, str):
            raise ValueError("Priority must be a string.")
        normalized = value.strip().lower()
        if normalized not in self.VALID_PRIORITIES:
            raise ValueError(f"Invalid priority: {value!r}.")
        self._priority = normalized

    def priority_score(self) -> int:
        """Повертає оцінку пріоритету, визначену підтипом."""
        raise NotImplementedError("Subclass must implement priority_score().")

    def sort_key(self) -> tuple[int, str, str]:
        """Ключ детермінованого сортування задач."""
        return (
            -self.priority_score(),
            self.name.casefold(),
            self.assignee.casefold(),
        )

    def __str__(self) -> str:
        return f"{self.name} ({self.priority}, {self.status})"

    def __repr__(self) -> str:
        return (
            f"{type(self).__name__}("
            f"name={self.name!r}, "
            f"assignee={self.assignee!r}, "
            f"hours={self.hours!r}, "
            f"status={self.status!r}, "
            f"priority={self.priority!r})"
        )

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, ProjectTask):
            return NotImplemented
        if type(self) is not type(other):
            return False
        return (
            self.name == other.name
            and self.assignee == other.assignee
            and self.hours == other.hours
            and self.status == other.status
            and self.priority == other.priority
        )


class DevelopmentTask(ProjectTask):
    """Задача розробки."""

    def priority_score(self) -> int:
        return self.PRIORITY_WEIGHTS[self.priority] * 2


class TestingTask(ProjectTask):
    """Задача тестування."""

    __test__ = False

    def priority_score(self) -> int:
        return self.PRIORITY_WEIGHTS[self.priority] + 1
