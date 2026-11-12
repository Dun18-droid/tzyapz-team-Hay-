package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести BookingValidator: правила BL-10 (дати, ночі, гості) і BL-20 (email, телефон) SRS 1.1.
 * Очікувані значення взято з вимог (docs/sprint4/coverage-matrix.md), а не з поведінки коду.
 * Автор: Мукоїд Е.
 */
@Tag("unit")
@DisplayName("BookingValidator — BL-10 дати і гості, BL-20 email і телефон")
class BookingValidatorTest {

    /** Фіксована «поточна дата», щоб тести не залежали від дня запуску. */
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 10);
    /** Місткість номера для перевірки кількості гостей. */
    private static final int CAPACITY = 2;
    /** Дата заїзду для тестів, де заїзд не перевіряється: пізніше за TODAY. */
    private static final LocalDate CHECK_IN = LocalDate.of(2026, 11, 20);

    private BookingValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();
    }

    // ---------- BL-10: кількість ночей 1..30 включно ----------
    // BUG-008 (module): на v1 30 ночей відхиляється з NIGHTS_OUT_OF_RANGE — тест лишається червоним до виправлення.

    @ParameterizedTest(name = "BL-10: {0} ночей → помилка ночей: {1}")
    @CsvSource({
            "1,  false",
            "30, false",
            "31, true"
    })
    void nightsOutsideOneToThirtyAreRejected(int nights, boolean expectError) {
        // arrange
        LocalDate checkOut = CHECK_IN.plusDays(nights);
        // act
        List<BookingError> errors = validator.validate(CHECK_IN, checkOut, 1, CAPACITY, TODAY);
        // assert
        assertEquals(expectError, errors.contains(BookingError.NIGHTS_OUT_OF_RANGE),
                "Ночей: " + nights + ", отримано помилки: " + errors);
    }

    // ---------- BL-10: дата виїзду пізніша за дату заїзду ----------

    @ParameterizedTest(name = "BL-10: виїзд = заїзд {0} дн. → помилка порядку дат: {1}")
    @CsvSource({
            "-1, true",
            "0,  true",
            "1,  false"
    })
    void checkOutNotAfterCheckInIsRejected(int offsetDays, boolean expectError) {
        LocalDate checkOut = CHECK_IN.plusDays(offsetDays);

        List<BookingError> errors = validator.validate(CHECK_IN, checkOut, 1, CAPACITY, TODAY);

        assertEquals(expectError, errors.contains(BookingError.CHECK_OUT_NOT_AFTER_CHECK_IN),
                "checkOut = checkIn " + offsetDays + " дн., отримано: " + errors);
    }

    // ---------- BL-10: дата заїзду не раніша за today ----------
    // BUG-009 (module): на v1 заїзд today − 1 приймається без CHECK_IN_IN_PAST — тест лишається червоним до виправлення.

    @ParameterizedTest(name = "BL-10: заїзд {0} (today = 2026-11-10) → помилка минулої дати: {1}")
    @CsvSource({
            "2026-11-09, true",
            "2026-11-10, false",
            "2026-11-11, false"
    })
    void checkInBeforeTodayIsRejected(LocalDate checkIn, boolean expectError) {
        LocalDate checkOut = checkIn.plusDays(2);

        List<BookingError> errors = validator.validate(checkIn, checkOut, 1, CAPACITY, TODAY);

        assertEquals(expectError, errors.contains(BookingError.CHECK_IN_IN_PAST),
                "Заїзд " + checkIn + ", отримано: " + errors);
    }

    // ---------- BL-10: гостей від 1 до capacity включно ----------

    @ParameterizedTest(name = "BL-10: {0} гостей при місткості 2 → помилка гостей: {1}")
    @CsvSource({
            "0, true",
            "1, false",
            "2, false",
            "3, true"
    })
    void guestsOutsideOneToCapacityAreRejected(int guests, boolean expectError) {
        LocalDate checkOut = CHECK_IN.plusDays(2);

        List<BookingError> errors = validator.validate(CHECK_IN, checkOut, guests, CAPACITY, TODAY);

        assertEquals(expectError, errors.contains(BookingError.GUESTS_OUT_OF_RANGE),
                "Гостей: " + guests + ", отримано: " + errors);
    }

    // ---------- BL-20: email «локальна_частина@домен.зона» ----------

    @ParameterizedTest(name = "BL-20: email «{0}» → коректний: {1}")
    @CsvSource({
            "guest@example.com, true",
            "guestexample.com,  false",
            "guest@example,     false",
            "@example.com,      false",
            "guest@.com,        false"
    })
    void emailFormat(String email, boolean expectedValid) {
        assertEquals(expectedValid, validator.isValidEmail(email), "Email: " + email);
    }

    // ---------- BL-20: телефон «+380» і рівно 9 цифр ----------

    @ParameterizedTest(name = "BL-20: телефон «{0}» → коректний: {1}")
    @CsvSource({
            "+380501234567,  true",   // +380 і 9 цифр
            "+38050123456,   false",  // 8 цифр
            "+3805012345678, false",  // 10 цифр
            "380501234567,   false",  // без «+»
            "+381501234567,  false",  // не +380
            "+38050123456a,  false"   // літера замість цифри
    })
    void phoneFormat(String phone, boolean expectedValid) {
        assertEquals(expectedValid, validator.isValidPhone(phone), "Телефон: " + phone);
    }

    // ---------- BL-20: validate(…, email, phone) додає INVALID_EMAIL і INVALID_PHONE ----------

    @Test
    @DisplayName("BL-20: некоректні email і телефон додають обидва коди до списку помилок")
    void invalidContactsAddEmailAndPhoneCodes() {
        // arrange — дати й гості коректні, неправильні лише контакти
        LocalDate checkOut = CHECK_IN.plusDays(2);
        // act
        List<BookingError> errors = validator.validate(CHECK_IN, checkOut, 1, CAPACITY, TODAY,
                "guestexample.com", "+38050123456");
        // assert — м'яка перевірка: звіт покаже обидві розбіжності одразу
        assertAll("Коди контактних даних",
                () -> assertTrue(errors.contains(BookingError.INVALID_EMAIL), "Немає INVALID_EMAIL: " + errors),
                () -> assertTrue(errors.contains(BookingError.INVALID_PHONE), "Немає INVALID_PHONE: " + errors)
        );
    }
}
