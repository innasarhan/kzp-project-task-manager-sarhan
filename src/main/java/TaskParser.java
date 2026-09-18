import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskParser {

    public List<Task> readTasks(Path path) throws IOException {
        List<Task> tasks = new ArrayList<>();

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;

            if (line.isBlank()) {
                System.out.println("Рядок " + lineNumber + ": порожній");
                continue;
            }

            String[] fields = line.split(";", -1);

            if (fields.length != 5) {
                System.out.println("Рядок " + lineNumber
                        + ": неправильна кількість полів");
                continue;
            }

            String title = fields[0].trim();
            String assignee = fields[1].trim();

            if (title.isEmpty()) {
                System.out.println("Рядок " + lineNumber
                        + ": порожня назва задачі");
                continue;
            }

            if (assignee.isEmpty()) {
                System.out.println("Рядок " + lineNumber
                        + ": порожній виконавець");
                continue;
            }

            double estimateHours;
            int priority;
            boolean done;

            try {
                estimateHours = Double.parseDouble(fields[2].trim());
            } catch (NumberFormatException e) {
                System.out.println("Рядок " + lineNumber
                        + ": оцінка годин має бути числом");
                continue;
            }

            try {
                priority = Integer.parseInt(fields[3].trim());
            } catch (NumberFormatException e) {
                System.out.println("Рядок " + lineNumber
                        + ": пріоритет має бути цілим числом");
                continue;
            }

            if (estimateHours < 0) {
                System.out.println("Рядок " + lineNumber
                        + ": оцінка годин не може бути від'ємною");
                continue;
            }

            if (!fields[4].trim().equalsIgnoreCase("true")
                    && !fields[4].trim().equalsIgnoreCase("false")) {
                System.out.println("Рядок " + lineNumber
                        + ": done має бути true або false");
                continue;
            }

            done = Boolean.parseBoolean(fields[4].trim());

            tasks.add(new Task(title, assignee, estimateHours, priority, done));
        }

                if (tasks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Не знайдено жодного валідного запису");
        }

        return tasks;
    }
}
