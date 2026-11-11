package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юніт-тести PriceCalculator: BL-30 (вихідні ночі), BL-40 (сезони), BL-50 (вартість).
 * Очікувані значення взято з розділу 5 SRS 1.1 і пораховано вручну (docs/sprint4/coverage-matrix.md),
 * а не з поведінки коду. Автор: Андрущенко К.; рецензент: Мукоїд Е.
 */
@DisplayName("СтейНау — PriceCalculator (BL-30, BL-40, BL-50)")
class PriceCalculatorTest {

    private PriceCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new PriceCalculator();
    }

    // ---------- BL-30 ----------

    // BUG-010 (module): isWeekendNight рахує вихідними суботу та неділю замість п'ятниці та суботи
    @ParameterizedTest(name = "{0} → вихідна ніч: {1}")
    @DisplayName("BL-30: вихідні ночі — п'ятниця і субота (межі тижня з обох боків)")
    @CsvSource({
            "2026-11-12, false",   // четвер — день перед межею
            "2026-11-13, true",    // п'ятниця
            "2026-11-14, true",    // субота
            "2026-11-15, false"    // неділя — день після межі
    })
    void weekendNightIsFridayAndSaturday(LocalDate night, boolean expected) {
        assertEquals(expected, calc.isWeekendNight(night), "isWeekendNight(" + night + ")");
    }

    // ---------- BL-40 ----------

    @ParameterizedTest(name = "{0} → ×{1}")
    @DisplayName("BL-40: сезонний коефіцієнт на кожній межі сезону з обох боків")
    @CsvSource({
            "2026-05-31, 1.00", "2026-06-01, 1.30",   // початок високого (літо)
            "2026-08-31, 1.30", "2026-09-01, 1.00",   // кінець високого (літо)
            "2026-12-19, 1.00", "2026-12-20, 1.30",   // початок високого (зима)
            "2027-01-10, 1.30", "2027-01-11, 1.00",   // кінець високого (зима)
            "2027-01-31, 1.00", "2027-02-01, 0.80",   // початок низького (лютий)
            "2027-03-31, 0.80", "2027-04-01, 1.00",   // кінець низького (березень)
            "2026-10-31, 1.00", "2026-11-01, 0.80",   // початок низького (листопад)
            "2026-11-30, 0.80", "2026-12-01, 1.00"    // кінець низького (листопад)
    })
    void seasonFactorAtBoundaries(LocalDate night, BigDecimal expected) {
        BigDecimal actual = calc.seasonFactor(night);
        assertEquals(0, expected.compareTo(actual), "seasonFactor(" + night + "): очікувалось " + expected + ", отримано " + actual);
    }

    // ---------- BL-50 ----------

    // BUG-011 (module): nightPrice не враховує 15% надбавку за вихідні (наприклад, 2026-09-18 п'ятниця повертає 100.00 замість 115.00)
    @ParameterizedTest(name = "тариф 100, ніч {0} → {1}")
    @DisplayName("BL-50: вартість ночі = тариф × сезон × 1,15 для вихідної")
    @CsvSource({
            "2026-09-14, 100.00",   // понеділок, звичайний сезон
            "2026-09-18, 115.00",   // п'ятниця, звичайний сезон: ×1,15
            "2026-07-04, 149.50",   // субота, високий сезон: ×1,30 ×1,15
            "2026-11-10, 80.00"     // вівторок, низький сезон: ×0,80
    })
    void nightPriceFormula(LocalDate night, BigDecimal expected) {
        assertEquals(expected, calc.nightPrice(new BigDecimal("100"), night), "nightPrice(100, " + night + ")");
    }

    // BUG-011 (module): помилка округлення вартісно-сезонних націнок для ночей у п'ятницю
    @Test
    @DisplayName("BL-50: округлення HALF_UP до 2 знаків")
    void nightPriceIsRoundedHalfUp() {
        assertAll("Округлення вартості ночі",
                // 99.99 × 1,15 = 114,9885 → 114.99 (п'ятниця, звичайний сезон)
                () -> assertEquals(new BigDecimal("114.99"),
                        calc.nightPrice(new BigDecimal("99.99"), LocalDate.of(2026, 9, 18))),
                // 33.33 × 1,30 × 1,15 = 49,82835 → 49.83 (п'ятниця, високий сезон)
                () -> assertEquals(new BigDecimal("49.83"),
                        calc.nightPrice(new BigDecimal("33.33"), LocalDate.of(2026, 6, 5))));
    }

    // BUG-012 (module): знижка за тривале проживання починається з 6 ночей замість 7 (повертає 615.00 замість 630.00)
    @Test
    @DisplayName("BL-50: знижка 10 % починається з 7 ночей (межа 6 / 7)")
    void longStayDiscountStartsAtSevenNights() {
        BigDecimal rate = new BigDecimal("100");
        LocalDate checkIn = LocalDate.of(2026, 9, 14); // понеділок
        assertAll("Межа знижки за тривале проживання",
                // 6 ночей (виїзд 09-20): 4 × 100 + 2 × 115 = 630.00, без знижки
                () -> assertEquals(new BigDecimal("630.00"), calc.total(rate, checkIn, LocalDate.of(2026, 9, 20)), "6 ночей"),
                // 7 ночей (виїзд 09-21): (5 × 100 + 2 × 115) × 0,90 = 657.00
                () -> assertEquals(new BigDecimal("657.00"), calc.total(rate, checkIn, LocalDate.of(2026, 9, 21)), "7 ночей"));
    }

    // BUG-011 (module): невірний підсумок через неправильний розрахунок суботньої/п'ятничної ночі на межі сезону
    @Test
    @DisplayName("BL-50: сума ночей через межу сезону — кожна ніч за власним коефіцієнтом")
    void totalSumsNightsAcrossSeasonBoundary() {
        // 2026-05-30 (сб) 115.00 + 05-31 (нд) 100.00 + 06-01 (пн, високий) 130.00 = 345.00
        assertEquals(new BigDecimal("345.00"),
                calc.total(new BigDecimal("100"), LocalDate.of(2026, 5, 30), LocalDate.of(2026, 6, 2)));
    }

    @ParameterizedTest(name = "тариф {0}, {1} → {2}: виняток")
    @DisplayName("BL-50: недодатний тариф або виїзд не пізніше заїзду → IllegalArgumentException")
    @CsvSource({
            "0,   2026-09-14, 2026-09-16",   // тариф 0
            "-1,  2026-09-14, 2026-09-16",   // від'ємний тариф
            "100, 2026-09-14, 2026-09-14",   // виїзд = заїзд
            "100, 2026-09-14, 2026-09-13"    // виїзд раніше заїзду
    })
    void totalWithInvalidArgumentsThrows(BigDecimal rate, LocalDate checkIn, LocalDate checkOut) {
        assertThrows(IllegalArgumentException.class, () -> calc.total(rate, checkIn, checkOut));
    }
}