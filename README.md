# Project Task Manager

## Опис проєкту

Java-проєкт для читання, перевірки та обробки списку проєктних задач.

**Предметна область:** Проєктні задачі

**Варіант:** 22

**Java:** 21

**Система збірки:** Maven

**Поточна версія:** 3.0.0

У лабораторній роботі №3 доменну модель було розширено засобами успадкування та поліморфізму.

Спільний стан та правила валідації зосереджено в абстрактному базовому типі `ProjectTask`. Для збереження стандартної поведінки попередньої моделі додано `DefaultProjectTask`, а спеціалізовану поведінку реалізовано у `DevelopmentTask` та `TestingTask`.

## Формат вхідних даних

Формат CSV збережено сумісним із попередніми лабораторними роботами.

Один запис на рядок:

    title;assignee;estimateHours;priority;done

Поля:

- `title` — назва задачі;
- `assignee` — виконавець;
- `estimateHours` — оцінка тривалості задачі в годинах;
- `priority` — пріоритет;
- `done` — ознака виконання (`true` або `false`).

Роздільник полів: `;`

Кодування: `UTF-8`

Приклад:

    Розробити API;Іван;12.5;1;true

    Створити дизайн;Олена;8.0;2;false

    Написати тести;Андрій;5.5;3;true

Невалідні записи не додаються до списку задач і не впливають на статистику. Для них виводиться номер рядка та причина помилки.

## Основна функціональність

Програма обчислює:

1. кількість валідних задач;
2. сумарну оцінку годин;
3. середній пріоритет;
4. кількість виконаних задач.

Для поточного `data/input.csv` результат:

    Кількість валідних задач: 3

    Сумарна оцінка годин: 26.00

    Середній пріоритет: 2.00

    Кількість виконаних задач: 2

## Доменна модель

### ProjectTask

`ProjectTask` є абстрактним спільним типом для проєктних задач.

Клас містить спільні поля:

- `title`;
- `assignee`;
- `estimateHours`;
- `priority`;
- `done`.

Поля є `private final`.

Конструктор централізовано перевіряє інваріанти:

- `title` не є `null` і не є порожнім;
- `assignee` не є `null` і не є порожнім;
- `estimateHours` є скінченним та не від'ємним;
- `priority` не є від'ємним.

У `ProjectTask` також визначено спільну логіку:

- `fromCsv(...)`;
- гетери;
- `equals()`;
- `hashCode()`;
- `toString()`.

Методи `evaluatePriority()` та `getKind()` оголошені абстрактними та реалізуються конкретними підтипами.

Для збереження сумісності з попередньою моделлю стандартна реалізація представлена окремим класом `DefaultProjectTask`.

### DefaultProjectTask

`DefaultProjectTask` є стандартною реалізацією `ProjectTask`.

Клас зберігає базову поведінку попередньої моделі: `evaluatePriority()` повертає початкове значення `priority` без додаткової модифікації.

`DefaultProjectTask` використовується для збереження стандартної поведінки та сумісності з існуючим CSV-форматом.

### DevelopmentTask

`DevelopmentTask` успадковує `ProjectTask` та перевизначає `evaluatePriority()`.

Для задачі розробки оцінка пріоритету додатково збільшується на 1, якщо оцінка тривалості становить щонайменше 10 годин.

Наприклад:

    priority = 2
    estimateHours = 12.5
    evaluatePriority() = 3

Метод `getKind()` повертає `TaskKind.DEVELOPMENT`.

### TestingTask

`TestingTask` успадковує `ProjectTask` та має власне правило `evaluatePriority()`.

Для невиконаної задачі тестування оцінка пріоритету збільшується на 1.

Для виконаної задачі додаткова зміна пріоритету не виконується.

Наприклад:

    priority = 3
    done = false
    evaluatePriority() = 4

Метод `getKind()` повертає `TaskKind.TESTING`.

### TaskKind

Для визначення типу задачі додано enum `TaskKind`.

Можливі значення:

- `DEVELOPMENT`;
- `TESTING`.

Підтипи повертають відповідне значення через `getKind()`.

## Ієрархія класів

Поточна модель має таку структуру:

    ProjectTask (abstract)
    ├── DefaultProjectTask
    ├── DevelopmentTask
    └── TestingTask

`ProjectTask` містить спільний стан та інваріанти.

`DefaultProjectTask` зберігає стандартну поведінку попередньої моделі.

`DevelopmentTask` та `TestingTask` реалізують спеціалізовану поведінку.

## Поліморфізм

Об'єкти різних підтипів обробляються через спільний тип:

    List<ProjectTask>

Наприклад:

    List<ProjectTask> tasks = List.of(
        new DefaultProjectTask("Документація", "Петро", 4.0, 1, false),
        new DevelopmentTask("Розробити API", "Іван", 12.5, 1, true),
        new TestingTask("Написати тести", "Андрій", 5.5, 3, false)
    );

Оцінка пріоритету виконується через:

    task.evaluatePriority()

без `if` або `switch` для визначення конкретного підтипу.

Java автоматично викликає реалізацію методу відповідного фактичного класу.

`TaskMetrics.averagePriority()` також працює через поліморфний `evaluatePriority()`, тому логіка конкретного типу не дублюється в класі метрик.

Такий підхід дозволяє додавати нові підтипи задач без переписування коду, який працює зі спільним типом `ProjectTask`.

## equals() та hashCode()

Для `ProjectTask` реалізовано:

- `equals()`;
- `hashCode()`.

Рівність враховує значення полів та конкретний клас об'єкта.

Тому однакові об'єкти одного типу вважаються рівними, а об'єкти різних підтипів не вважаються рівними лише через однакові значення полів.

Це забезпечує узгоджену поведінку під час використання:

    HashSet<ProjectTask>

    HashMap<ProjectTask, ...>

Тести перевіряють:

- рівність однакових задач;
- однаковий `hashCode()` для рівних об'єктів;
- відсутність дублювання в `HashSet`;
- відмінність задач різних підтипів.

## Успадкування та композиція

Успадкування використано для `DefaultProjectTask`, `DevelopmentTask` і `TestingTask`, оскільки всі вони є різновидами `ProjectTask`.

Підтипи мають спільний стан та інваріанти, але можуть реалізовувати різну поведінку `evaluatePriority()`.

Тому між класами існує відношення `is-a`.

Композиція могла б бути використана, наприклад, для окремого об'єкта-стратегії, який відповідав би за алгоритм оцінювання пріоритету.

У межах цієї моделі спадкування є природним способом представити спільну частину доменної моделі та різну поведінку конкретних різновидів задач.

## Зворотна сумісність

Для збереження сумісності з попередніми лабораторними роботами:

- формат CSV не змінено;
- правила валідації збережено;
- попередні тести збережено;
- зовнішній формат звіту програми збережено;
- стандартну поведінку попередньої моделі перенесено до `DefaultProjectTask`.

Метод:

    ProjectTask.fromCsv(...)

залишено для роботи з тим самим п'ятикомпонентним форматом вхідних даних.

Таким чином, внутрішня модель була розширена, але зовнішній формат роботи програми залишився сумісним.

## Структура проєкту

    .
    ├── .github/
    │   └── workflows/
    │       └── ci.yml
    │
    ├── data/
    │   └── input.csv
    │
    ├── src/
    │   ├── main/
    │   │   └── java/
    │   │       ├── DefaultProjectTask.java
    │   │       ├── DevelopmentTask.java
    │   │       ├── EstimatePriority.java
    │   │       ├── HelloWorld.java
    │   │       ├── Main.java
    │   │       ├── ProjectTask.java
    │   │       ├── ReportFormatter.java
    │   │       ├── Task.java
    │   │       ├── TaskKind.java
    │   │       ├── TaskMetrics.java
    │   │       ├── TaskParser.java
    │   │       └── TestingTask.java
    │   │
    │   └── test/
    │       └── java/
    │           ├── EstimatePriorityTest.java
    │           ├── ProjectTaskEqualityTest.java
    │           ├── ProjectTaskFromCsvTest.java
    │           ├── ProjectTaskPolymorphismTest.java
    │           ├── ProjectTaskTest.java
    │           ├── TaskKindTest.java
    │           ├── TaskMetricsTest.java
    │           └── TaskParserTest.java
    │
    ├── .gitignore
    ├── pom.xml
    ├── README.md
    ├── REPORT.md
    ├── mvnw
    └── mvnw.cmd

Старий `Task.java` збережено як код попередньої лабораторної роботи. Поточна робоча логіка використовує `ProjectTask` та його підтипи.

## Документація коду

Для публічних класів, конструкторів та методів додано Javadoc.

Документовано, зокрема:

- `ProjectTask`;
- `DefaultProjectTask`;
- `DevelopmentTask`;
- `TestingTask`;
- `TaskKind`;
- методи роботи з моделлю;
- конструктори;
- основні правила поведінки.

## Запуск

### Maven Wrapper

macOS/Linux:

    ./mvnw -B clean verify

Windows:

    mvnw.cmd -B clean verify

### JUnit-тести

    ./mvnw -B test

Поточна кількість автоматичних тестів:

    30

Результат:

    Tests run: 30, Failures: 0, Errors: 0, Skipped: 0

### Запуск програми

    java -cp target/classes Main

### Запуск із власним CSV

    java -cp target/classes Main --input data/input.csv

### Запис звіту у файл

    java -cp target/classes Main --input data/input.csv --output report.txt

### Довідка

    java -cp target/classes Main --help

### Версія

    java -cp target/classes Main --version

Очікувана версія:

    3.0.0

## Виконуваний JAR

Після виконання:

    ./mvnw -B clean package

JAR знаходиться у:

    target/maven-actions-hello-3.0.0.jar

Запуск:

    java -jar target/maven-actions-hello-3.0.0.jar

Приклад запуску з CSV:

    java -jar target/maven-actions-hello-3.0.0.jar --input data/input.csv

## Тестування та статичний аналіз

Повна перевірка:

    ./mvnw -B clean verify

Під час `verify` виконуються:

- компіляція;
- JUnit-тести;
- SpotBugs;
- створення JAR.

Поточний результат тестування:

    Tests run: 30, Failures: 0, Errors: 0, Skipped: 0

    BUILD SUCCESS

SpotBugs:

    BugInstance size is 0

    Error size is 0

    No errors/warnings found

Окремі тести перевіряють:

- базовий тип `ProjectTask`;
- `DefaultProjectTask`;
- `DevelopmentTask`;
- `TestingTask`;
- `TaskKind`;
- поліморфний `evaluatePriority()`;
- `equals()` та `hashCode()`;
- роботу `HashSet`;
- сумісність старих тестів та CSV-формату;
- позитивні, граничні та негативні випадки.

## Continuous Integration

Для проєкту налаштовано GitHub Actions.

CI перевіряє проєкт на:

- Ubuntu;
- Windows;
- macOS.

Для запуску використовується Java 21 та Maven Wrapper.

Під час CI виконуються автоматичні перевірки проєкту та збірка.

Для лабораторної роботи перевірки на трьох операційних системах завершилися успішно.

JAR формується під час збірки:

    target/maven-actions-hello-3.0.0.jar

## GitHub

Репозиторій:

https://github.com/innasarhan/kzp-project-task-manager-sarhan

Поточна гілка лабораторної роботи №3:

    java/lab03

Для лабораторної роботи №3 створено Issues:

    #16 [ЛР3] Розширити модель ProjectTask та додати поліморфізм

    #17 [ЛР3] Додати TaskKind та equals/hashCode

    #18 [ЛР3] Додати тести та оновити документацію

Попередні Issues лабораторної роботи №2 збережено в історії репозиторію.

## Версіювання

Для лабораторної роботи №3 використовується версія:

    3.0.0

Git-тег лабораторної роботи:

    v3.0.0

## Документація

Детальний опис виконання лабораторної роботи №3, змін доменної моделі, поліморфізму, enum, `equals/hashCode`, тестування та порівняння з попередньою реалізацією наведено у `REPORT.md`.

## Академічна доброчесність

Під час виконання роботи використовувалися GitHub Copilot та ChatGPT як допоміжні інструменти для консультацій, роботи з кодом, тестами та документацією.

Усі прийняті зміни перевіряються локально за допомогою Maven та JUnit.

Фінальна реалізація додатково перевіряється статичним аналізом SpotBugs та Continuous Integration.