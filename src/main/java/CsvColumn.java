import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Позначає поле, яке потрібно включити до CSV-файлу.
 *
 * <p>Значення анотації визначає заголовок відповідного стовпця.
 * Порядок стовпців визначається окремо за назвами Java-полів.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn {

    /**
     * Повертає заголовок CSV-стовпця.
     *
     * @return назва стовпця
     */
    String value();
}
