# Звіти про дефекти demo-застосунку — Спринт 4, «СтейНау»

Трекер дефектів demo-застосунку (https://automationintesting.online/) — той самий трекер, що й дефекти
модуля ([bug-reports.md](bug-reports.md)), без мітки `module`. Нумерація продовжує
[docs/sprint3/bug-reports.md](../sprint3/bug-reports.md). Матриця покриття — [coverage-matrix.md](coverage-matrix.md).

## Зведення

| Bug ID | Title | Severity | Priority | Вимога | Тест | Статус |
|---|---|---|---|---|---|---|
| [BUG-012](#bug-0xx) | Після невдалого входу (невірний пароль) кнопка Logout усе одно відображається | Major | P2 | F-80 | `AdminLoginTest.wrongPasswordIsRejected` | New |
| [BUG-013](#bug-0xx1) | Після Logout форма входу не з'являється повторно | Major | P2 | F-80 | `AdminLoginTest.validLoginOpensRooms` | New |

---

## BUG-012

**Title:** Після невдалого входу (невірний пароль) кнопка Logout усе одно відображається

**Bug ID:** BUG-0XX **Reporter:** Андрущенко К. **Date:** 10.11.2026 **Reviewer:** — **Status:** New
**Severity:** Major **Priority:** P2
**Linked:** TC-037 / F-80; test `AdminLoginTest.wrongPasswordIsRejected`

### Preconditions
Сторінка https://automationintesting.online/admin відкрита, форма входу порожня.

### Steps to reproduce
1. У поле `id=username` ввести `admin`.
2. У поле `id=password` ввести `T3-wrong`.
3. Натиснути кнопку `id=doLogin`.

### Actual result
З'являється повідомлення про помилку "Invalid credentials" (текст коректний). URL лишається на сторінці входу (перехід на `/admin/rooms` не відбувається). Проте кнопка `Logout` (`//button[text()='Logout']`) стає видимою — так, ніби вхід відбувся.

### Expected result
За F-80 при невірному паролі елементів авторизованого інтерфейсу (зокрема Logout) бути не повинно.

### Environment
Demo https://automationintesting.online/ (публічний, спільний), Chrome 156 headless через Selenium WebDriver 4.21; запуск: `mvn test -DexcludedGroups=none -Dtest=AdminLoginTest#wrongPasswordIsRejected`; 09–10.11.2026.

### Attachments
Два незалежні прогони (09.11.2026 18:44 і 23:57), однаковий результат:
`Кнопка Logout не має бути видимою ==> expected: <false> but was: <true>`.

### Note
Публічне demo (легенда продукту: "сайт регулярно оновлюється"); фактичного переходу на `/admin/rooms` не підтверджено — ручна перевірка глибини доступу ще не проводилась, бажано виконати до фінального звіту.

---

## BUG-013

**Title:** Після Logout форма входу не з'являється повторно

**Bug ID:** BUG-0XX+1 **Reporter:** Андрущенко К. **Date:** 10.11.2026 **Reviewer:** — **Status:** New
**Severity:** Major **Priority:** P2
**Linked:** TC-036 / F-80; test `AdminLoginTest.validLoginOpensRooms`

### Preconditions
Успішний вхід admin / password, відкрито розділ `/admin/rooms`.

### Steps to reproduce
1. Увійти admin / password (див. BUG-0XX, кроки 1–3 з правильним паролем).
2. Переконатись, що відкрито `/admin/rooms`, видно Logout і Rooms.
3. Натиснути кнопку `Logout` (`//button[text()='Logout']`).

### Actual result
Протягом 15 секунд очікування поле `id=username` не з'являється повторно.

### Expected result
За F-80 (TC-036) після Logout користувач має повернутись на форму входу.

### Environment
Demo https://automationintesting.online/ (публічний, спільний), Chrome 156 headless через Selenium WebDriver 4.21; запуск: `mvn test -DexcludedGroups=none -Dtest=AdminLoginTest#validLoginOpensRooms`; 09–10.11.2026.

### Attachments
Два незалежні прогони (09.11.2026 18:44, час 17.66 с; 23:57, час 17.87 с) — однаковий результат.