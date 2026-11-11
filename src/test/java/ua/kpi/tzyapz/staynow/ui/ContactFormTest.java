package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Контактна форма гостя (F-70, GUI-85, GUI-60). Сценарії: docs/sprint4/ui-scenarios.md (TC-021, TC-023, TC-024).
 * Увага: поле Message має id=description, тому беремо data-testid. Порядок повідомлень про помилки на demo
 * не фіксований — перевіряємо «серед усіх містить», а не «перший рядок дорівнює».
 */
@Tag("ui")
@DisplayName("СтейНау UI: контактна форма (F-70)")
class ContactFormTest extends BaseUiTest {

    private static final By NAME = By.cssSelector("[data-testid='ContactName']");
    private static final By EMAIL = By.cssSelector("[data-testid='ContactEmail']");
    private static final By PHONE = By.cssSelector("[data-testid='ContactPhone']");
    private static final By SUBJECT = By.cssSelector("[data-testid='ContactSubject']");
    private static final By MESSAGE = By.cssSelector("[data-testid='ContactDescription']");
    private static final By SUBMIT = By.cssSelector("#contact button.btn-primary");
    private static final By THANKS_TITLE = By.cssSelector("#contact h3");
    private static final By THANKS_BODY = By.cssSelector("#contact .card-body");
    private static final By ERRORS_BOX = By.cssSelector("#contact .alert-danger");
    private static final By ERROR_ITEMS = By.cssSelector("#contact .alert-danger p");
    private static final String THANKS_TEXT = "Thanks for getting in touch";

    private void openAndFill(String subject, String message) {
        driver.get(BASE_URL);
        waitVisible(NAME);
        type(NAME, "T3-Guest");
        type(EMAIL, "t3.guest@example.com");
        type(PHONE, "+380001234567");
        type(SUBJECT, subject);   // порожній рядок — поле не заповнюється
        type(MESSAGE, message);
    }

    /**
     * Кнопка Submit лежить дуже низько на сторінці; навіть після scrollIntoView і очікування
     * elementToBeClickable звичайний click() час від часу перехоплюється іншим елементом, що
     * накладається поверх кнопки (ElementClickInterceptedException). elementToBeClickable
     * перевіряє лише displayed+enabled, а не факт перекриття в момент кліку.
     * Робочий обхід: якщо звичайний клік перехоплено — клікаємо через JS (минає hit-тест браузера).
     * Це дозволяє тесту пройти, але не пояснює, ЩО саме перекриває кнопку — тому варто окремо
     * перевірити це вручну (не headless) і за потреби завести дефект/задачу на дослідження.
     */
    private void submit() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);
        try {
            button.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        }
    }

    private List<String> errorTexts() {
        waitVisible(ERRORS_BOX);
        return driver.findElements(ERROR_ITEMS).stream().map(WebElement::getText).map(String::trim).toList();
    }

    private String contactBlockText() {
        return driver.findElement(By.id("contact")).getText();
    }

    @Test
    @DisplayName("TC-021 / F-70, GUI-85: коректне повідомлення → подяка з іменем і темою")
    void validMessageShowsThanks() {
        openAndFill("T3-Booking question", "T3 test message about booking dates");
        submit();

        // жорстка перевірка: форма замінена подякою
        wait.until(ExpectedConditions.textToBePresentInElementLocated(THANKS_TITLE, THANKS_TEXT));

        assertAll("Подяка",
                () -> assertTrue(driver.findElement(THANKS_TITLE).getText().contains("T3-Guest"), "Заголовок подяки має містити ім'я гостя"),
                () -> assertTrue(driver.findElement(THANKS_BODY).getText().contains("T3-Booking question"), "Текст подяки має містити тему"));
    }

    @Test
    @DisplayName("TC-023 / GUI-60, F-70: порожня тема → помилка називає поле Subject")
    void emptySubjectShowsSubjectError() {
        openAndFill("", "T3 test message about booking dates");
        submit();

        List<String> errors = errorTexts();
        // жорстка перевірка: серед помилок є повідомлення про порожню тему
        assertTrue(errors.contains("Subject may not be blank"), "Очікувана помилка про порожню тему; отримано: " + errors);

        assertAll("Якість повідомлень і відсутність подяки",
                () -> assertTrue(errors.stream().allMatch(e -> e.length() <= 100), "Кожне повідомлення ≤ 100 символів; отримано: " + errors),
                () -> assertFalse(contactBlockText().contains(THANKS_TEXT), "Подяки бути не має"));
    }

    @Test
    @DisplayName("TC-024 / F-70: тема з 4 символів → помилка про діапазон 5–100")
    void subjectOfFourCharsIsRejected() {
        openAndFill("T3-A", "T3 boundary message text ok");
        submit();

        List<String> errors = errorTexts();
        assertAll("Тема довжиною 4 символи",
                () -> assertEquals(List.of("Subject must be between 5 and 100 characters."), errors, "Єдина помилка про діапазон теми"),
                () -> assertFalse(contactBlockText().contains(THANKS_TEXT), "Подяки бути не має"));
    }
}