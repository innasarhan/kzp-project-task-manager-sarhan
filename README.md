# Project Task Manager

## Опис проєкту

Java-проєкт для читання, перевірки та обробки списку проєктних задач.

**Предметна область:** Проєктні задачі
**Варіант:** 22
**Java:** 21
**Система збірки:** Maven

У лабораторній роботі №2 доменну модель було рефакторено: замість простого класу `Task` використовується сутність `ProjectTask` з інкапсульованим незмінним станом.

## Формат вхідних даних

Один запис на рядок:

```text
title;assignee;estimateHours;priority;done
```

Поля:

- `title` — назва задачі;
- `assignee` — виконавець;
- `estimateHours` — оцінка тривалості задачі в годинах;
- `priority` — пріоритет;
- `done` — ознака виконання (`true` або `false`).

Роздільник полів:

```text
;
```

Кодування:

```text
UTF-8
```

Приклад:

```text
Розробити API;Іван;12.5;1;true
Створити дизайн;Олена;8.0;2;false
Написати тести;Андрій;5.5;3;true
```

Невалідні записи не додаються до списку задач і не впливають на статистику. Для них виводиться номер рядка та причина помилки.

## Основна функціональність

Програма обчислює:

1. кількість валідних задач;
2. сумарну оцінку годин;
3. середній пріоритет;
4. кількість виконаних задач.

Для поточного `data/input.csv` результат:

```text
Кількість валідних задач: 3
Сумарна оцінка годин: 26.00
Середній пріоритет: 2.00
Кількість виконаних задач: 2
```

## Доменна модель

### ProjectTask

`ProjectTask` є основною сутністю предметної області.

Клас містить:

- `title`;
- `assignee`;
- `estimateHours`;
- `priority`;
- `done`.

Поля є `private final`.

Конструктор перевіряє:

- `title` не є `null` і не є порожнім;
- `assignee` не є `null` і не є порожнім;
- `estimateHours >= 0`;
- `priority >= 0`.

Для створення задачі з CSV-рядка використовується:

```java
ProjectTask.fromCsv(...)
```

Також реалізовано `toString()` з використанням `Locale.ROOT`.

### EstimatePriority

Для пари значень оцінки та пріоритету використовується immutable `record`:

```java
EstimatePriority(double estimateHours, int priority)
```

## Структура проєкту

```text
.
├── .github/
│   └── workflows/
│       └── ci.yml
├── data/
│   └── input.csv
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── EstimatePriority.java
│   │       ├── HelloWorld.java
│   │       ├── Main.java
│   │       ├── ProjectTask.java
│   │       ├── ReportFormatter.java
│   │       ├── Task.java
│   │       ├── TaskMetrics.java
│   │       └── TaskParser.java
│   └── test/
│       └── java/
│           ├── EstimatePriorityTest.java
│           ├── ProjectTaskFromCsvTest.java
│           ├── ProjectTaskTest.java
│           ├── TaskMetricsTest.java
│           └── TaskParserTest.java
├── .gitignore
├── pom.xml
├── README.md
├── REPORT.md
├── mvnw
└── mvnw.cmd
```

Старий `Task.java` збережено як код попередньої лабораторної роботи. Поточна робоча логіка використовує `ProjectTask`.

## Запуск

### Maven Wrapper

macOS/Linux:

```bash
./mvnw -B clean verify
```

Windows:

```cmd
mvnw.cmd -B clean verify
```

### JUnit-тести

```bash
./mvnw -B test
```

Поточна кількість автоматичних тестів:

```text
21
```

### Запуск програми

```bash
java -cp target/classes Main
```

### Запуск із власним CSV

```bash
java -cp target/classes Main --input data/input.csv
```

### Запис звіту у файл

```bash
java -cp target/classes Main --input data/input.csv --output report.txt
```

### Довідка

```bash
java -cp target/classes Main --help
```

### Версія

```bash
java -cp target/classes Main --version
```

## Виконуваний JAR

Після виконання:

```bash
./mvnw -B clean package
```

JAR знаходиться у:

```text
target/maven-actions-hello-0.1.0.jar
```

Запуск:

```bash
java -jar target/maven-actions-hello-0.1.0.jar
```

Приклад запуску з CSV:

```bash
java -jar target/maven-actions-hello-0.1.0.jar --input data/input.csv
```

## Тестування та статичний аналіз

Повна перевірка:

```bash
./mvnw -B clean verify
```

Під час `verify` виконуються:

- компіляція;
- JUnit-тести;
- SpotBugs;
- створення JAR.

Поточний результат локальної перевірки:

```text
Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
BugInstance size is 0
Error size is 0
No errors/warnings found
BUILD SUCCESS
```

## GitHub Actions

CI успадковано з лабораторної роботи №1.

Workflow перевіряє проєкт на:

- Ubuntu;
- Windows;
- macOS.

Для запуску використовується Java 21 та Maven Wrapper.

## GitHub

Репозиторій:

https://github.com/innasarhan/kzp-project-task-manager-sarhan

Поточна гілка лабораторної роботи №2:

```text
java/lab02
```

Для роботи створено Issues:

```text
#8  [ЛР2] Створити клас ProjectTask
#9  [ЛР2] Реалізувати валідацію ProjectTask
#10 [ЛР2] Додати record EstimatePriority
#11 [ЛР2] Додати тести для ProjectTask та EstimatePriority
#12 [ЛР2] Оновити README та REPORT
```

## Документація

Детальний опис виконання лабораторної роботи №2, стану до та після рефакторингу, тестування та результатів перевірки наведено у `REPORT.md`.

## Академічна доброчесність

Під час виконання роботи використовувалися GitHub Copilot та ChatGPT як допоміжні інструменти для консультацій, роботи з кодом, тестами та документацією.

Усі прийняті зміни перевірялися локально за допомогою Maven та JUnit.