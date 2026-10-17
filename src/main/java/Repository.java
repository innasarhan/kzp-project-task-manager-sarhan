import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Узагальнений репозиторій для зберігання та пошуку об'єктів.
 *
 * @param <T> тип об'єктів, які зберігаються в репозиторії
 */
public class Repository<T> {

    private final List<T> items = new ArrayList<>();

    /**
     * Додає об'єкт до репозиторію.
     *
     * @param item об'єкт для додавання
     * @throws NullPointerException якщо item дорівнює null
     */
    public void add(T item) {
        items.add(Objects.requireNonNull(item, "item"));
    }

    /**
     * Повертає незмінний знімок усіх об'єктів.
     *
     * @return незмінний список об'єктів
     */
    public List<T> snapshot() {
        return List.copyOf(items);
    }

    /**
     * Знаходить об'єкти, що відповідають заданій умові.
     *
     * @param predicate умова пошуку
     * @return список знайдених об'єктів
     * @throws NullPointerException якщо predicate дорівнює null
     */
    public List<T> search(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        return items.stream()
                .filter(predicate)
                .toList();
    }

    /**
     * Повертає кількість об'єктів у репозиторії.
     *
     * @return кількість об'єктів
     */
    public int size() {
        return items.size();
    }

    /**
     * Перевіряє, чи репозиторій порожній.
     *
     * @return true, якщо об'єктів немає
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
