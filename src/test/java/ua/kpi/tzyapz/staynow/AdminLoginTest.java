package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Вхід адміністратора в адмінпанель (F-80, F-85). Сценарії: docs/sprint4/ui-scenarios.md (TC-036, TC-037). */
@Tag("ui")
@DisplayName("СтейНау UI: вхід в адмінпанель (F-80, F-85)")
class AdminLoginTest extends BaseUiTest {

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN = By.id("doLogin");
    private static final By LOGIN_ERROR = By.cssSelector(".alert-danger[role='alert']");
    private static final By ROOMS_LINK = By.cssSelector("a[href='/admin/rooms']");
    private static final By LOGOUT = By.xpath("//button[text()='Logout']");

    @Test
    @DisplayName("TC-036 / F-80, F-85: вхід admin / password відкриває розділ номерів, Logout веде на головну сторінку")
    void validLoginOpensRooms() {
        driver.get(BASE_URL + "admin");
        waitVisible(USERNAME);
        type(USERNAME, "admin");
        type(PASSWORD, "password");
        driver.findElement(LOGIN).click();

        // жорстка перевірка: після входу відкрито розділ номерів
        wait.until(ExpectedConditions.urlContains("/admin/rooms"));

        // м'які перевірки (assertAll): не кількість записів, бо demo спільний
        boolean logoutVisible = eventually(d -> !d.findElements(LOGOUT).isEmpty() && d.findElement(LOGOUT).isDisplayed());
        boolean roomsLinkVisible = eventually(d -> !d.findElements(ROOMS_LINK).isEmpty() && d.findElement(ROOMS_LINK).isDisplayed());
        boolean room101Listed = eventually(d -> d.findElement(By.tagName("body")).getText().contains("101"));
        assertAll("Після входу",
                () -> assertTrue(logoutVisible, "Кнопка Logout має бути видимою"),
                () -> assertTrue(roomsLinkVisible, "Посилання Rooms має бути видимим"),
                () -> assertTrue(room101Listed, "У таблиці номерів має бути номер 101"));

        // жорстка перевірка (F-85): Logout веде на головну сторінку сайту
        driver.findElement(LOGOUT).click();
        boolean onHomepage = eventually(d -> BASE_URL.equals(d.getCurrentUrl()));
        assertTrue(onHomepage, "Після Logout має відкритися головна сторінка (F-85); поточний URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("TC-037 / F-80: неправильний пароль → «Invalid credentials», доступу до Rooms немає")
    void wrongPasswordIsRejected() {
        driver.get(BASE_URL + "admin");
        waitVisible(USERNAME);
        type(USERNAME, "admin");
        type(PASSWORD, "T3-wrong");
        driver.findElement(LOGIN).click();

        String error = waitVisible(LOGIN_ERROR).getText().trim();

        // За GUI-70 кнопка Logout у шапці сторінки входу присутня завжди (TC-040, спринт 3),
        // тому ознака "вхід не виконано" — відсутність посилання Rooms, а не відсутність Logout.
        boolean roomsLinkVisible = !driver.findElements(ROOMS_LINK).isEmpty() && driver.findElement(ROOMS_LINK).isDisplayed();

        assertAll("Після спроби входу з неправильним паролем",
                () -> assertEquals("Invalid credentials", error, "Текст помилки входу"),
                () -> assertFalse(driver.getCurrentUrl().contains("/admin/rooms"), "Не має відкритися розділ номерів"),
                () -> assertFalse(roomsLinkVisible, "Посилання Rooms не повинно бути видимим"));
    }
}