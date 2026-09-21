import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Читає задачі з CSV-файлу.
 */
public class TaskParser {

    /**
     * Читає всі валідні задачі з файлу.
     *
     * @param path шлях до CSV-файлу
     * @return список валідних задач
     * @throws IOException якщо файл неможливо прочитати
     * @throws IllegalArgumentException якщо не знайдено жодного
     *                                  валідного запису
     */
    public List<ProjectTask> readTasks(Path path) throws IOException {
        List<ProjectTask> tasks = new ArrayList<>();

        List<String> lines = Files.readAllLines(
                path,
                StandardCharsets.UTF_8
        );

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;

            if (line.isBlank()) {
                System.out.println(
                        "Рядок " + lineNumber + ": порожній"
                );
                continue;
            }

            try {
                tasks.add(ProjectTask.fromCsv(line));
            } catch (IllegalArgumentException e) {
                System.out.println(
                        "Рядок " + lineNumber + ": " + e.getMessage()
                );
            }
        }

        if (tasks.isEmpty()) {
            throw new IllegalArgumentException(
                    "Не знайдено жодного валідного запису"
            );
        }

        return tasks;
    }
}
