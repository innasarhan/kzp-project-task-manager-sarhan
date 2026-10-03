/**
 * Проєктна задача тестування.
 *
 * <p>Задача тестування має власне правило оцінки пріоритету.</p>
 */
public class TestingTask extends ProjectTask {

    /**
     * Створює задачу тестування.
     *
     * @param title назва задачі
     * @param assignee виконавець задачі
     * @param estimateHours оцінка тривалості в годинах
     * @param priority базовий пріоритет
     * @param done ознака виконання
     */
    public TestingTask(
            String title,
            String assignee,
            double estimateHours,
            int priority,
            boolean done) {

        super(title, assignee, estimateHours, priority, done);
    }

    /**
     * Оцінює пріоритет задачі тестування.
     *
     * @return оцінений пріоритет
     */
    @Override
    public int evaluatePriority() {
        return getPriority() + (isDone() ? 0 : 1);
    }

    /**
     * Повертає тип задачі.
     *
     * @return тип TESTING
     */
    @Override
    public TaskKind getKind() {
        return TaskKind.TESTING;
    }
}
