import argparse
import sys

from project_tasks.analysis import analyze_tasks
from project_tasks.errors import DataError
from project_tasks.loader import load_tasks
from project_tasks.reporting import write_report


def main() -> int:
    """Run the project task analysis command-line interface."""
    parser = argparse.ArgumentParser(
        description="Analyze project tasks from a CSV file and write a report."
    )
    parser.add_argument(
        "input",
        help="Path to the input CSV file",
    )
    parser.add_argument(
        "output",
        help="Path to the output text report",
    )
    args = parser.parse_args()

    try:
        tasks = load_tasks(args.input)
        analysis = analyze_tasks(tasks)
        write_report(analysis, args.output)
    except DataError as error:
        print(f"Error: {error}", file=sys.stderr)
        return 1
    except OSError as error:
        print(f"Error writing report: {error}", file=sys.stderr)
        return 1

    print(f"Report created: {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
