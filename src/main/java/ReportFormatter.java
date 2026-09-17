import java.util.Locale;

public class ReportFormatter {

    public String format(int validTasks,
                         double totalEstimateHours,
                         double averagePriority,
                         int completedTasks) {

        return String.format(
                Locale.ROOT,
                """
                Лабораторна робота №1%n\
                Варіант 22%n\
                %n\
                Кількість валідних задач: %d%n\
                Сумарна оцінка годин: %.2f%n\
                Середній пріоритет: %.2f%n\
                Кількість виконаних задач: %d%n\
                """,
                validTasks,
                totalEstimateHours,
                averagePriority,
                completedTasks
        );
    }
}