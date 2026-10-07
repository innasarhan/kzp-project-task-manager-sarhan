# Project Task Manager

## Опис проєкту

Java-проєкт для читання, перевірки та обробки списку проєктних задач.

**Предметна область:** Проєктні задачі

**Варіант:** 22

**Java:** 21

**Система збірки:** Maven

**Поточна версія:** 4.0.0

У лабораторній роботі №4 реалізовано потокову обробку колекції поліморфних проєктних задач за допомогою Stream API.

Попередню доменну модель з лабораторної роботи №3 збережено. Спільний стан та правила валідації зосереджено в абстрактному типі `ProjectTask`, а різну поведінку реалізовано у `DevelopmentTask` та `TestingTask`.

Основні обчислення попередньої реалізації доповнено Stream API-запитами без зміни предметної області, варіанта, формату вхідних даних та зовнішнього формату звіту.

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

Програма зберігає основні показники попереднього звіту:

1. кількість валідних задач;
2. сумарну оцінку годин;
3. середній пріоритет;
4. кількість виконаних задач.

Для поточного `data/input.csv` результат залишається таким самим:

    Кількість валідних задач: 3
    Сумарна оцінка годин: 26.00
    Середній пріоритет: 2.00
    Кількість виконаних задач: 2

Зовнішній формат звіту не змінювався.

## Доменна модель

### ProjectTask

`ProjectTask` є абстрактним спільним типом для проєктних задач.

Клас містить спільні поля:

- `title`;
- `assignee`;
- `estimateHours`;
- `priority`;
- `done`.

Конструктор централізовано перевіряє інваріанти:

- `title` не є `null` і не є порожнім;
- `assignee` не є `null` і не є порожнім;
- `estimateHours` є скінченним та не від'ємним;
- `priority` не є від'ємним.

У `ProjectTask` також визначено:

- `fromCsv(...)`;
- гетери;
- `equals()`;
- `hashCode()`;
- `toString()`.

Методи `evaluatePriority()` та `getKind()` є поліморфними та реалізуються конкретними підтипами.

### DefaultProjectTask

`DefaultProjectTask` є стандартною реалізацією `ProjectTask`.

Він зберігає поведінку попередньої моделі: `evaluatePriority()` повертає початкове значення `priority`.

### DevelopmentTask

`DevelopmentTask` успадковує `ProjectTask` та перевизначає `evaluatePriority()`.

Для задачі розробки оцінка пріоритету збільшується на 1, якщо оцінка тривалості становить щонайменше 10 годин.

Приклад:

    priority = 2
    estimateHours = 12.5
    evaluatePriority() = 3

### TestingTask

`TestingTask` успадковує `ProjectTask` та має власне правило `evaluatePriority()`.

Для невиконаної задачі тестування оцінка пріоритету збільшується на 1.

Для виконаної задачі додаткова зміна пріоритету не виконується.

Приклад:

    priority = 3
    done = false
    evaluatePriority() = 4

### TaskKind

Для визначення виду задачі використовується enum `TaskKind`.

Можливі значення:

- `DEVELOPMENT`;
- `TESTING`.

## Ієрархія класів

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
        new DevelopmentTask(
            "Розробити API", "Іван", 12.5, 1, true
        ),
        new TestingTask(
            "Написати тести", "Андрій", 5.5, 3, false
        )
    );

Потокові запити працюють зі спільним типом `ProjectTask` та використовують поліморфні методи, зокрема:

    ProjectTask::evaluatePriority

В основних Stream-запитах немає `if` або `switch`, які визначають конкретний підтип.

Тому логіка спеціалізованої поведінки залишається у відповідних підкласах.

## Потокова обробка даних

Для лабораторної роботи №4 створено клас:

    ProjectTaskQueries

Він містить п'ять окремих Stream API-запитів та окремий метод пошуку.

Кожен запит винесено в іменований метод з одним зрозумілим результатом.

### 1. Відбір через filter

Метод:

    unfinishedTasks(List<ProjectTask> tasks)

повертає невиконані задачі.

Порядок операцій:

    stream()
    → filter(...)
    → toList()

Основна операція:

    .filter(task -> !task.isDone())

Результатом є список `List<ProjectTask>`.

### 2. Перетворення через map

Метод:

    taskTitles(List<ProjectTask> tasks)

перетворює об'єкти задач на їхні назви.

Порядок операцій:

    stream()
    → map(ProjectTask::getTitle)
    → toList()

Основна операція:

    .map(ProjectTask::getTitle)

Результатом є:

    List<String>

### 3. Групування через groupingBy

Метод:

    hoursByAssignee(List<ProjectTask> tasks)

групує задачі за виконавцем і обчислює сумарну кількість годин для кожного виконавця.

Порядок операцій:

    stream()
    → groupingBy(...)
    → summingDouble(...)
    → Map

Використовується:

    Collectors.groupingBy(
        ProjectTask::getAssignee,
        Collectors.summingDouble(
            ProjectTask::getEstimateHours
        )
    )

Результат має тип:

    Map<String, Double>

Наприклад:

    Іван   → 12.5
    Олена  → 12.0
    Андрій → 5.5

### 4. Зведена статистика

Метод:

    estimateStatistics(List<ProjectTask> tasks)

використовує:

    Collectors.summarizingDouble(
        ProjectTask::getEstimateHours
    )

Результат має тип:

    DoubleSummaryStatistics

Він містить:

- кількість елементів;
- суму;
- мінімальне значення;
- максимальне значення;
- середнє значення.

Для порожнього списку повертається порожня статистика з нульовою кількістю елементів.

### 5. Top-N зі складеним компаратором

Метод:

    topByPriority(List<ProjectTask> tasks, int n)

повертає перші `N` задач після сортування.

Порядок операцій:

    stream()
    → sorted(...)
    → limit(n)
    → toList()

Основний критерій:

    ProjectTask::evaluatePriority

Пріоритет сортується за спаданням за допомогою:

    reversed()

Другорядний критерій:

    ProjectTask::getEstimateHours

Він додається через:

    thenComparing(...)

Таким чином, компаратор має структуру:

    основний критерій:
        evaluatePriority, за спаданням

    другорядний критерій:
        estimateHours

Після сортування застосовується:

    limit(n)

Для від'ємного `N` метод викидає `IllegalArgumentException`.

Для `N = 0` повертається порожній список.

Якщо `N` більший за кількість задач, повертаються всі доступні задачі.

## Пошук через Optional

Окремий метод:

    findByTitle(List<ProjectTask> tasks, String title)

виконує пошук задачі за назвою.

Порядок операцій:

    stream()
    → filter(...)
    → findFirst()
    → Optional<ProjectTask>

Якщо задача знайдена, повертається `Optional` з результатом.

Якщо задача відсутня або вхідна колекція порожня, повертається:

    Optional.empty()

При дублюванні назв повертається перша знайдена задача.

## Незмінність вхідної колекції

Stream API-запити не змінюють вхідну колекцію.

Зокрема, для `topByPriority()` використовується:

    stream().sorted(...)

а не сортування самої вхідної колекції через `sort()`.

Окремий тест `shouldNotMutateInputCollection()` перевіряє, що після виконання всіх запитів початкова колекція не змінилася.

## Основні статистичні обчислення

Попередню циклову реалізацію збережено у класі:

    TaskMetrics

Для потокової реалізації створено:

    StreamTaskMetrics

Потокова реалізація використовує:

- `mapToDouble(...).sum()`;
- `mapToInt(...).average()`;
- `filter(...).count()`.

Наприклад, сумарна оцінка годин обчислюється через:

    tasks.stream()
        .mapToDouble(ProjectTask::getEstimateHours)
        .sum();

Середній поліморфний пріоритет:

    tasks.stream()
        .mapToInt(ProjectTask::evaluatePriority)
        .average()
        .orElse(0.0);

Кількість виконаних задач:

    tasks.stream()
        .filter(ProjectTask::isDone)
        .count();

## Порівняння циклової та потокової реалізацій

| Циклова реалізація | Stream API |
|---|---|
| `for` + накопичення годин | `mapToDouble().sum()` |
| `for` + накопичення пріоритетів | `mapToInt().average()` |
| `for` + перевірка `isDone()` | `filter().count()` |
| цикл з відбором задач | `filter()` |
| цикл з отриманням назв | `map()` |
| ручне групування за виконавцем | `Collectors.groupingBy()` |
| ручне накопичення статистики | `Collectors.summarizingDouble()` |
| ручне сортування та обмеження | `sorted()` + `limit()` |
| ручний пошук | `filter()` + `findFirst()` + `Optional` |

Циклова реалізація `TaskMetrics` збережена як контрольна.

Потокова реалізація `StreamTaskMetrics` перевіряється разом із попередньою реалізацією.

Окремий тест `TaskMetricsEquivalenceTest` перевіряє:

- сумарну оцінку годин;
- середній пріоритет;
- кількість виконаних задач;
- еквівалентність готового контрольного звіту.

## Збереження формату звіту

Формат зовнішнього звіту не змінювався.

Для однакових вхідних даних циклова та потокова реалізації формують однакові показники.

Еквівалентність готових звітів перевіряється автоматичним тестом:

    shouldProduceEquivalentControlReports()

Тому перехід на Stream API не змінює зовнішню поведінку програми.

## equals() та hashCode()

Для `ProjectTask` реалізовано:

- `equals()`;
- `hashCode()`.

Рівність враховує значення полів та конкретний клас об'єкта.

Тести перевіряють:

- рівність однакових задач;
- однаковий `hashCode()` для рівних об'єктів;
- відсутність дублювання в `HashSet`;
- відмінність задач різних підтипів.

## Успадкування та композиція

Успадкування використано для `DefaultProjectTask`, `DevelopmentTask` і `TestingTask`, оскільки всі вони є різновидами `ProjectTask`.

Підтипи мають спільний стан та інваріанти, але можуть реалізовувати різну поведінку `evaluatePriority()`.

Тому між класами існує відношення `is-a`.

Композиція могла б бути використана для окремого об'єкта-стратегії оцінювання пріоритету, але в межах цієї предметної області спадкування є природним способом представити різновиди задач.

## Зворотна сумісність

Для збереження сумісності з попередніми лабораторними роботами:

- формат CSV не змінено;
- правила валідації збережено;
- попередні тести збережено;
- зовнішній формат звіту збережено;
- стандартну поведінку попередньої моделі збережено через `DefaultProjectTask`.

Метод:

    ProjectTask.fromCsv(...)

продовжує працювати з тим самим п'ятикомпонентним форматом:

    title;assignee;estimateHours;priority;done

## Тестування

У проєкті збережено всі попередні тести та додано нові тести для Stream API.

Поточний результат:

    Tests run: 51, Failures: 0, Errors: 0, Skipped: 0

Тести охоплюють:

- `ProjectTask`;
- `DefaultProjectTask`;
- `DevelopmentTask`;
- `TestingTask`;
- `TaskKind`;
- поліморфний `evaluatePriority()`;
- `equals()` та `hashCode()`;
- `HashSet`;
- CSV-парсинг;
- `filter`;
- `map`;
- `groupingBy`;
- `summarizingDouble`;
- top-N;
- `Optional` пошук;
- дублікати;
- порожні колекції;
- граничні значення `N`;
- відсутній результат пошуку;
- незмінність вхідної колекції;
- еквівалентність циклової та потокової реалізацій;
- еквівалентність контрольного звіту.

## Статичний аналіз

Під час:

    ./mvnw clean verify

виконується SpotBugs.

Остання локальна перевірка завершилася:

    BugInstance size is 0
    Error size is 0
    No errors/warnings found

Результат Maven:

    BUILD SUCCESS

## Виконуваний JAR

Після виконання:

    ./mvnw -B clean package

створюється JAR:

    target/maven-actions-hello-4.0.0.jar

Для запуску:

    java -jar target/maven-actions-hello-4.0.0.jar

Приклад:

    java -jar target/maven-actions-hello-4.0.0.jar \
        --input data/input.csv

## Запуск

### Maven Wrapper

macOS/Linux:

    ./mvnw -B clean verify

Windows:

    mvnw.cmd -B clean verify

### Тести

    ./mvnw -B test

### Запуск програми

    java -cp target/classes Main --input data/input.csv

### Запис звіту у файл

    java -cp target/classes Main \
        --input data/input.csv \
        --output report.txt

### Довідка

    java -cp target/classes Main --help

### Версія

    java -cp target/classes Main --version

Очікувана версія:

    4.0.0

## Continuous Integration

Для проєкту налаштовано GitHub Actions.

CI передбачає перевірку на:

- Ubuntu;
- Windows;
- macOS.

Для збірки використовується Java 21 та Maven Wrapper.

На кожній операційній системі виконується:

    clean verify

Тобто CI перевіряє компіляцію, автоматичні тести, SpotBugs та створення JAR.

JAR публікується як GitHub Actions artifact для кожної операційної системи.

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
    │   │       ├── ProjectTaskQueries.java
    │   │       ├── ReportFormatter.java
    │   │       ├── StreamTaskMetrics.java
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
    │           ├── ProjectTaskQueriesTest.java
    │           ├── ProjectTaskTest.java
    │           ├── StreamTaskMetricsTest.java
    │           ├── TaskKindTest.java
    │           ├── TaskMetricsEquivalenceTest.java
    │           ├── TaskMetricsTest.java
    │           └── TaskParserTest.java
    │
    ├── .gitignore
    ├── pom.xml
    ├── README.md
    ├── REPORT.md
    ├── mvnw
    └── mvnw.cmd

Старий `Task.java` збережено як код попередніх лабораторних робіт. Поточна робоча логіка використовує `ProjectTask` та його підтипи.

## Версіювання

Для лабораторної роботи №4 використовується версія:

    4.0.0

Запланований Git-тег:

    v4.0.0

Поточна гілка лабораторної роботи:

    java/lab04

## GitHub

Репозиторій:

    https://github.com/innasarhan/kzp-project-task-manager-sarhan

Для лабораторної роботи №4 документація, Issue та Pull Request будуть оновлені після завершення локальної перевірки та налаштування CI.

## Документація коду

Для створених або змінених публічних класів, конструкторів та методів додано Javadoc.

Зокрема документовано:

- `ProjectTaskQueries`;
- `StreamTaskMetrics`;
- `TaskMetricsEquivalenceTest`;
- `ProjectTaskQueriesTest`;
- основні класи доменної моделі та їхні методи.

## Академічна доброчесність

Під час виконання роботи використовувалися GitHub Copilot та ChatGPT як допоміжні інструменти для консультацій, роботи з кодом, тестами та документацією.

Усі прийняті зміни перевіряються локально за допомогою Maven та JUnit.

Фінальна реалізація додатково перевіряється статичним аналізом SpotBugs та Continuous Integration.