# СтейНау — автотести модуля бронювання (Спринт 4, ТЗЯПЗ)

Репозиторій автотестів команди Групи E: юніт-тести JUnit 5 для модуля бізнес-логіки «СтейНау» (v1) і
UI-тести Selenium WebDriver 4 для demo-застосунку. Інкремент **Досьє якості v4**.

## 1. Вимоги до робочого місця

| Що | Версія | Перевірка |
|---|---|---|
| JDK (Temurin) | 17 | `java -version` |
| Apache Maven | 3.9 (працює і 3.8) | `mvn -v` — рядок `Java version: 17.…` |
| Google Chrome | актуальна | драйвер ставити не потрібно — його завантажує Selenium Manager |
| Git | будь-яка | `git --version` |

Інтернет потрібен для першої збірки (залежності Maven) і для UI-тестів (публічний demo
`https://automationintesting.online`).

## 2. Збірка і запуск

```bash
git clone https://github.com/Dun18-droid/tzyapz-team-Hay-.git
cd tzyapz-team-Hay-

mvn clean test                          # юніт-тести (UI з тегом "ui" за замовчуванням виключені)
mvn clean test -DexcludedGroups=none    # юніт- + UI-тести (потрібен Chrome, браузер у headless-режимі)

mvn test -Dtest=AvailabilityTest                          # один клас
mvn test -DexcludedGroups=none -Dtest=AdminLoginTest      # один UI-клас
```

Звіти Surefire — `target/surefire-reports/` (`*.txt` — підсумок по класу, `TEST-*.xml` — усі тести).

**Очікуваний результат юніт-набору на модулі v1: BUILD FAILURE.** Частина тестів «червона» свідомо: вони
фіксують закладені дефекти модуля (BUG-008, 009, 010, 011, 014, 015) і мають лишатися червоними до
перевірки на v2 у спринті 5. Зведення — у [протоколі запуску](docs/sprint4/surefire-summary.md).

## 3. Структура

```
├── pom.xml                      # JDK 17, JUnit 5.10.2, Selenium 4.21.0, Surefire (група "ui" виключена)
├── README.md
├── docs/
│   ├── sprint4/
│   │   ├── coverage-matrix.md   # BL-вимога → правило → тести → статус → рецензент
│   │   ├── ui-scenarios.md      # TC-xx → кроки, локатори, очікування, тест-метод
│   │   ├── surefire-summary.md  # протокол запуску
│   │   ├── bug-reports.md       # дефекти модуля BUG-008…011, 014, 015 (мітка module)
│   │   ├── bug-reports-demo.md  # дефекти demo-застосунку (BUG-013)
│   │   ├── code-review.md       # зауваження рецензентів
│   │   ├── task-board.md        # розподіл задач
│   │   ├── retro.md             # протокол ретроспективи
│   │   └── starter-guide.md     # довідка стартового проєкту
│   └── sprint1…sprint3/         # артефакти попередніх спринтів
├── selenium-ide/
│   ├── staynow.side             # проєкт Selenium IDE (TC-021)
│   └── export/                  # експорт у Java — лише довідково, не збирається
└── src/
    ├── main/java/ua/kpi/tzyapz/staynow/        # модуль v1: BookingValidator, PriceCalculator, Availability, BookingError
    └── test/java/ua/kpi/tzyapz/staynow/
        ├── BookingValidatorTest.java   # BL-10, BL-20
        ├── PriceCalculatorTest.java    # BL-30, BL-40, BL-50
        ├── AvailabilityTest.java       # BL-60, BL-70
        └── ui/                         # WebDriver: BaseUiTest, ContactFormTest, AdminLoginTest
```

## 4. Хто що написав

| Автор | Юніт-тести | Інше |
|---|---|---|
| Мукоїд Е. | `BookingValidatorTest` (7 методів), BUG-008, BUG-009 | матриця покриття, UI-сценарії, рецензування `PriceCalculatorTest` |
| Андрущенко К. | `PriceCalculatorTest` (7 методів), BUG-010, BUG-011, BUG-013 | інфраструктура, UI-тести, Selenium IDE (TC-021), рецензування `AvailabilityTest` |
| Дятлов М. | `AvailabilityTest` (7 методів), BUG-014, BUG-015 | README, протокол запуску, рецензування `BookingValidatorTest`, ретро |

## 5. Норматив (команда з 3 осіб)

| Показник | Норматив | Факт |
|---|---|---|
| Юніт-тестів JUnit 5 (параметризований метод = 1) | ≥ 12 | 21 |
| Юніт-тестів на особу | ≥ 3 | 7 / 7 / 7 |
| UI-сценаріїв WebDriver | ≥ 4 | 5 (TC-021, 023, 024, 036, 037) |
| Дефектів модуля (звіт + тест) | ≥ 3 | 6 |
| Сценаріїв Selenium IDE | 1–2 | 1 (TC-021) |

## 6. Дефекти модуля v1

| ID | Вимога | Severity | Дефект | Тест |
|---|---|---|---|---|
| BUG-008 | BL-10 | Normal | 30 ночей відхиляється (`< 30` замість `≤ 30`) | `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected` |
| BUG-009 | BL-10 | Major | дата заїзду в минулому приймається | `BookingValidatorTest.checkInBeforeTodayIsRejected` |
| BUG-010 | BL-30 | Major | вихідні — субота й неділя замість п'ятниці й суботи | `PriceCalculatorTest.weekendNightIsFridayAndSaturday` та ще 3 |
| BUG-011 | BL-50 | Minor | округлення DOWN замість HALF_UP | `PriceCalculatorTest.nightPriceIsRoundedHalfUp` |
| BUG-014 | BL-60 | Major | рівно 7 днів до заїзду — плата 50 % замість 0 | `AvailabilityTest.cancellationFeeAtBoundaries`, `refundAtBoundaries` |
| BUG-015 | BL-70 | Major | виїзд у день заїзду іншого гостя вважається перетином | `AvailabilityTest.isAvailableForIntervals`, `bookingFreeRoomIsRegistered` |

Severity — за шкалою спринта 3 (4.12); усі дефекти мають Priority P3. Повні звіти — [bug-reports.md](docs/sprint4/bug-reports.md).

## 7. Що не покрито і чому

- `INVALID_DATE_FORMAT` (null-дати у `BookingValidator`): правило є лише в SRS 1.1 з позначкою «потребує
  підтвердження замовником»; в оригінальній SRS_04 і в модулі v1 його немає. Дефект на непідтверджену
  вимогу не оформлюємо.
- Сценарії бронювання (TC-007…TC-020) не автоматизовано: сторінка `/reservation/{id}` на demo станом на
  08.11.2026 не завантажується.

## 8. Ризики

**Selenium IDE.** `selenium-ide/staynow.side` — записаний сценарій TC-021 (21 команда: `open`,
`setWindowSize`, 2 × `waitForElementVisible`, 6 × `click`, 10 × `executeScript`, фінальний `assertText` із
текстом подяки). Чому так: розширення Selenium IDE прибрано з Chrome Web Store, а в Edge його вимикають
(перехід на Manifest V3), тому запис виконано у Firefox (Selenium IDE 3.17.4). У сучасному Firefox у цій версії
не працюють команди `type` і `send keys` (`initKeyEvent is not a function`), тому поля заповнюються через
`executeScript`; перед ними стоїть `waitForElementVisible | id=name | 15000` — без очікування скрипт падав
приблизно в половині запусків, бо React ще не встигав відмалювати форму. Запис і його експорт не можна
запускати як тест проєкту:

1. Поля форми заповнено командами `executeScript` (значення виставляє JavaScript), а не `type`. Експорт
   `selenium-ide/export/TC021contactformvalidmessageTest.java` цього не витримав: значення полів стали іменами
   змінних у `vars.put("t3.guest@example.com", …)`, а поле Name не заповнюється зовсім (код лише чекає на його
   появу), хоча фінальна перевірка очікує «Thanks for getting in touch T3-Guest!». Тобто експорт не пройшов би
   й після компіляції.
2. Експорт написано в стилі JUnit 4 і Hamcrest (`@Before`, `org.junit.Test`, `assertThat`), він відкриває
   `FirefoxDriver` без headless і використовує старе очікування `new WebDriverWait(driver, 15)` (у Selenium 4
   час очікування задається через `Duration`). У проєкті — JUnit 5 і Chrome.
3. Відтворення залежить від спільного demo: верстка чи дані можуть змінитися, тож сценарій перед демо
   відтворюють у Selenium IDE ще раз.

Тому експорт лежить поза `src/` і не збирається — він довідковий. Робочий тест TC-021 —
`ContactFormTest.validMessageShowsThanks`.

**Спільний demo.** UI-тести ходять на публічний demo, яким користуються й інші: дані можуть змінюватися,
сайт може відповідати повільно або бути недоступним. Тому очікування — 15 с, а тести не рахують кількість номерів і повідомлень. Падіння UI-тесту без змін у коді спершу перевіряємо на самому demo.

**Клік по Submit.** У `ContactFormTest` кнопку Submit іноді перекриває інший елемент
(`ElementClickInterceptedException`); тест обходить це кліком через JavaScript. Причину перекриття не з'ясовано.

**Chrome і chromedriver.** Драйвер завантажує Selenium Manager, але якщо в системі вже є старий
`chromedriver` у `PATH` (наприклад `/usr/bin/chromedriver` з пакета дистрибутива), він береться замість
завантаженого й UI-тести падають із `SessionNotCreatedException` (версія драйвера не збігається з Chrome).
Лікування: `which -a chromedriver`, перейменувати старий файл і очистити `~/.cache/selenium`.

## 9. Висновок про стан модуля v1

**Модуль `staynow` v1 не готовий до випуску.** 76 запусків тестів трьох класів модуля дали 63 passed і
13 red; усі 13 червоних пояснюються шістьма дефектами з розділу 6 (протокол — [surefire-summary.md](docs/sprint4/surefire-summary.md)).

| Клас | Вимоги | Що працює | Що ні |
|---|---|---|---|
| `BookingValidator` | BL-10, BL-20 | перевірка email, телефону і кількості гостей (BL-20); решта меж ночей | 30 ночей відхиляється (BUG-008, Normal); дата заїзду в минулому приймається (BUG-009, Major) |
| `PriceCalculator` | BL-30, BL-40, BL-50 | сезонні коефіцієнти на межах сезонів | вихідні рахуються як субота й неділя замість п'ятниці й суботи, тож п'ятнична ніч без надбавки, а недільна з нею (BUG-010, Major); округлення вниз замість HALF_UP (BUG-011, Minor) |
| `Availability` | BL-60, BL-70 | пороги 3, 6 і 8 днів скасування; справжні перетини дат | рівно 7 днів дає плату 50 % замість 0 (BUG-014, Major); виїзд у день заїзду іншого гостя блокує бронювання (BUG-015, Major) |

Що це означає для продукту: чотири дефекти Major зачіпають гроші й доступність номерів — неправильна ціна
п'ятниці й неділі, втрата половини вартості при скасуванні за тиждень, неможливість забронювати номер у день
виїзду попереднього гостя, прийняття заїзду в минулому; Normal і Minor дають розходження в одну ніч або в
копійках. Дефекти локалізовані: у кожному випадку це неправильна умова, константа або режим округлення в
одному методі, тож виправлення очікується точковим.

Рекомендація: v1 не випускати. У спринті 5 той самий набір запускається на v2: тест, що був червоним через
дефект, стає зеленим (дефект closed) або лишається червоним (reopened), а нові червоні тести вказують на регресію.

Обмеження висновку: він стосується правил BL-10…BL-70, перевірених юніт-тестами. Правило
`INVALID_DATE_FORMAT` не підтверджено замовником (розділ 7), а сценарії бронювання через інтерфейс не
автоматизовано, тому про них висновку немає.
