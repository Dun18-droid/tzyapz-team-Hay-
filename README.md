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

| ID | Вимога | Дефект | Тест |
|---|---|---|---|
| BUG-008 | BL-10 | 30 ночей відхиляється (`< 30` замість `≤ 30`) | `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected` |
| BUG-009 | BL-10 | дата заїзду в минулому приймається | `BookingValidatorTest.checkInBeforeTodayIsRejected` |
| BUG-010 | BL-30 | вихідні — субота й неділя замість п'ятниці й суботи | `PriceCalculatorTest.weekendNightIsFridayAndSaturday` та ще 3 |
| BUG-011 | BL-50 | округлення DOWN замість HALF_UP | `PriceCalculatorTest.nightPriceIsRoundedHalfUp` |
| BUG-014 | BL-60 | рівно 7 днів до заїзду — плата 50 % замість 0 | `AvailabilityTest.cancellationFeeAtBoundaries`, `refundAtBoundaries` |
| BUG-015 | BL-70 | виїзд у день заїзду іншого гостя вважається перетином | `AvailabilityTest.isAvailableForIntervals`, `bookingFreeRoomIsRegistered` |

Повні звіти — [bug-reports.md](docs/sprint4/bug-reports.md).

## 7. Що не покрито і чому

- `INVALID_DATE_FORMAT` (null-дати у `BookingValidator`): правило є лише в SRS 1.1 з позначкою «потребує
  підтвердження замовником»; в оригінальній SRS_04 і в модулі v1 його немає. Дефект на непідтверджену
  вимогу не оформлюємо.
- Сценарії бронювання (TC-007…TC-020) не автоматизовано: сторінка `/reservation/{id}` на demo станом на
  08.11.2026 не завантажується.

## 8. Ризики

**Selenium IDE.** Запис у `selenium-ide/staynow.side` і його експорт у `selenium-ide/export/` не можна
вважати готовим тестом. Експорт є чернеткою: він у стилі JUnit 4 і Hamcrest, відкриває Firefox без headless,
використовує `new WebDriverWait(driver, 15)` (такого конструктора немає в Selenium 4), а вставлення значень
через `executeScript` перетворилося на присвоєння змінним замість заповнення полів (поле Name в експорті
взагалі відсутнє). Тому експорт лежить поза `src/` і не збирається; робочий тест TC-021 — це
`ContactFormTest.validMessageShowsThanks`. Сам запис також містить дубльовані команди й клік по
`css=.justify-content-center` без користі — його варто чистити перед відтворенням.

**Спільний demo.** UI-тести ходять на публічний demo, яким користуються й інші: дані можуть змінюватися,
сайт може відповідати повільно або бути недоступним. Тому очікування — 15 с, а тести не рахують кількість номерів і повідомлень. Падіння UI-тесту без змін у коді спершу перевіряємо на самому demo.

**Клік по Submit.** У `ContactFormTest` кнопку Submit іноді перекриває інший елемент
(`ElementClickInterceptedException`); тест обходить це кліком через JavaScript. Причину перекриття не з'ясовано.

**Chrome і chromedriver.** Драйвер завантажує Selenium Manager, але якщо в системі вже є старий
`chromedriver` у `PATH` (наприклад `/usr/bin/chromedriver` з пакета дистрибутива), він береться замість
завантаженого й UI-тести падають із `SessionNotCreatedException` (версія драйвера не збігається з Chrome).
Лікування: `which -a chromedriver`, перейменувати старий файл і очистити `~/.cache/selenium`.
