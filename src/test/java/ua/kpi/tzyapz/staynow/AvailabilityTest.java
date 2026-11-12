package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести Availability: BL-60 (плата за скасування, повернення коштів) і BL-70 (зайнятість номера)
 * SRS 1.1. Очікувані значення взято з вимог і пораховано вручну (docs/sprint4/coverage-matrix.md),
 * а не з поведінки коду. Автор: Дятлов М.; рецензент: Андрущенко К.
 */
@Tag("unit")
@DisplayName("СтейНау — Availability (BL-60 скасування, BL-70 зайнятість номера)")
class AvailabilityTest {

    private static final String ROOM = "101";
    /** Існуюче бронювання номера 101 у кожному тесті: [2026-12-10, 2026-12-15). */
    private static final LocalDate BOOKED_IN = LocalDate.of(2026, 12, 10);
    private static final LocalDate BOOKED_OUT = LocalDate.of(2026, 12, 15);

    private Availability availability;

    @BeforeEach
    void setUp() {
        availability = new Availability();
        availability.book(ROOM, BOOKED_IN, BOOKED_OUT);
    }

    // ---------- BL-60: плата за скасування ----------
    // BUG-014 (module): cancellationFee для рівно 7 днів до заїзду утримує 50 % замість 0 («> 7» замість «≥ 7»).

    @ParameterizedTest(name = "BL-60: {0} дн. до заїзду, вартість 1000.00 → плата {1}")
    @DisplayName("BL-60: ≥7 днів — 0; 3..6 — 50 %; <3 — 100 % (межі 7 і 3 з обох боків)")
    @CsvSource({
            "8, 0.00",
            "7, 0.00",
            "6, 500.00",
            "3, 500.00",
            "2, 1000.00",
            "0, 1000.00"
    })
    void cancellationFeeAtBoundaries(int daysBeforeCheckIn, String expectedFee) {
        // arrange
        BigDecimal total = new BigDecimal("1000.00");
        // act
        BigDecimal fee = availability.cancellationFee(total, daysBeforeCheckIn);
        // assert
        assertEquals(new BigDecimal(expectedFee), fee, "Днів до заїзду: " + daysBeforeCheckIn);
    }

    // BUG-014 (module): наслідок — refund для 7 днів повертає 500.00 замість 1000.00.
    @ParameterizedTest(name = "BL-60: {0} дн. до заїзду, вартість 1000.00 → повернення {1}")
    @DisplayName("BL-60: повернення = вартість − плата за скасування (межі 7, 3 і 2)")
    @CsvSource({
            "7, 1000.00",
            "6, 500.00",
            "3, 500.00",
            "2, 0.00"
    })
    void refundAtBoundaries(int daysBeforeCheckIn, String expectedRefund) {
        BigDecimal total = new BigDecimal("1000.00");

        BigDecimal refund = availability.refund(total, daysBeforeCheckIn);

        assertEquals(new BigDecimal(expectedRefund), refund, "Днів до заїзду: " + daysBeforeCheckIn);
    }

    @Test
    @DisplayName("BL-60: плата і повернення округлюються до копійок (HALF_UP)")
    void feeAndRefundAreRoundedHalfUp() {
        // arrange — 5 днів: 50 % від 333.33 = 166.665 → 166.67; повернення 333.33 − 166.67 = 166.66
        BigDecimal total = new BigDecimal("333.33");
        // act
        BigDecimal fee = availability.cancellationFee(total, 5);
        BigDecimal refund = availability.refund(total, 5);
        // assert — м'яка перевірка: обидва значення одного розрахунку
        assertAll("Округлення BL-60",
                () -> assertEquals(new BigDecimal("166.67"), fee, "Плата"),
                () -> assertEquals(new BigDecimal("166.66"), refund, "Повернення")
        );
    }

    // ---------- BL-70: зайнятість номера, інтервали [checkIn, checkOut) ----------
    // BUG-015 (module): isAvailable вважає зайнятим номер, коли виїзд одного гостя збігається із заїздом
    // іншого (межі інтервалу порівнюються нестрого) — набори [1] і [2] лишаються червоними.

    @ParameterizedTest(name = "BL-70: номер 101 (зайнятий 10.12–15.12), новий {0} → {1}: вільний = {2}")
    @DisplayName("BL-70: напіввідкриті інтервали — виїзд у день заїзду іншого гостя дозволено")
    @CsvSource({
            "2026-12-15, 2026-12-17, true",    // заїзд нового = виїзд існуючого
            "2026-12-08, 2026-12-10, true",    // виїзд нового = заїзд існуючого
            "2026-12-14, 2026-12-16, false",   // перетин із кінця
            "2026-12-09, 2026-12-11, false",   // перетин із початку
            "2026-12-11, 2026-12-13, false",   // всередині існуючого
            "2026-12-09, 2026-12-16, false"    // охоплює існуюче
    })
    void isAvailableForIntervals(LocalDate checkIn, LocalDate checkOut, boolean expectedAvailable) {
        boolean available = availability.isAvailable(ROOM, checkIn, checkOut);

        assertEquals(expectedAvailable, available, "Інтервал " + checkIn + " → " + checkOut);
    }

    @Test
    @DisplayName("BL-70: інший номер не зайнятий тим самим інтервалом")
    void otherRoomIsAvailable() {
        assertTrue(availability.isAvailable("102", BOOKED_IN, BOOKED_OUT));
    }

    @Test
    @DisplayName("BL-70: бронювання зайнятого номера — IllegalStateException")
    void bookingOccupiedRoomThrows() {
        assertThrows(IllegalStateException.class,
                () -> availability.book(ROOM, LocalDate.of(2026, 12, 14), LocalDate.of(2026, 12, 16)));
    }

    // BUG-015 (module): book() для заїзду в день виїзду попереднього гостя кидає IllegalStateException.
    @Test
    @DisplayName("BL-70: бронювання вільного номера (заїзд у день виїзду іншого гостя) реєструється")
    void bookingFreeRoomIsRegistered() {
        // arrange
        LocalDate checkIn = LocalDate.of(2026, 12, 15);
        LocalDate checkOut = LocalDate.of(2026, 12, 17);
        // act
        Availability.Stay stay = availability.book(ROOM, checkIn, checkOut);
        // assert — м'яка перевірка: результат бронювання і стан реєстру
        assertAll("Реєстрація бронювання",
                () -> assertEquals(new Availability.Stay(ROOM, checkIn, checkOut), stay),
                () -> assertEquals(2, availability.stays().size(), "Кількість бронювань"),
                () -> assertTrue(availability.stays().contains(stay), "Реєстр містить нове бронювання")
        );
    }
}
