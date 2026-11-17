# Верифікація дефектів після регресії - Спринт 5, «СтейНау»

> **Статус:** чернетка від 17.11.2026 (Мукоїд Е.). Статус змінено лише після реального перевиконання кроків (методичка 3.3, правило 1); закриття затверджує Керівник тестування (Андрущенко К.) рецензією PR.
> Джерела перевірки: `docs/sprint5/regression-log.xlsx`, `docs/sprint5/surefire-v2/`, `docs/sprint5/defects-regression.md`.
> BUG-012 знято ще у спринті 4 (за GUI-70), у верифікації не бере участі.

## 1. Зведена таблиця

| Дефект | Severity | Об'єкт | Чим перевірено | Результат на v2 | Статус до регресії | Статус після | Дата, виконавець |
|---|---|---|---|---|---|---|---|
| BUG-001 | Major | стенд / demo | TC-050 | відтворюється: через 33 хв після входу адмінпанель лишилась відкритою | Open | Open (відтворюється) | 16.11.2026, Мукоїд Е. |
| BUG-002 | Normal | стенд / demo | TC-048 | відтворюється: «Report» лише календар, немає вибору періоду і «Прогноз» | Open | Open (відтворюється) | 17.11.2026, Мукоїд Е. |
| BUG-003 | Minor | стенд / demo | TC-019 | відтворюється: «size must be between 3 and 30» не називає поле | Open | Open (відтворюється) | 16.11.2026, Мукоїд Е. |
| BUG-004 | Minor | стенд / demo | TC-019 | відтворюється: «must not be empty» не називає поле Email | Open | Open (відтворюється) | 16.11.2026, Мукоїд Е. |
| BUG-005 | Minor | стенд / demo | TC-019 | відтворюється: повідомлення для Phone не називають поле | Open | Open (відтворюється) | 16.11.2026, Мукоїд Е. |
| BUG-006 | Minor | стенд / demo | TC-019 | відтворюється: «size must be between 3 and 18» не називає поле | Open | Open (відтворюється) | 16.11.2026, Мукоїд Е. |
| BUG-007 | Minor | стенд / demo | TC-020 | відтворюється: «must be a well-formed email address» не називає поле | Open | Open (відтворюється) | 16.11.2026, Мукоїд Е. |
| BUG-008 | Normal | модуль v2 | `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected[2]` | failed на v1, passed на v2 | Fixed | Verified → **Closed (verified)** | 16.11.2026, Мукоїд Е. |
| BUG-009 | Major | модуль v2 | `BookingValidatorTest.checkInBeforeTodayIsRejected[1]` | failed на v2: заїзд 2026-11-09, отримано `[]` (немає CHECK_IN_IN_PAST) | Fixed | **Reopened** | 16.11.2026, Мукоїд Е. |
| BUG-010 | Major | модуль v2 | `longStayDiscountStartsAtSevenNights`, `nightPriceFormula[2]`, `totalSumsNightsAcrossSeasonBoundary`, `weekendNightIsFridayAndSaturday[2]`, `[4]` | failed на v1, passed на v2 (усі 5) | Fixed | Verified → **Closed (verified)** | 16.11.2026, Мукоїд Е. |
| BUG-011 | Minor | модуль v2 | `PriceCalculatorTest.nightPriceIsRoundedHalfUp` | failed на v1, passed на v2 | Fixed | Verified → **Closed (verified)** | 16.11.2026, Мукоїд Е. |
| BUG-013 | Minor | demo і стенд | `AdminLoginTest.validLoginOpensRooms` (TC-036), TC-041, TC-042 | відтворюється: після Logout відкривається головна, а не `/admin`; на demo `POST /api/auth/logout` ще й повертає 500 (BUG-025) | Open | Open (відтворюється) | 17.11.2026, Мукоїд Е. |
| BUG-014 | Major | модуль v2 | `AvailabilityTest.cancellationFeeAtBoundaries[2]`, `refundAtBoundaries[1]` | failed на v2: за 7 днів до заїзду утримується 500.00 замість 0.00 (повернення 500.00 замість 1000.00) | Fixed | **Reopened** | 16.11.2026, Мукоїд Е. |
| BUG-015 | Major | модуль v2 | `AvailabilityTest.bookingFreeRoomIsRegistered`, `isAvailableForIntervals[1]`, `[2]` | failed на v1, passed на v2 (усі 3) | Fixed | Verified → **Closed (verified)** | 16.11.2026, Мукоїд Е. |

Підсумок: Closed (verified) - 4 (BUG-008, 010, 011, 015); Reopened - 2 (BUG-009, 014); відкриті й відтворюються - 8 (BUG-001...007, 013).

## 2. Записи верифікації (текст для коментарів до дефектів і розділу 7 звіту)

Звіти про дефекти спринтів 3-4 (`bug-reports.md`, `bug-reports-demo.md`) не змінюються: статуси «до» і «після» фіксуються у цьому файлі та в колонці I журналу регресії. Формат запису: **статус, що перевірено, результат, дата і виконавець**.

**BUG-001** - Повторна перевірка на v2 (стенд): TC-050, вхід 20:20, перевірка 20:53 (33 хв), після переходу на «Rooms» адмінпанель відкрита, сторінку входу не показано. Дефект відтворюється, статус: Open. 16.11.2026, Мукоїд Е.

**BUG-002** - Повторна перевірка на v2 (стенд): TC-048, розділ «Report» містить лише календар бронювань, немає вибору періоду, заповнюваності та позначки «Прогноз». Дефект відтворюється, статус: Open. 17.11.2026, Мукоїд Е.

**BUG-003** - Повторна перевірка на v2 (стенд): TC-019, при порожньому Lastname повідомлення «size must be between 3 and 30» не називає поле. Дефект відтворюється, статус: Open. 16.11.2026, Мукоїд Е.

**BUG-004** - Повторна перевірка на v2 (стенд): TC-019, при порожньому Email повідомлення «must not be empty» не називає поле. Дефект відтворюється, статус: Open. 16.11.2026, Мукоїд Е.

**BUG-005** - Повторна перевірка на v2 (стенд): TC-019, при порожньому Phone повідомлення «must not be empty» і «size must be between 11 and 21» не називають поле. Дефект відтворюється, статус: Open. 16.11.2026, Мукоїд Е.

**BUG-006** - Повторна перевірка на v2 (стенд): TC-019, при порожньому Firstname повідомлення «size must be between 3 and 18» не називає поле. Дефект відтворюється, статус: Open. 16.11.2026, Мукоїд Е.

**BUG-007** - Повторна перевірка на v2 (стенд): TC-020, при Email без «@» повідомлення «must be a well-formed email address» не називає поле. Дефект відтворюється, статус: Open. 16.11.2026, Мукоїд Е.

**BUG-008** - Верифікація на модулі v2: `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected(int, boolean)[2]` - passed (на v1 failed). Бронювання на 30 ночей більше не відхиляється. Статус: Verified (до Closed після затвердження КТ). 16.11.2026, Мукоїд Е.

**BUG-009** - Верифікація на модулі v2: `BookingValidatorTest.checkInBeforeTodayIsRejected(LocalDate, boolean)[1]` - failed: заїзд 2026-11-09, отримано `[]` (очікувався код CHECK_IN_IN_PAST). Дефект не виправлено, статус: Reopened. 16.11.2026, Мукоїд Е.

**BUG-010** - Верифікація на модулі v2: `longStayDiscountStartsAtSevenNights`, `nightPriceFormula[2]`, `totalSumsNightsAcrossSeasonBoundary`, `weekendNightIsFridayAndSaturday[2]` і `[4]` - passed (на v1 failed). Вихідними вважаються п'ятниця й субота. Статус: Verified (до Closed після затвердження КТ). 16.11.2026, Мукоїд Е.

**BUG-011** - Верифікація на модулі v2: `PriceCalculatorTest.nightPriceIsRoundedHalfUp` - passed (на v1 failed). Округлення HALF_UP. Статус: Verified (до Closed після затвердження КТ). 16.11.2026, Мукоїд Е.

**BUG-013** - Повторна перевірка: TC-036, TC-041, TC-042 на локальному стенді та на demo (Chrome), `AdminLoginTest.validLoginOpensRooms` failed. Після Logout відкривається головна, а не форма входу `/admin`. Дефект відтворюється, статус: Open. На demo додатково `POST /api/auth/logout` повертає 500 (кандидат BUG-025). 17.11.2026, Мукоїд Е.

**BUG-014** - Верифікація на модулі v2: `AvailabilityTest.cancellationFeeAtBoundaries(int, String)[2]` - failed: за 7 днів до заїзду очікувалось 0.00, отримано 500.00; `refundAtBoundaries(int, String)[1]` - failed: очікувалось 1000.00, отримано 500.00. Дефект не виправлено, статус: Reopened. 16.11.2026, Мукоїд Е.

**BUG-015** - Верифікація на модулі v2: `AvailabilityTest.bookingFreeRoomIsRegistered`, `isAvailableForIntervals(LocalDate, LocalDate, boolean)[1]` і `[2]` - passed (на v1 failed). Виїзд одного гостя в день заїзду іншого більше не вважається зайнятістю. Статус: Verified (до Closed після затвердження КТ). 16.11.2026, Мукоїд Е.

## 3. Що потрібно від Керівника тестування

1. Затвердити статуси Verified → Closed (verified) для BUG-008, 010, 011, 015.
2. Підтвердити Reopened для BUG-009 і BUG-014 та передати їх розробникам.
3. Погодити робочі номери нових дефектів BUG-016...BUG-026 (`defects-regression.md`).
