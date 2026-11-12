# UI-сценарії для автоматизації — Спринт 4, «СтейНау»

Автор: Мукоїд Е. (тест-аналітик). Перевірено вручну на demo 08.11.2026, Chrome; уточнено 10.11.2026 (TC-036 крок 3, TC-037 — за GUI-70 і F-85).

**Середовище UI-тестів:** публічний demo `https://automationintesting.online` — тести мають запускатися на будь-якій машині з інтернетом (`mvn test -DexcludedGroups=none`), а локальне розгортання (localhost:3003) на машині викладача відсутнє.

## Чому обрано саме ці сценарії

| Критерій | Рішення |
|---|---|
| Пройшли в Run 1 спринта 3 | Усі 5 обраних кейсів — passed; тест має бути «зеленим» |
| Високий пріоритет, повторюються в кожному випуску | Контактна форма (F-70) і вхід адміністратора (F-80) — ключові операції гостя і адміністратора |
| Не залежать від сторінки номера | Сторінка `/reservation/{id}` на demo станом на 08.11.2026 не завантажується (нескінченний індикатор), тому сценарії бронювання (TC-007…TC-020) не автоматизуються |
| Стабільні на спільному demo | Не перевіряємо кількість номерів і повідомлень: інші користувачі demo постійно створюють записи («QA Double …», лічильник Messages) |

## Перелік

| TC | Вимога | Сценарій | Тест-клас і метод |
|---|---|---|---|
| TC-021 | F-70, GUI-85 | Контактна форма з коректними даними → подяка з іменем і темою | `ui/ContactFormTest.validMessageShowsThanks` |
| TC-023 | GUI-60, F-70 | Порожня тема → помилка називає поле Subject | `ui/ContactFormTest.emptySubjectShowsSubjectError` |
| TC-024 | F-70 | Тема 4 символи → помилка про діапазон 5–100 | `ui/ContactFormTest.subjectOfFourCharsIsRejected` |
| TC-036 | F-80 | Вхід admin / password → розділ номерів; Logout → сторінка входу | `ui/AdminLoginTest.validLoginOpensRooms` |
| TC-037 | F-80 | Невірний пароль → «Invalid credentials», вхід не виконано | `ui/AdminLoginTest.wrongPasswordIsRejected` |

Норматив для 3 осіб — ≥4 сценарії; 5-й — запас на випадок нестабільності.

## Локатори

### Контактна форма (головна сторінка, секція `#contact`)

| Елемент | Локатор | Примітка |
|---|---|---|
| Name | `By.cssSelector("[data-testid='ContactName']")` | також `id=name` |
| Email | `By.cssSelector("[data-testid='ContactEmail']")` | також `id=email` |
| Phone | `By.cssSelector("[data-testid='ContactPhone']")` | також `id=phone` |
| Subject | `By.cssSelector("[data-testid='ContactSubject']")` | також `id=subject` |
| Message | `By.cssSelector("[data-testid='ContactDescription']")` | **увага: `id=description`, не `message`** |
| Кнопка Submit | `By.cssSelector("#contact button.btn-primary")` | id і data-testid немає |
| Заголовок подяки | `By.cssSelector("#contact h3")` | текст «Thanks for getting in touch T3-Guest!» |
| Блок подяки | `By.cssSelector("#contact .card-body")` | містить тему жирним `<p>` |
| Помилки | `By.cssSelector("#contact .alert-danger p")` | кожна помилка — окремий `<p>` |

### Адмінпанель (`/admin`)

| Елемент | Локатор | Примітка |
|---|---|---|
| Username | `By.id("username")` | placeholder «Enter username» |
| Password | `By.id("password")` | |
| Кнопка Login | `By.id("doLogin")` | |
| Помилка входу | `By.cssSelector(".alert-danger[role='alert']")` | текст «Invalid credentials» |
| Посилання Rooms | `By.cssSelector("a[href='/admin/rooms']")` | з'являється лише після входу |
| Посилання Report | `By.id("reportLink")` | |
| Кнопка Logout | `By.xpath("//button[text()='Logout']")` | id немає; відносний XPath за текстом |

## Сценарії

### TC-021 — коректне повідомлення через контактну форму

| Крок | Дія | Очікування перед перевіркою | Перевірка |
|---|---|---|---|
| 1 | `get(BASE_URL)` | `visibilityOfElementLocated(ContactName)` | — |
| 2 | Name ← `T3-Guest`; Email ← `t3.guest@example.com`; Phone ← `+380001234567`; Subject ← `T3-Booking question`; Message ← `T3 test message about booking dates` | — | — |
| 3 | клік Submit | `textToBePresentInElementLocated(#contact h3, "Thanks for getting in touch")` | **м'яко** (`assertAll`): заголовок містить «T3-Guest»; `#contact .card-body` містить «T3-Booking question» |

Фактичний результат ручної перевірки 08.11.2026: «Thanks for getting in touch T3-Guest! We'll get back to you about **T3-Booking question** as soon as possible.»

### TC-023 — порожня тема

| Крок | Дія | Очікування | Перевірка |
|---|---|---|---|
| 1 | `get(BASE_URL)` | `visibilityOfElementLocated(ContactName)` | — |
| 2 | Name, Email, Phone, Message — як у TC-021; Subject не заповнювати | — | — |
| 3 | клік Submit | `visibilityOfElementLocated(#contact .alert-danger)` | **жорстко**: серед текстів `#contact .alert-danger p` є «Subject may not be blank»; **м'яко**: кожне повідомлення ≤ 100 символів; заголовка подяки немає |

**Порядок повідомлень на demo не фіксований** — перевіряти «містить серед усіх», а не «перший рядок дорівнює». Для порожньої форми повністю demo показує 8 повідомлень: Phone may not be blank; Phone must be between 11 and 21 characters.; Message must be between 20 and 2000 characters.; Message may not be blank; Email may not be blank; Name may not be blank; Subject may not be blank; Subject must be between 5 and 100 characters.

### TC-024 — тема з 4 символів

| Крок | Дія | Очікування | Перевірка |
|---|---|---|---|
| 1 | `get(BASE_URL)` | `visibilityOfElementLocated(ContactName)` | — |
| 2 | Name, Email, Phone — як у TC-021; Subject ← `T3-A`; Message ← `T3 boundary message text ok` | — | — |
| 3 | клік Submit | `visibilityOfElementLocated(#contact .alert-danger)` | **м'яко**: текст помилки = «Subject must be between 5 and 100 characters.»; заголовка подяки немає |

Фактичний результат ручної перевірки 08.11.2026 (з темою «Abcd»): одне повідомлення «Subject must be between 5 and 100 characters.»

### TC-036 — успішний вхід і вихід

| Крок | Дія | Очікування | Перевірка |
|---|---|---|---|
| 1 | `get(BASE_URL + "admin")` | `visibilityOfElementLocated(id=username)` | — |
| 2 | username ← `admin`; password ← `password`; клік `doLogin` | `urlContains("/admin/rooms")` | **м'яко**: видно кнопку Logout; видно посилання Rooms; у таблиці є номер «101» |
| 3 | клік Logout | `visibilityOfElementLocated(id=username)` | **жорстко**: знову форма входу |

Крок 3 перевіряє F-85 (SRS 1.1): «після Logout система повертає на сторінку входу». Ручна перевірка 10.11.2026: demo після Logout відкриває головну сторінку — розбіжність з F-85, тест червоний і посилається на звіт про дефект.

Не перевіряти кількість номерів у таблиці — її змінюють інші користувачі demo (08.11.2026: десятки записів «QA Double …»).

### TC-037 — невірний пароль

**Не перевіряти відсутність кнопки Logout:** за GUI-70 (SRS 1.1) кнопка «Logout» є в шапці сторінки входу завжди, навіть до входу (кейс TC-040, passed у спринті 3). Ознака невдалого входу — повідомлення, URL і відсутність посилання Rooms.

| Крок | Дія | Очікування | Перевірка |
|---|---|---|---|
| 1 | `get(BASE_URL + "admin")` | `visibilityOfElementLocated(id=username)` | — |
| 2 | username ← `admin`; password ← `T3-wrong`; клік `doLogin` | `visibilityOfElementLocated(.alert-danger[role='alert'])` | **м'яко**: текст = «Invalid credentials»; URL не містить `/admin/rooms`; поле `id=username` лишається на сторінці; посилання Rooms (`a[href='/admin/rooms']`) немає |

## Для Selenium IDE (1–2 сценарії)

Рекомендовано записати TC-021 і TC-037: обидва короткі, без сторінки номера, з явним текстом для `assert text` / `verify text`.

## Загальні правила для тестів

- `BASE_URL = "https://automationintesting.online/"`; `@Tag("ui")`; headless + `--window-size=1280,900`.
- Новий браузер у `@BeforeEach`, `driver.quit()` у `@AfterEach`; без `Thread.sleep` і `implicitlyWait`.
- Тайм-аут `WebDriverWait` — 15 с: demo спільний і відповідає повільно.
- Тестові дані з префіксом `T3-` — щоб відрізняти наші записи від чужих на demo.
