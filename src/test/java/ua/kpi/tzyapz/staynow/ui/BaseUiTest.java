package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Спільна основа UI-тестів «СтейНау»: окремий браузер на кожен тест, явні очікування (15 с),
 * без Thread.sleep і implicitlyWait (demo спільний і відповідає повільно).
 * Запуск: mvn test -DexcludedGroups=none -Dtest=AdminLoginTest
 */
abstract class BaseUiTest {

    protected static final String BASE_URL = "https://automationintesting.online/";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** Чекає, поки елемент стане видимим, і повертає його. */
    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Очищає поле і вводить текст; порожній текст — поле не чіпаємо. */
    protected void type(By locator, String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        WebElement field = waitVisible(locator);
        field.clear();
        field.sendKeys(text);
    }

    /** Для «м'яких» перевірок: true, якщо умова виконалась за 15 с, інакше false (без винятку). */
    protected boolean eventually(Function<WebDriver, Boolean> condition) {
        try {
            return Boolean.TRUE.equals(wait.until(condition));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
