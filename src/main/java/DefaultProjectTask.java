/**
 * Стандартна проєктна задача без спеціалізованого правила пріоритету.
 *
 * <p>Клас використовується для збереження базової поведінки
 * попередньої версії моделі та сумісності з CSV-форматом.</p>
 */
public class DefaultProjectTask extends ProjectTask {

    /**
     * Створює стандартну проєктну задачу.
     *
     * @param title назва задачі
     * @param assignee виконавець задачі
     * @param estimateHours оцінка тривалості в годинах
     * @param priority базовий пріоритет
     * @param done ознака виконання
     */
    public DefaultProjectTask(
            String title,
            String assignee,
            double estimateHours,
            int priority,
            boolean done) {

        super(title, assignee, estimateHours, priority, done);
    }

    /**
     * Повертає базову оцінку пріоритету без додаткової модифікації.
     *
     * @return базовий пріоритет
     */
    @Override
    public int evaluatePriority() {
        return getPriority();
    }

    /**
     * Повертає тип стандартної задачі.
     *
     * @return тип DEVELOPMENT
     */
    @Override
    public TaskKind getKind() {
        return TaskKind.DEVELOPMENT;
    }
}
