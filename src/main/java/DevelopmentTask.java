/**
 * Проєктна задача розробки.
 *
 * <p>Для задачі розробки підвищується оцінка пріоритету
 * залежно від оцінки тривалості.</p>
 */
public class DevelopmentTask extends ProjectTask {

    /**
     * Створює задачу розробки.
     *
     * @param title назва задачі
     * @param assignee виконавець задачі
     * @param estimateHours оцінка тривалості в годинах
     * @param priority базовий пріоритет
     * @param done ознака виконання
     */
    public DevelopmentTask(
            String title,
            String assignee,
            double estimateHours,
            int priority,
            boolean done) {

        super(title, assignee, estimateHours, priority, done);
    }

    /**
     * Оцінює пріоритет задачі розробки.
     *
     * @return оцінений пріоритет
     */
    @Override
    public int evaluatePriority() {
        return getPriority() + (getEstimateHours() >= 10.0 ? 1 : 0);
    }

    /**
     * Повертає тип задачі.
     *
     * @return тип DEVELOPMENT
     */
    @Override
    public TaskKind getKind() {
        return TaskKind.DEVELOPMENT;
    }
}
