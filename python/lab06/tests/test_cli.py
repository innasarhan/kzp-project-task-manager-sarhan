from pathlib import Path

from project_tasks.cli import main


def test_cli_creates_report(tmp_path: Path, monkeypatch) -> None:
    input_file = tmp_path / "tasks.csv"
    output_file = tmp_path / "report.txt"

    input_file.write_text(
        "name,assignee,hours,status,priority\n"
        "Website design,Anna,5,in_progress,high\n"
        "Database setup,Oleh,3,done,medium\n",
        encoding="utf-8",
    )

    monkeypatch.setattr(
        "sys.argv",
        ["project-tasks", str(input_file), str(output_file)],
    )

    assert main() == 0
    assert output_file.exists()

    report = output_file.read_text(encoding="utf-8")
    assert "Total active tasks: 1" in report
    assert "Total hours: 5" in report


def test_cli_returns_error_for_missing_input(
    tmp_path: Path,
    monkeypatch,
    capsys,
) -> None:
    missing_file = tmp_path / "missing.csv"
    output_file = tmp_path / "report.txt"

    monkeypatch.setattr(
        "sys.argv",
        ["project-tasks", str(missing_file), str(output_file)],
    )

    assert main() == 1

    captured = capsys.readouterr()
    assert "Error:" in captured.err
    assert not output_file.exists()
