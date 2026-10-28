# Звіти про дефекти — Спринт 3, «СтейНау»

Трекер команди — цей файл (методичка, розд. 8: `bug-reports.md` замість GitHub Issues). Звіти складено за шаблоном 4.11, рецензування — за сімома правилами 4.10 (чек-лист 5.3).

Еталон — [SRS 1.1](../sprint2/SRS-1.1.md). Журнал виконання — [execution-log.xlsx](execution-log.xlsx). Вкладення — [attachments/](attachments/).

## Середовище (спільне для всіх звітів)

| Параметр | Значення |
|---|---|
| Продукт | restful-booker-platform, локальне розгортання за вказівкою викладача |
| Адреса | http://localhost:3003 (гостьова частина), http://localhost:3003/admin (адмінпанель) |
| Як запущено | `.\build_locally.cmd` у корені каталогу restful-booker-platform (код із ZIP-архіву гілки trunk) |
| Коміт restful-booker-platform | КОМІТ |
| Браузер | Chrome ВЕРСІЯ_CHROME |
| ОС | Windows ВЕРСІЯ_WINDOWS |
| Обліковий запис адміністратора | admin / password |

## Зведення

| Bug ID | Title | Severity | Priority | TC | Вимога | Статус |
|---|---|---|---|---|---|---|
| [BUG-001](#bug-001) | Сесія адміністратора не завершується після 30 хвилин неактивності | Major | P3 | TC-050 | S-20, F-130 | New |
| [BUG-002](#bug-002) | Розділ «Report» не дає вибрати період і не позначає майбутній період «Прогноз» | Normal | P3 | TC-048 | F-120 | New |
| [BUG-003](#bug-003) | Форма бронювання: при порожньому Lastname помилка «size must be between 3 and 30» не називає поле | Minor | P3 | TC-019 | GUI-60 | New |
| [BUG-004](#bug-004) | Форма бронювання: при порожньому Email помилка «must not be empty» не називає поле | Minor | P3 | TC-019 | GUI-60 | New |
| [BUG-005](#bug-005) | Форма бронювання: при порожньому Phone помилки «must not be empty» і «size must be between 11 and 21» не називають поле | Minor | P3 | TC-019 | GUI-60 | New |
| [BUG-006](#bug-006) | Форма бронювання: при порожньому Firstname друга помилка «size must be between 3 and 18» не називає поле | Minor | P3 | TC-019 | GUI-60 | New |
| [BUG-007](#bug-007) | Форма бронювання: при Email без «@» помилка «must be a well-formed email address» не називає поле | Minor | P3 | TC-020 | GUI-60 | New |

Разом: 7 звітів; severity — Major 1, Normal 1, Minor 5; priority — P3 7.

---

## BUG-001

**Title:** Сесія адміністратора не завершується після 30 хвилин неактивності

**Bug ID:** BUG-001 **Reporter:** Дятлов М. **Date:** 25.10.2026 **Reviewer:** — **Status:** New
**Severity:** Major **Priority:** P3
**Linked:** test case TC-050; requirement S-20, F-130

### Description
S-20 вимагає автоматично завершувати сесію адміністратора через 30 хвилин неактивності, F-130 — вимагати після цього повторний вхід. Фактично після 31 хвилини без дій адмінпанель лишається доступною без повторного входу, тож залишена без нагляду відкрита сторінка дає сторонній людині повний доступ до керування номерами.
Severity Major (4.12): вимога безпеки не виконується — важлива функція працює не на належному рівні, але бронювання гостями це не блокує. Priority P3 (4.13): дефект не блокує виконання жодного тест-кейсу.

### Steps to reproduce
1. Відкрити у Chrome адресу http://localhost:3003/admin (див. «Середовище»).
2. У поле Username ввести «admin».
3. У поле Password ввести «password».
4. Натиснути «Login»; зафіксувати час входу.
5. Не виконувати жодних дій у вкладці 31 хвилину.
6. Натиснути «Rooms» у шапці адмінпанелі.

### Actual result
Після кроку 6 адмінпанель лишається відкритою: адреса /admin/rooms, показано список номерів 101–103 і кнопку «Logout»; сторінку входу не показано, повторний вхід не вимагається. Вхід о 15:09, натискання «Rooms» о 15:41 (25.10.2026).

### Expected result
Після кроку 6 відкривається сторінка входу /admin; для доступу до адмінпанелі потрібен повторний вхід (S-20, F-130).

### Environment
Див. «Середовище»; Chrome, звичайне вікно (не інкогніто); обліковий запис admin, роль адміністратор; 25.10.2026, 15:09–15:41.

### Attachments
[BUG-001-1.png](attachments/BUG-001-1.png) — адмінпанель /admin/rooms зі списком номерів 101–103 і кнопкою «Logout» після 31 хвилини неактивності (крок 6).

---

## BUG-002

**Title:** Розділ «Report» не дає вибрати період і не позначає майбутній період «Прогноз»

**Bug ID:** BUG-002 **Reporter:** Дятлов М. **Date:** 25.10.2026 **Reviewer:** — **Status:** New
**Severity:** Normal **Priority:** P3
**Linked:** test case TC-048; requirement F-120

### Description
F-120 (SRS 1.1) вимагає звіт заповнюваності номерів: за минулий період — за фактичними заселеннями, за майбутній — за підтвердженими бронюваннями з позначкою «Прогноз». Розділ «Report» адмінпанелі цього не надає, тож адміністратор не може отримати заповнюваність за вибраний період.
Severity Normal (4.12): адмінпанеллю користуватися можна, бронювання видно, але потрібного показника немає. Priority P3 (4.13): інших кейсів не блокує; вимога F-120 має пріоритет Low і ще потребує підтвердження замовником (SRS 1.1, дефект № 7).

### Steps to reproduce
1. Відкрити у Chrome (нова сесія, інкогніто) адресу http://localhost:3003/admin (див. «Середовище»).
2. У поле Username ввести «admin».
3. У поле Password ввести «password».
4. Натиснути «Login».
5. Натиснути «Report» у меню адмінпанелі.

### Actual result
Після кроку 5 показано календар бронювань за поточний місяць із кнопками перемикання місяців. Поля вибору періоду (дата початку і кінця) немає; значення заповнюваності (частки зайнятих ночей) немає; позначки «Прогноз» для майбутніх дат немає.

### Expected result
Після кроку 5 можна вибрати період; для минулого періоду показано заповнюваність за фактичними заселеннями і виселеннями, для майбутнього — за підтвердженими бронюваннями з позначкою «Прогноз» (F-120).

### Environment
Див. «Середовище»; Chrome, інкогніто; обліковий запис admin, роль адміністратор; 25.10.2026.

### Attachments
[BUG-002-1.png](attachments/BUG-002-1.png) — сторінка «Report» після кроку 5: календар без вибору періоду, показника заповнюваності і позначки «Прогноз».

---

## BUG-003

**Title:** Форма бронювання: при порожньому Lastname помилка «size must be between 3 and 30» не називає поле

**Bug ID:** BUG-003 **Reporter:** Дятлов М. **Date:** 28.10.2026 **Reviewer:** — **Status:** New
**Severity:** Minor **Priority:** P3
**Linked:** test case TC-019; requirement GUI-60

### Description
GUI-60 вимагає, щоб кожне повідомлення про помилку у формі бронювання називало поле і причину (приклад у SRS: «Subject is required»). Для порожнього поля Lastname, крім повідомлення «Lastname should not be blank», показується «size must be between 3 and 30» — без назви поля. Гість не може зрозуміти, якого поля стосується обмеження довжини.
Severity Minor (4.12): проблема інтерфейсу — бронювання можливе після заповнення полів, але повідомлення неінформативне. Priority P3 (4.13): інших кейсів не блокує.

### Steps to reproduce
1. Відкрити у Chrome (нова сесія, інкогніто) адресу http://localhost:3003 (див. «Середовище»).
2. У поле Check In ввести дату «сьогодні + 120 днів» (мм/дд/рррр).
3. У поле Check Out ввести дату «сьогодні + 121 день» (мм/дд/рррр).
4. Натиснути «Check Availability».
5. Натиснути «Book now» на картці номера 103 (Suite, £225 per night).
6. Натиснути «Reserve Now» на сторінці номера.
7. Не заповнюючи поля Firstname, Lastname, Email, Phone, натиснути «Reserve Now» у формі.

### Actual result
Над кнопкою «Reserve Now» показано перелік із 7 повідомлень: «size must be between 3 and 30», «must not be empty», «must not be empty», «size must be between 11 and 21», «Firstname should not be blank», «Lastname should not be blank», «size must be between 3 and 18». Повідомлення «size must be between 3 and 30» (обмеження довжини Lastname) не містить назви поля. Бронювання не створено.

### Expected result
Кожне повідомлення називає поле і причину, наприклад «Lastname must be between 3 and 30 characters» (GUI-60).

### Environment
Див. «Середовище»; Chrome, інкогніто; без входу (гість); 28.10.2026.

### Attachments
[BUG-003-1.png](attachments/BUG-003-1.png) — форма бронювання після кроку 7: червоний блок із 7 повідомленнями над Price Summary (£225 x 1 nights, Total £265).

---

## BUG-004

**Title:** Форма бронювання: при порожньому Email помилка «must not be empty» не називає поле

**Bug ID:** BUG-004 **Reporter:** Дятлов М. **Date:** 28.10.2026 **Reviewer:** — **Status:** New
**Severity:** Minor **Priority:** P3
**Linked:** test case TC-019; requirement GUI-60

### Description
GUI-60 вимагає, щоб кожне повідомлення про помилку називало поле і причину. Для порожнього поля Email показується «must not be empty» без назви поля; таке саме повідомлення виводиться і для Phone, тож гість не може визначити, яке поле заповнити.
Severity Minor (4.12): проблема інтерфейсу, бронювання можливе після заповнення полів. Priority P3 (4.13): інших кейсів не блокує.

### Steps to reproduce
1. Відкрити у Chrome (нова сесія, інкогніто) адресу http://localhost:3003 (див. «Середовище»).
2. У поле Check In ввести дату «сьогодні + 120 днів» (мм/дд/рррр).
3. У поле Check Out ввести дату «сьогодні + 121 день» (мм/дд/рррр).
4. Натиснути «Check Availability».
5. Натиснути «Book now» на картці номера 103 (Suite, £225 per night).
6. Натиснути «Reserve Now» на сторінці номера.
7. Не заповнюючи поля Firstname, Lastname, Email, Phone, натиснути «Reserve Now» у формі.

### Actual result
Серед 7 повідомлень над кнопкою «Reserve Now» є «must not be empty», що стосується поля Email, без назви поля. Бронювання не створено.

### Expected result
Повідомлення називає поле і причину, наприклад «Email is required» (GUI-60).

### Environment
Див. «Середовище»; Chrome, інкогніто; без входу (гість); 28.10.2026.

### Attachments
[BUG-003-1.png](attachments/BUG-003-1.png) — той самий знімок, що й у BUG-003 (друге повідомлення в переліку).

---

## BUG-005

**Title:** Форма бронювання: при порожньому Phone помилки «must not be empty» і «size must be between 11 and 21» не називають поле

**Bug ID:** BUG-005 **Reporter:** Дятлов М. **Date:** 28.10.2026 **Reviewer:** — **Status:** New
**Severity:** Minor **Priority:** P3
**Linked:** test case TC-019; requirement GUI-60

### Description
GUI-60 вимагає, щоб кожне повідомлення про помилку називало поле і причину. Для порожнього поля Phone показуються два повідомлення — «must not be empty» і «size must be between 11 and 21», — жодне з них не називає поле.
Severity Minor (4.12): проблема інтерфейсу, бронювання можливе після заповнення полів. Priority P3 (4.13): інших кейсів не блокує.

### Steps to reproduce
1. Відкрити у Chrome (нова сесія, інкогніто) адресу http://localhost:3003 (див. «Середовище»).
2. У поле Check In ввести дату «сьогодні + 120 днів» (мм/дд/рррр).
3. У поле Check Out ввести дату «сьогодні + 121 день» (мм/дд/рррр).
4. Натиснути «Check Availability».
5. Натиснути «Book now» на картці номера 103 (Suite, £225 per night).
6. Натиснути «Reserve Now» на сторінці номера.
7. Не заповнюючи поля Firstname, Lastname, Email, Phone, натиснути «Reserve Now» у формі.

### Actual result
Серед 7 повідомлень над кнопкою «Reserve Now» є «must not be empty» і «size must be between 11 and 21», що стосуються поля Phone, без назви поля. Бронювання не створено.

### Expected result
Одне повідомлення, що називає поле і причину, наприклад «Phone must be between 11 and 21 characters» (GUI-60).

### Environment
Див. «Середовище»; Chrome, інкогніто; без входу (гість); 28.10.2026.

### Attachments
[BUG-003-1.png](attachments/BUG-003-1.png) — той самий знімок, що й у BUG-003 (третє і четверте повідомлення в переліку).

---

## BUG-006

**Title:** Форма бронювання: при порожньому Firstname друга помилка «size must be between 3 and 18» не називає поле

**Bug ID:** BUG-006 **Reporter:** Дятлов М. **Date:** 28.10.2026 **Reviewer:** — **Status:** New
**Severity:** Minor **Priority:** P3
**Linked:** test case TC-019; requirement GUI-60

### Description
GUI-60 вимагає, щоб кожне повідомлення про помилку називало поле і причину. Для порожнього поля Firstname, крім «Firstname should not be blank», показується друге повідомлення «size must be between 3 and 18» без назви поля.
Severity Minor (4.12): проблема інтерфейсу, бронювання можливе після заповнення полів. Priority P3 (4.13): інших кейсів не блокує.

### Steps to reproduce
1. Відкрити у Chrome (нова сесія, інкогніто) адресу http://localhost:3003 (див. «Середовище»).
2. У поле Check In ввести дату «сьогодні + 120 днів» (мм/дд/рррр).
3. У поле Check Out ввести дату «сьогодні + 121 день» (мм/дд/рррр).
4. Натиснути «Check Availability».
5. Натиснути «Book now» на картці номера 103 (Suite, £225 per night).
6. Натиснути «Reserve Now» на сторінці номера.
7. Не заповнюючи поля Firstname, Lastname, Email, Phone, натиснути «Reserve Now» у формі.

### Actual result
Серед 7 повідомлень над кнопкою «Reserve Now» останнє — «size must be between 3 and 18» (обмеження довжини Firstname) — не містить назви поля. Бронювання не створено.

### Expected result
Повідомлення називає поле і причину, наприклад «Firstname must be between 3 and 18 characters» (GUI-60).

### Environment
Див. «Середовище»; Chrome, інкогніто; без входу (гість); 28.10.2026.

### Attachments
[BUG-003-1.png](attachments/BUG-003-1.png) — той самий знімок, що й у BUG-003 (останнє повідомлення в переліку).

---

## BUG-007

**Title:** Форма бронювання: при Email без «@» помилка «must be a well-formed email address» не називає поле

**Bug ID:** BUG-007 **Reporter:** Дятлов М. **Date:** 28.10.2026 **Reviewer:** — **Status:** New
**Severity:** Minor **Priority:** P3
**Linked:** test case TC-020; requirement GUI-60, F-60

### Description
GUI-60 вимагає, щоб кожне повідомлення про помилку називало поле і причину. При Email без символу «@» форма показує «must be a well-formed email address» без назви поля Email. Перевірка формату сама працює (бронювання відхилено), дефект — лише в тексті повідомлення.
Severity Minor (4.12): проблема інтерфейсу; з контексту поле можна вгадати, але вимогу не виконано. Priority P3 (4.13): інших кейсів не блокує.

### Steps to reproduce
1. Відкрити у Chrome (нова сесія, інкогніто) адресу http://localhost:3003 (див. «Середовище»).
2. У поле Check In ввести дату «сьогодні + 125 днів» (мм/дд/рррр).
3. У поле Check Out ввести дату «сьогодні + 126 днів» (мм/дд/рррр).
4. Натиснути «Check Availability».
5. Натиснути «Book now» на картці номера 103 (Suite, £225 per night).
6. Натиснути «Reserve Now» на сторінці номера.
7. У поле Firstname ввести «T3-Guest».
8. У поле Lastname ввести «Tester».
9. У поле Email ввести «t3.guest.example.com».
10. У поле Phone ввести «+380001234567».
11. Натиснути «Reserve Now» у формі.

### Actual result
Над кнопкою «Reserve Now» показано одне повідомлення «must be a well-formed email address» без назви поля. Бронювання не створено.

### Expected result
Повідомлення називає поле і причину, наприклад «Email must be a well-formed email address» (GUI-60).

### Environment
Див. «Середовище»; Chrome, інкогніто; без входу (гість); 28.10.2026.

### Attachments
[BUG-007-1.png](attachments/BUG-007-1.png) — форма бронювання після кроку 11 із даними T3-Guest / Tester / t3.guest.example.com / +380001234567 і повідомленням «must be a well-formed email address».
