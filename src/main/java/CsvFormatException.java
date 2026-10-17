/**
 * Виняток, що сигналізує про помилки читання або запису CSV.
 */
public class CsvFormatException extends RuntimeException {

    /**
     * Створює виняток із повідомленням.
     *
     * @param message опис помилки
     */
    public CsvFormatException(String message) {
        super(message);
    }

    /**
     * Створює виняток із повідомленням і початковою причиною.
     *
     * @param message опис помилки
     * @param cause початкова причина помилки
     */
    public CsvFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
