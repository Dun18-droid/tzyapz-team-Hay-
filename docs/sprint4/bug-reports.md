# Звіти про дефекти модуля — Спринт 4, «СтейНау»

Трекер дефектів модуля бізнес-логіки `code/modules/staynow/v1` (позначка `module`). Шаблон — як у спринті 3 ([docs/sprint3/bug-reports.md](../sprint3/bug-reports.md)), нумерація продовжує його (BUG-008…). Джерело очікуваних значень — розділ BL [SRS 1.1](../sprint2/SRS-1.1.md); тести, що фіксують дефекти, лишаються «червоними» до перевірки на v2 (спринт 5). Матриця покриття — [coverage-matrix.md](coverage-matrix.md).

## Зведення

| Bug ID | Title | Severity | Priority | Вимога | Тест | Статус |
|---|---|---|---|---|---|---|
| [BUG-008](#bug-008) | BookingValidator.validate відхиляє бронювання рівно на 30 ночей | Normal | P3 | BL-10 | `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected` | New |
| [BUG-009](#bug-009) | BookingValidator.validate приймає дату заїзду, ранішу за today | Major | P3 | BL-10 | `BookingValidatorTest.checkInBeforeTodayIsRejected` | New |

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
1. Модуль `staynow` v1; створити `BookingValidator validator = new BookingValidator();`.
2. Викликати `validator.validate(LocalDate.of(2026, 11, 20), LocalDate.of(2026, 12, 20), 1, 2, LocalDate.of(2026, 11, 10))` — 30 ночей, 1 гість, місткість 2, today = 10.11.2026.

### Actual result
Повертається список `[NIGHTS_OUT_OF_RANGE]`.

### Expected result
Порожній список: 30 ночей допустимі (BL-10, «від 1 до 30 включно»).

### Environment
Модуль `code/modules/staynow/v1` (версія 1.0.0); JDK 17 (Eclipse Temurin 17.0.21), JUnit 5.10; запуск: `mvn test -Dtest=BookingValidatorTest` (або IntelliJ IDEA → Run 'BookingValidatorTest'); 08.11.2026.

### Attachments
Підсумок запуску: `org.opentest4j.AssertionFailedError: Ночей: 30, отримано помилки: [NIGHTS_OUT_OF_RANGE] ==> Expected: false, Actual: true` (BookingValidatorTest.nightsOutsideOneToThirtyAreRejected).

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
