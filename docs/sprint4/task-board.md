# Таблиця розподілу задач — Спринт 4 «Автоматизація: JUnit 5 і Selenium WebDriver»

Продукт: «СтейНау» (staynow) | Команда: Група E.
Ролі у спринті 4: Дятлов М. — Керівник тестування + Скрам-майстер / рецензент; Мукоїд Е. — Тест-аналітик;
Андрущенко К. — Інженер з тестування.

| № | Задача / підзадача | Роль | Виконавець | Статус | Артефакт / посилання | Рецензент |
|---|---|---|---|---|---|---|
| 1 | Sprint Planning: декомпозиція, розподіл тестів між усіма | КТ+СМ | Дятлов М. | готово | цей файл, coverage-matrix.md | Команда |
| 2 | Інфраструктура: репозиторій, стартовий проєкт, модуль v1, перевірка збірки | ІТ | Андрущенко К. | готово | PR #14 | Дятлов М. |
| 3 | Матриця покриття BL (14 обов'язкових тестів), рішення команди | ТА | Мукоїд Е. | готово | coverage-matrix.md | Команда |
| 4 | UI-сценарії з локаторами (TC-021, 023, 024, 036, 037) | ТА | Мукоїд Е. | готово | ui-scenarios.md | Андрущенко К. |
| 5 | `BookingValidatorTest` (BL-10, BL-20) і звіти BUG-008, BUG-009 | ТА | Мукоїд Е. | готово | BookingValidatorTest.java, bug-reports.md | Дятлов М. |
| 6 | `PriceCalculatorTest` (BL-30, 40, 50) і звіти BUG-010, BUG-011 | ІТ | Андрущенко К. | готово | PriceCalculatorTest.java, PR #15 | Мукоїд Е. |
| 7 | `AvailabilityTest` (BL-60, BL-70) і звіти BUG-014, BUG-015 | КТ+СМ | Дятлов М. | готово | AvailabilityTest.java, bug-reports.md | Андрущенко К. |
| 8 | Selenium IDE: запис TC-021, експорт у Java | ІТ | Андрущенко К. | готово | selenium-ide/ | Мукоїд Е. |
| 9 | WebDriver-тести: `BaseUiTest`, `ContactFormTest`, `AdminLoginTest`; звіт BUG-013 | ІТ | Андрущенко К. | готово (BUG-013 open) | src/test/.../ui/, bug-reports-demo.md | Мукоїд Е. |
| 10 | Code review `BookingValidatorTest` і BUG-008/009 | СМ | Дятлов М. | готово | code-review.md | — |
| 11 | Code review `PriceCalculatorTest` і BUG-010, 011, 013 | ТА | Мукоїд Е. | готово | PR #15, code-review.md | — |
| 12 | Code review `AvailabilityTest` і BUG-014, 015 | ІТ | Андрущенко К. | готово (approve 12.11.2026) | code-review.md | — |
| 13 | README команди, протокол запуску (Surefire) | КТ | Дятлов М. | в роботі (вписати результат реального `mvn clean test`) | README.md, surefire-summary.md | Мукоїд Е. |
| 14 | Розділ «Автоматизація» досьє: норматив, матриця, дефекти, висновок про v1 | КТ | Дятлов М. | заплановано | досьє v4 | Андрущенко К. |
| 15 | Підготовка Demo: `mvn clean test` у чистій копії, WebDriver без headless, Selenium IDE | КТ+СМ | Дятлов М. | заплановано | — | Команда |
| 16 | Ретроспектива, протокол Start/Stop/Continue | СМ | Дятлов М. | заплановано | retro.md | Мукоїд Е., Андрущенко К. |
