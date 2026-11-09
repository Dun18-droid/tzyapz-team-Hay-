# Стартовий проєкт автотестів ТЗЯПЗ (JUnit 5 + Selenium WebDriver 4)

Мінімальний Maven-проєкт для спринта 4 «Автоматизація» і спринта 5 «Регресія». Містить робочий
`pom.xml`, клас-зразок `Calculator` з юніт-тестами JUnit 5 та один UI-тест Selenium WebDriver
(smoke-перевірка головної сторінки demo-застосунку). Команда клонує його, додає модуль бізнес-логіки
свого продукту й пише власні тести.

## 1. Вимоги до робочого місця

| Що | Версія | Як перевірити |
|---|---|---|
| JDK (Temurin) | 17 | `java -version` → `openjdk version "17.…"` |
| Apache Maven | 3.9 (працює і 3.8) | `mvn -v` → рядок `Java version: 17.…` |
| IntelliJ IDEA Community Edition | актуальна | меню Help → About |
| Google Chrome (або Firefox) | актуальна | драйвер браузера встановлювати НЕ потрібно — його завантажує Selenium Manager |
| Git | будь-яка | `git --version` |

Інтернет потрібен для першого збирання (Maven завантажує залежності у `~/.m2`) і для UI-тестів
(вони відкривають публічний demo-застосунок).

## 2. Імпорт у IntelliJ IDEA

1. `File → Open…` → виберіть папку з `pom.xml` (саме папку, не файл) → `Open as Project`.
2. Дочекайтеся, поки IDEA проіндексує проєкт і завантажить залежності (індикатор унизу праворуч).
3. `File → Project Structure → Project → SDK` має бути **17**. Якщо у списку немає JDK 17 —
   `Add SDK → Download JDK… → Eclipse Temurin 17`.
4. Відкрийте `src/test/java/ua/kpi/tzyapz/starter/CalculatorTest.java`, натисніть зелену стрілку
   біля назви класу → тести мають пройти (7 зелених).

## 3. Запуск тестів

```bash
# юніт-тести (UI-тести з тегом "ui" за замовчуванням виключені)
mvn test

# юніт- + UI-тести (потрібен встановлений Chrome; браузер запускається у headless-режимі)
mvn test -DexcludedGroups=none

# лише один клас або один метод
mvn test -Dtest=CalculatorTest
mvn test -Dtest=CalculatorTest#divideByZeroThrows
mvn test -DexcludedGroups=none -Dtest=SmokeUiTest

# чисте збирання «з нуля» (так робить рецензент і аудитор)
mvn clean test
```

Очікуваний результат для стартового проєкту:

```
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Звіти Surefire лежать у `target/surefire-reports/`: `*.txt` — короткий підсумок по класу,
`TEST-*.xml` — повний перелік тестів із часом і текстом помилок. Саме ці файли (або їхній
знімок у README/протоколі) є «протоколом запуску» інкременту спринта 4.

## 4. Структура папок

```
starter/
├── pom.xml                              # залежності: junit-jupiter 5.10, selenium-java 4.21; Surefire
├── README.md
├── .gitignore
└── src/
    ├── main/java/ua/kpi/tzyapz/starter/
    │   └── Calculator.java              # клас-зразок (сюди ж або поруч кладуть модуль продукту)
    └── test/java/ua/kpi/tzyapz/starter/
        ├── CalculatorTest.java          # зразок юніт-тестів: @Test, @BeforeEach, @ParameterizedTest, assertAll
        └── ui/
            └── SmokeUiTest.java         # зразок UI-тесту: ChromeOptions headless, WebDriverWait, @Tag("ui")
```

Правило: тести лежать у `src/test/java` у **тому самому пакеті**, що й клас, який вони перевіряють;
UI-тести — у підпакеті `ui`. Ім'я тест-класу = ім'я класу + `Test`.

## 5. Як підключити модуль свого продукту

Модуль бізнес-логіки продукту лежить у пакеті `code/modules/<slug>/v1/` (наприклад
`code/modules/shopline/v1/`) і є самостійним Maven-проєктом з таким самим `pom.xml`
(`artifactId` = `<slug>-module`). Два рівноцінні варіанти:

**Варіант А (рекомендований): модуль стає робочим проєктом команди.**
1. Скопіюйте папку `code/modules/<slug>/v1/` у репозиторій команди (наприклад як `qa-<slug>/`).
2. Створіть у ній `src/test/java/ua/kpi/tzyapz/<slug>/` для юніт-тестів
   і `src/test/java/ua/kpi/tzyapz/<slug>/ui/` для WebDriver-тестів.
3. Скопіюйте туди `SmokeUiTest.java` як заготовку UI-тесту (змініть пакет) та цей `README.md`
   і `.gitignore`. У спринті 5 ви замініте `src/main/java` на версію v2 — тести лишаться на місці.

**Варіант Б: класи модуля додаються у стартовий проєкт.**
1. Скопіюйте `code/modules/<slug>/v1/src/main/java/ua/kpi/tzyapz/<slug>/` у
   `starter/src/main/java/ua/kpi/tzyapz/<slug>/`.
2. Тести пишіть у `starter/src/test/java/ua/kpi/tzyapz/<slug>/`.
3. За бажанням змініть `artifactId` і `name` у `pom.xml` на назву команди.

В обох варіантах збірка перевіряється командою `mvn clean test` з кореня проєкту.

## 6. Як вставити тест, експортований із Selenium IDE

Selenium IDE (`Export → Java JUnit`) генерує клас у стилі JUnit 4, а у проєкті підключено JUnit 5.
Після вставки файлу у `src/test/java/ua/kpi/tzyapz/<slug>/ui/` зробіть такі заміни:

| Було (експорт IDE, JUnit 4) | Стало (JUnit 5) |
|---|---|
| `package com.example;` | `package ua.kpi.tzyapz.<slug>.ui;` |
| `import org.junit.Test;` | `import org.junit.jupiter.api.Test;` |
| `import org.junit.Before;` / `@Before` | `import org.junit.jupiter.api.BeforeEach;` / `@BeforeEach` |
| `import org.junit.After;` / `@After` | `import org.junit.jupiter.api.AfterEach;` / `@AfterEach` |
| `import static org.junit.Assert.*;` | `import static org.junit.jupiter.api.Assertions.*;` |
| `assertThat(x, is("текст"))` (Hamcrest) | `assertEquals("текст", x)` |
| `driver = new ChromeDriver();` | `driver = new ChromeDriver(options);` з `--headless=new` (див. `SmokeUiTest`) |
| невикористані імпорти (`FirefoxDriver`, `RemoteWebDriver`, `Actions`, `Alert`, `Keys`…) | видалити |
| — | додати `@Tag("ui")` над класом |

Далі замініть «сліпі» `Thread.sleep` і команди `wait for …` на `WebDriverWait` +
`ExpectedConditions`, а очікуваний результат — на явні `assert…`. Перевірте: `mvn test -DexcludedGroups=none -Dtest=<Клас>`.

## 7. Типові помилки

**`mvn` збирає старим JDK (помилки на кшталт `invalid target release: 17`, `release version 17 not supported`).**
Перевірте, який JDK бачить Maven:

```bash
mvn -v
# Java version: 1.8.0_… або 11.… → JAVA_HOME вказує не на 17
```

macOS / Linux:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)      # macOS
export JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64     # Linux (шлях може відрізнятися)
mvn -v
```
Windows (PowerShell, поточне вікно):
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.11.9-hotspot"
mvn -v
```
Щоб зміна була постійною, задайте змінну середовища `JAVA_HOME` у налаштуваннях системи
(Windows) або у `~/.zshrc` / `~/.bashrc`.

**`No tests were executed` або `Tests run: 0`.** Клас тесту не в `src/test/java`, метод не
позначений `@Test`, або ім'я класу не закінчується на `Test`/`Tests` (Surefire включає лише
`*Test`, `Test*`, `*Tests`, `*TestCase`).

**UI-тест не запускається (`Skipped` або взагалі не в списку).** Тег `ui` виключено за
замовчуванням — запускайте `mvn test -DexcludedGroups=none`.

**`SessionNotCreatedException` / `Unable to obtain driver`.** Selenium Manager не зміг
завантажити драйвер: перевірте інтернет і чи встановлений Chrome; у корпоративній мережі із
проксі — задайте `HTTPS_PROXY`. Кеш драйверів — `~/.cache/selenium`.

**`NoSuchElementException` / `TimeoutException`.** Елемент ще не з'явився або локатор
неправильний: перевірте локатор у DevTools (`Ctrl+F` у вкладці Elements приймає CSS та XPath)
і додайте `WebDriverWait` перед пошуком.

**Тест «іноді падає».** Сторінка перемальовується після дії (SPA): чекайте на стан, який справді
перевіряєте (текст, кількість елементів, URL), а не на «щось з'явилося».

**Кирилиця у виводі перетворилася на `????`.** У `pom.xml` уже задано `UTF-8`; для консолі
Windows виконайте `chcp 65001` або запускайте тести з IDEA.

**`BigDecimal` — `expected: <120> but was: <120.00>`.** `assertEquals` порівнює `BigDecimal`
разом зі шкалою (кількістю знаків); задайте очікуване значення з тією ж шкалою
(`new BigDecimal("120.00")`) або порівнюйте через `compareTo`.
