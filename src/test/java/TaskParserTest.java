import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskParserTest {

    @Test
    void shouldReadOnlyValidTasks() throws Exception {
        Path tempFile = Files.createTempFile("tasks", ".csv");

        Files.writeString(
                tempFile,
                """
                Розробити API;Іван;12.5;1;true
                Створити дизайн;Олена;8.0;2;false
                Виправити помилку;Марія;-4.0;2;false
                Документація;Петро;abc;1;true

                Неправильний рядок;Іван;10.0
                """
        );

        TaskParser parser = new TaskParser();

        List<ProjectTask> tasks = parser.readTasks(tempFile);

        assertEquals(2, tasks.size());

        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldThrowExceptionWhenAllRowsAreInvalid() throws Exception {
        Path tempFile = Files.createTempFile("tasks", ".csv");

        Files.writeString(
                tempFile,
                """
                Задача 1;Іван;abc;1;true
                Задача 2;Олена;-5.0;2;false
                """
        );

        TaskParser parser = new TaskParser();

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions
                .assertThrows(
                        IllegalArgumentException.class,
                        () -> parser.readTasks(tempFile)
                );

        assertEquals(
                "Не знайдено жодного валідного запису",
                exception.getMessage()
        );

        Files.deleteIfExists(tempFile);
    }
}
