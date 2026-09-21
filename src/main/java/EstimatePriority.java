/**
 * Незмінне значення, що об'єднує оцінку задачі та її пріоритет.
 *
 * @param estimateHours оцінка задачі в годинах
 * @param priority пріоритет задачі
 */
public record EstimatePriority(double estimateHours, int priority) {
}