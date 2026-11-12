# Звіти про дефекти модуля — Спринт 4, «СтейНау»

Трекер дефектів модуля бізнес-логіки `code/modules/staynow/v1` (позначка `module`). Шаблон — як у спринті 3 ([docs/sprint3/bug-reports.md](../sprint3/bug-reports.md)), нумерація продовжує його (BUG-008…). Джерело очікуваних значень — розділ BL [SRS 1.1](../sprint2/SRS-1.1.md); тести, що фіксують дефекти, лишаються «червоними» до перевірки на v2 (спринт 5). Матриця покриття — [coverage-matrix.md](coverage-matrix.md).

## Зведення

| Bug ID | Title | Severity | Priority | Вимога | Тест | Статус |
|---|---|---|---|---|---|---|
| [BUG-008](#bug-008) | BookingValidator.validate відхиляє бронювання рівно на 30 ночей | Normal | P3 | BL-10 | `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected` | New |
| [BUG-009](#bug-009) | BookingValidator.validate приймає дату заїзду, ранішу за today | Major | P3 | BL-10 | `BookingValidatorTest.checkInBeforeTodayIsRejected` | New |
| [BUG-010](#bug-010) | PriceCalculator.isWeekendNight вважає вихідними суботу й неділю замість п'ятниці й суботи | Major | P3 | BL-30 | `PriceCalculatorTest.weekendNightIsFridayAndSaturday`, `nightPriceFormula`, `totalSumsNightsAcrossSeasonBoundary`, `longStayDiscountStartsAtSevenNights` | New |
| [BUG-011](#bug-011) | PriceCalculator.nightPrice округлює вниз (DOWN) замість HALF_UP | Minor | P3 | BL-50 | `PriceCalculatorTest.nightPriceIsRoundedHalfUp` | New |

---

## BUG-008

**Title:** BookingValidator.validate відхиляє бронювання рівно на 30 ночей (NIGHTS_OUT_OF_RANGE)

**Bug ID:** BUG-008 **Мітка:** module **Reporter:** Мукоїд Е. **Date:** 08.11.2026 **Reviewer:** — **Status:** New
**Severity:** Normal **Priority:** P3
**Linked:** requirement BL-10; test `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected` (набір [2] «30, false»)

### Description
BL-10 (SRS 1.1) вимагає, щоб кількість ночей була від 1 до 30 **включно**. Модуль v1 повертає `NIGHTS_OUT_OF_RANGE` для 30 ночей, тобто відхиляє максимальне допустиме бронювання; верхню межу реалізовано як «< 30» замість «≤ 30». Значення 1 і 31 обробляються правильно.
Severity Normal (4.12): бронювання можливе, але не на всю допустиму тривалість — гість не може забронювати 30 ночей. Priority P3 (4.13): інших тестів не блокує.

### Steps to reproduce
1. Модуль `staynow` v1; створити `PriceCalculator calc = new PriceCalculator();`.
2. Викликати `calc.nightPrice(new BigDecimal("33.33"), LocalDate.of(2026, 6, 3))` — середа, високий сезон (×1,30), без надбавки за вихідну ніч.

### Actual result
`43.32`

### Expected result
`43.33`: 33.33 × 1,30 = 43,329, округлення HALF_UP до 2 знаків (BL-50).

### Environment
Модуль `code/modules/staynow/v1` (версія 1.0.0); JDK 17 (Eclipse Temurin 17.0.21), JUnit 5.10; запуск: `mvn test -Dtest=PriceCalculatorTest`; 12.11.2026.

### Attachments
Підсумок запуску: `org.opentest4j.AssertionFailedError: expected: <43.33> but was: <43.32>` (`PriceCalculatorTest.nightPriceIsRoundedHalfUp`, перший кейс); другий кейс — `expected: <129.99> but was: <129.98>`.

---

## BUG-009

**Title:** BookingValidator.validate приймає дату заїзду, ранішу за today (немає CHECK_IN_IN_PAST)

**Bug ID:** BUG-009 **Мітка:** module **Reporter:** Мукоїд Е. **Date:** 08.11.2026 **Reviewer:** — **Status:** New
**Severity:** Major **Priority:** P3
**Linked:** requirement BL-10; test `BookingValidatorTest.checkInBeforeTodayIsRejected` (набір [1] «2026-11-09, true»)

### Description
BL-10 (SRS 1.1) вимагає, щоб дата заїзду була не ранішою за поточну дату `today`. Модуль v1 для заїзду на день раніше за `today` повертає порожній список — бронювання в минулому вважається допустимим. Заїзд у день `today` і пізніше обробляється правильно.
Severity Major (4.12): система приймає некоректне бронювання на дату, що вже минула, — порушення важливого правила цілісності даних; решта перевірок працює. Priority P3 (4.13): інших тестів не блокує.

### Steps to reproduce
1. Модуль `staynow` v1; створити `BookingValidator validator = new BookingValidator();`.
2. Викликати `validator.validate(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 11), 1, 2, LocalDate.of(2026, 11, 10))` — заїзд 09.11.2026, today = 10.11.2026.

### Actual result
Повертається порожній список `[]`.

### Expected result
Список містить `CHECK_IN_IN_PAST` (у SRS 1.1 — `PAST_CHECK_IN`): дата заїзду раніша за today (BL-10).

### Environment
Модуль `code/modules/staynow/v1` (версія 1.0.0); JDK 17 (Eclipse Temurin 17.0.21), JUnit 5.10; запуск: `mvn test -Dtest=BookingValidatorTest` (або IntelliJ IDEA → Run 'BookingValidatorTest'); 08.11.2026.

### Attachments
Підсумок запуску: `org.opentest4j.AssertionFailedError: Заїзд 2026-11-09, отримано: [] ==> Expected: true, Actual: false` (BookingValidatorTest.checkInBeforeTodayIsRejected).

---

## BUG-010

**Title:** PriceCalculator.isWeekendNight вважає вихідними суботу й неділю замість п'ятниці й суботи (зсув на день)

**Bug ID:** BUG-010 **Мітка:** module **Reporter:** Андрущенко К. **Date:** 10.11.2026 **Reviewer:** — **Status:** New
**Severity:** Major **Priority:** P3
**Linked:** requirement BL-30; tests `PriceCalculatorTest.weekendNightIsFridayAndSaturday` (набори [2] «2026-11-13, true», [4] «2026-11-15, false»), `nightPriceFormula` (набір [2]), `totalSumsNightsAcrossSeasonBoundary`, `longStayDiscountStartsAtSevenNights` (частина «6 ночей»)

### Description
BL-30 (SRS 1.1) і javadoc класу визначають «вихідну ніч» як ніч з п'ятниці на суботу або з суботи на неділю, тобто вихідними мають вважатись ночі, що **починаються** в п'ятницю і в суботу. Метод `isWeekendNight` натомість перевіряє `SATURDAY`/`SUNDAY` — класичний зсув визначення на один день: п'ятниця хибно вважається буднем, а неділя — вихідною.
Severity Major (4.12): помилка в ключовій формулі ціноутворення, що каскадно впливає на `nightPrice()` і `total()` — усі п'ятничні ночі недорахунковуються (без надбавки 15 %), усі недільні — перерахунковуються. Priority P3 (4.13): дефект фіксується одразу чотирма тестами, але жодного іншого тесту в наборі не блокує — кожен тест перевіряє незалежний набір значень і виконується самостійно.

### Steps to reproduce
1. Модуль `staynow` v1; створити `PriceCalculator calc = new PriceCalculator();`.
2. Викликати `calc.isWeekendNight(LocalDate.of(2026, 11, 13))` — п'ятниця, 13.11.2026.
3. Для порівняння: `calc.isWeekendNight(LocalDate.of(2026, 11, 15))` — неділя, 15.11.2026.

### Actual result
Крок 2: `false`. Крок 3: `true`.

### Expected result
За BL-30: крок 2 має повернути `true` (п'ятниця — вихідна ніч), крок 3 — `false` (неділя — не вихідна).

### Environment
Модуль `code/modules/staynow/v1` (версія 1.0.0); JDK 17 (Eclipse Temurin 17.0.21), JUnit 5.10; запуск: `mvn test -Dtest=PriceCalculatorTest`; 09–10.11.2026.

### Attachments
Підсумок запуску (два незалежні прогони, 09.11.2026 18:44 і 23:57, однаковий результат):
`isWeekendNight(2026-11-13) ==> expected: <true> but was: <false>`
`isWeekendNight(2026-11-15) ==> expected: <false> but was: <true>`
`nightPrice(100, 2026-09-18) ==> expected: <115.00> but was: <100.00>`
`totalSumsNightsAcrossSeasonBoundary ==> expected: <345.00> but was: <360.00>`
`longStayDiscountStartsAtSevenNights, «6 ночей» ==> expected: <630.00> but was: <615.00>`

---

## BUG-011

**Title:** PriceCalculator.nightPrice округлює вартість ночі вниз (DOWN) замість HALF_UP

**Bug ID:** BUG-011 **Мітка:** module **Reporter:** Андрущенко К. **Date:** 10.11.2026 **Reviewer:** — **Status:** New
**Severity:** Minor **Priority:** P3
**Linked:** requirement BL-50; test `PriceCalculatorTest.nightPriceIsRoundedHalfUp`

### Description
Javadoc класу і BL-50 (SRS 1.1) прямо вимагають округлення HALF_UP до 2 знаків. Метод `nightPrice` викликає `setScale(2, RoundingMode.DOWN)` — завжди відкидає зайві розряди, а не округлює за правилом «від 5 — вгору».
Дефект незалежний від BUG-010 (зсуву дня): навіть якщо надбавка за вихідну ніч через BUG-010 не застосовується, саме сезонний множник уже дає розходження. Для `nightPrice(33.33, 2026-06-05)`: `33.33 × 1.30 = 43.329` — третій знак «9» ≥ 5, тож за HALF_UP має бути `43.33`, а код через DOWN повертає `43.32` (саме це значення й зафіксовано в логу — без жодного стосунку до відсутньої надбавки за вихідний, яка дала б інше число).
Severity Minor (4.12): розходження в копійках, функція в цілому працює. Priority P3 (4.13): інших тестів не блокує.

### Steps to reproduce
1. Модуль `staynow` v1; створити `PriceCalculator calc = new PriceCalculator();`.
2. Викликати `calc.nightPrice(new BigDecimal("33.33"), LocalDate.of(2026, 6, 5))`.

### Actual result
`43.32`

### Expected result
Мінімум `43.33` (сезонний множник із округленням HALF_UP, без урахування BUG-010); остаточно очікуване тестом значення (з коректною надбавкою за вихідну ніч) — `49.83` (BL-50), що потребує виправлення обох дефектів разом.

### Environment
Модуль `code/modules/staynow/v1` (версія 1.0.0); JDK 17 (Eclipse Temurin 17.0.21), JUnit 5.10; запуск: `mvn test -Dtest=PriceCalculatorTest`; 09–10.11.2026.

### Attachments
Підсумок запуску: `org.opentest4j.AssertionFailedError: expected: <49.83> but was: <43.32>` (`nightPriceIsRoundedHalfUp`, другий кейс). Проміжний розрахунок `33.33 × 1.30 = 43.329 → HALF_UP 43.33 ≠ фактичні 43.32` наведено вище в Description як доказ незалежності від BUG-010.