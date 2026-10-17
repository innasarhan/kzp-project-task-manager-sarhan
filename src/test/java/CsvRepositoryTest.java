import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void repositoryAddsSearchesAndReturnsSnapshot() {
        Repository<ProjectTaskRecord> repository = new Repository<>();

        ProjectTaskRecord first = task(
                "API", "Olena", 4.0,
                TaskStatus.TODO, TaskPriority.HIGH);

        ProjectTaskRecord second = task(
                "Tests", "Ivan", 2.0,
                TaskStatus.DONE, TaskPriority.LOW);

        repository.add(first);
        repository.add(second);

        assertEquals(2, repository.size());
        assertEquals(2, repository.snapshot().size());
        assertEquals(
                List.of(first),
                repository.search(item -> item.getPriority() == TaskPriority.HIGH));
        assertFalse(repository.isEmpty());

        assertThrows(
                UnsupportedOperationException.class,
                () -> repository.snapshot().add(first));
    }

    @Test
    void repositoryRejectsNullValues() {
        Repository<ProjectTaskRecord> repository = new Repository<>();

        assertThrows(
                NullPointerException.class,
                () -> repository.add(null));

        assertThrows(
                NullPointerException.class,
                () -> repository.search(null));
    }

    @Test
    void csvExportAndImportPreserveRecords() {
        Path file = tempDir.resolve("tasks.csv");

       List<ProjectTaskRecord> original = List.of(
        task("API, v2", "Olena", 4.5,
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH),
        task("Fix \"login\"", "Ivan", 2.0,
                TaskStatus.TODO, TaskPriority.MEDIUM),
        task("First line\nSecond line", "Oksana", 1.5,
                TaskStatus.DONE, TaskPriority.LOW),
        task("Database migration", "Andrii", 6.0,
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH),
        task("Documentation", "Maria", 2.5,
                TaskStatus.TODO, TaskPriority.MEDIUM));

        new CsvExporter().export(original, file);

        List<ProjectTaskRecord> imported =
                new CsvImporter().importFile(file, ProjectTaskRecord.class);

        assertEquals(original, imported);
    }

    @Test
    void csvExporterUsesExpectedHeaders() throws IOException {
        Path file = tempDir.resolve("headers.csv");

        new CsvExporter().export(
                List.of(task(
                        "API", "Olena", 3.0,
                        TaskStatus.TODO, TaskPriority.HIGH)),
                file);

        String firstLine = Files.readAllLines(
                file, StandardCharsets.UTF_8).get(0);

        assertEquals(
                "виконавець,години,назва,пріоритет,статус",
                firstLine);
    }

    @Test
    void importerReturnsEmptyListForEmptyFile() throws IOException {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "", StandardCharsets.UTF_8);

        List<ProjectTaskRecord> imported =
                new CsvImporter().importFile(file, ProjectTaskRecord.class);

        assertTrue(imported.isEmpty());
    }

    @Test
    void importerRejectsWrongFieldCount() throws IOException {
        Path file = tempDir.resolve("wrong-count.csv");

        Files.writeString(
                file,
                "виконавець,години,назва,пріоритет,статус\n"
                        + "Olena,4.0,API,HIGH\n",
                StandardCharsets.UTF_8);

        assertThrows(
                CsvFormatException.class,
                () -> new CsvImporter().importFile(
                        file, ProjectTaskRecord.class));
    }

    @Test
    void importerRejectsInvalidNumber() throws IOException {
        Path file = tempDir.resolve("invalid-number.csv");

        Files.writeString(
                file,
                "виконавець,години,назва,пріоритет,статус\n"
                        + "Olena,not-a-number,API,HIGH,TODO\n",
                StandardCharsets.UTF_8);

        assertThrows(
                CsvFormatException.class,
                () -> new CsvImporter().importFile(
                        file, ProjectTaskRecord.class));
    }

    @Test
    void importerRejectsInvalidEnum() throws IOException {
        Path file = tempDir.resolve("invalid-enum.csv");

        Files.writeString(
                file,
                "виконавець,години,назва,пріоритет,статус\n"
                        + "Olena,4.0,API,URGENT,TODO\n",
                StandardCharsets.UTF_8);

        assertThrows(
                CsvFormatException.class,
                () -> new CsvImporter().importFile(
                        file, ProjectTaskRecord.class));
    }

    @Test
    void importerRejectsUnclosedQuotedField() throws IOException {
        Path file = tempDir.resolve("unclosed.csv");

        Files.writeString(
                file,
                "виконавець,години,назва,пріоритет,статус\n"
                        + "Olena,4.0,\"unfinished,HIGH,TODO\n",
                StandardCharsets.UTF_8);

        assertThrows(
                CsvFormatException.class,
                () -> new CsvImporter().importFile(
                        file, ProjectTaskRecord.class));
    }

    @Test
    void importerWrapsFileErrors() {
        Path missing = tempDir.resolve("missing.csv");

        CsvFormatException exception = assertThrows(
                CsvFormatException.class,
                () -> new CsvImporter().importFile(
                        missing, ProjectTaskRecord.class));

        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    void recordsCompareAllFieldsAndIdentitySeparately() {
        ProjectTaskRecord first = task(
                "API", "Olena", 4.0,
                TaskStatus.TODO, TaskPriority.HIGH);

        ProjectTaskRecord equal = task(
                "API", "Olena", 4.0,
                TaskStatus.TODO, TaskPriority.HIGH);

        ProjectTaskRecord changed = task(
                "API", "Olena", 5.0,
                TaskStatus.DONE, TaskPriority.LOW);

        assertEquals(first, equal);
        assertEquals(first.hashCode(), equal.hashCode());
        assertTrue(first.hasSameIdentity(changed));
        assertFalse(first.equals(changed));
    }

    private ProjectTaskRecord task(
            String name,
            String assignee,
            double hours,
            TaskStatus status,
            TaskPriority priority) {

        return new ProjectTaskRecord(
                name, assignee, hours, status, priority);
    }

@Test
void exportingEmptyListCreatesEmptyFileAndImportsEmptyList()
        throws IOException {
    Path file = tempDir.resolve("empty-export.csv");

    new CsvExporter().export(List.of(), file);

    assertEquals("", Files.readString(file, StandardCharsets.UTF_8));

    List<ProjectTaskRecord> imported =
            new CsvImporter().importFile(file, ProjectTaskRecord.class);

    assertTrue(imported.isEmpty());
}
}
