# Протокол запуску — Спринт 4, «СтейНау»

**Команди:** `mvn clean test` (юніт-набір) і `mvn clean test -DexcludedGroups=none` (юніт- + UI).
**Модуль:** `src/main/java/ua/kpi/tzyapz/staynow` v1. **Дата запуску:** 11.11.2026.
**Середовище:** Kali Linux (ядро 7.1.5+kali-amd64, amd64), OpenJDK 25.0.5 (збірка з `release 17`),
Google Chrome 157.0.8103.50 (headless), Selenium 4.21.0, Surefire 3.2.5, JUnit Jupiter 5.10.2.

## 1. Повний запуск (`mvn clean test -DexcludedGroups=none`)

Підсумок Maven: `Tests run: 91, Failures: 13, Errors: 1, Skipped: 0` — `BUILD FAILURE`.

| Тест-клас | Вимоги / сценарії | Tests run | Failures | Errors | Skipped |
|---|---|---|---|---|---|
| `BookingValidatorTest` | BL-10, BL-20 | 25 | 2 | 0 | 0 |
| `PriceCalculatorTest` | BL-30, BL-40, BL-50 | 31 | 6 | 0 | 0 |
| `AvailabilityTest` | BL-60, BL-70 | 20 | 4 | 1 | 0 |
| `ExampleTest` (зразок) | BL-10, BL-20, BL-50 | 2 | 0 | 0 | 0 |
| `CalculatorTest` (стартовий проєкт) | — | 7 | 0 | 0 | 0 |
| `ContactFormTest` (UI) | TC-021, 023, 024 | 3 | 0 | 0 | 0 |
| `AdminLoginTest` (UI) | TC-036, 037 | 2 | 1 | 0 | 0 |
| `SmokeUiTest` (UI, стартовий проєкт) | — | 1 | 0 | 0 | 0 |
| **Разом** | | **91** | **13** | **1** | **0** |

Юніт-набір без UI (`mvn clean test`) дає `Tests run: 85, Failures: 12, Errors: 1` — ті самі 13 «червоних»
юніт-запусків без UI-тестів. Surefire рахує кожен запуск параметризованого методу окремо: 21 тест-метод
класів команди дають 76 запусків.

## 2. «Червоні» тести і дефекти

| Тест (набір даних) | Фактичний результат | Дефект |
|---|---|---|
| `BookingValidatorTest.nightsOutsideOneToThirtyAreRejected` [2] — 30 ночей | `NIGHTS_OUT_OF_RANGE` отримано, очікували false | BUG-008 |
| `BookingValidatorTest.checkInBeforeTodayIsRejected` [1] — заїзд 2026-11-09 | `[]`, очікували `CHECK_IN_IN_PAST` | BUG-009 |
| `PriceCalculatorTest.weekendNightIsFridayAndSaturday` [2] — 2026-11-13 (Пт); [4] — 2026-11-15 (Нд) | true очікували, false (Пт); false очікували, true (Нд) | BUG-010 |
| `PriceCalculatorTest.nightPriceFormula` [2] — 100, 2026-09-18 (Пт) | 115.00 очікували, 100.00 | BUG-010 |
| `PriceCalculatorTest.totalSumsNightsAcrossSeasonBoundary` | 345.00 очікували, 360.00 | BUG-010 |
| `PriceCalculatorTest.longStayDiscountStartsAtSevenNights` — 6 ночей | 630.00 очікували, 615.00 | BUG-010 |
| `PriceCalculatorTest.nightPriceIsRoundedHalfUp` — 114.99 / 49.83 | 99.99 і 43.32 | BUG-010, BUG-011 |
| `AvailabilityTest.cancellationFeeAtBoundaries` [2] — 7 днів | 0.00 очікували, 500.00 | BUG-014 |
| `AvailabilityTest.refundAtBoundaries` [1] — 7 днів | 1000.00 очікували, 500.00 | BUG-014 |
| `AvailabilityTest.isAvailableForIntervals` [1] 12-15→12-17; [2] 12-08→12-10 | true очікували, false | BUG-015 |
| `AvailabilityTest.bookingFreeRoomIsRegistered` (Error) | `IllegalStateException: Номер 101 зайнятий на вибрані дати` | BUG-015 |
| `AdminLoginTest.validLoginOpensRooms` (UI, TC-036) | після Logout немає форми входу з полем username (F-85) | BUG-013 (demo) |

Усі інші запуски — passed (77 із 91): межі BL-40 (сезони), BL-20 (email, телефон), BL-60 (пороги 3, 6, 8),
BL-70 (справжні перетини, зайнятий номер), а також `ContactFormTest` (TC-021, 023, 024), `AdminLoginTest`
TC-037, `SmokeUiTest` і `CalculatorTest`.

## 3. Примітки до UI-запуску

- Тривалість UI-частини ≈ 45 с; demo `https://automationintesting.online` відповідав без збоїв.
- Попередження `Unable to find CDP implementation matching 157` у виводі — інформаційні (немає пакета
  `selenium-devtools` під Chrome 157), на результат тестів не впливають.
- Перший запуск UI-набору впав на старті браузера: у PATH був старий `/usr/bin/chromedriver` версії 148
  проти Chrome 157 (`SessionNotCreatedException`). Після перейменування старого драйвера Selenium Manager
  завантажив сумісний — див. README, п. 8.
