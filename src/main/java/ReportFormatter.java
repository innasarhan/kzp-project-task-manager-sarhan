import java.util.Locale;

/**
 * Формує текстовий звіт за результатами обробки задач.
 */
public class ReportFormatter {

    /**
     * Формує звіт із чотирма основними показниками.
     *
     * @param validTasks кількість валідних задач
     * @param totalEstimateHours сумарна оцінка задач у годинах
     * @param averagePriority середній пріоритет задач
     * @param completedTasks кількість виконаних задач
     * @return відформатований текст звіту
     */
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
