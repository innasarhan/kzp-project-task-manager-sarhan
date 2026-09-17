import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static final String VERSION = "0.1.0";

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--help")) {
            printHelp();
            return;
        }

        if (args.length > 0 && args[0].equals("--version")) {
            System.out.println(VERSION);
            return;
        }

        Path inputPath = Path.of("data/input.csv");
        Path outputPath = null;

        if (args.length >= 2 && args[0].equals("--input")) {
            inputPath = Path.of(args[1]);
        }

        if (args.length >= 2 && args[0].equals("--output")) {
            outputPath = Path.of(args[1]);
        }

        if (args.length >= 4
                && args[0].equals("--input")
                && args[2].equals("--output")) {
            inputPath = Path.of(args[1]);
            outputPath = Path.of(args[3]);
        }

        TaskParser parser = new TaskParser();
        TaskMetrics metrics = new TaskMetrics();
        ReportFormatter formatter = new ReportFormatter();

        List<Task> tasks = parser.readTasks(inputPath);

        String report = formatter.format(
                tasks.size(),
                metrics.totalEstimateHours(tasks),
                metrics.averagePriority(tasks),
                metrics.countCompletedTasks(tasks)
        );

        System.out.print(report);

        if (outputPath != null) {
            writeReport(outputPath, report);
        }
    }

    private static void writeReport(Path outputPath, String report)
            throws IOException {

        Path parent = outputPath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(
                outputPath,
                report,
                StandardCharsets.UTF_8
        );
    }

    private static void printHelp() {
        System.out.println("Використання:");
        System.out.println("  java -jar lab01.jar [--input FILE] [--output FILE]");
        System.out.println();
        System.out.println("Опції:");
        System.out.println("  --help              показати довідку");
        System.out.println("  --input FILE        шлях до вхідного CSV-файлу");
        System.out.println("  --output FILE       шлях до файлу звіту");
        System.out.println("  --version           показати версію програми");
    }
}