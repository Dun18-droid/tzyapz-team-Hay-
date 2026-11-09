package ua.kpi.tzyapz.starter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculator — зразок юніт-тестів JUnit 5")
class CalculatorTest {

    private Calculator calc;

    @BeforeEach
    void setUp() {
        calc = new Calculator();
    }

    @Test
    @DisplayName("add: 2 + 3 = 5")
    void addsTwoNumbers() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    @DisplayName("divide: ділення на нуль кидає ArithmeticException")
    void divideByZeroThrows() {
        assertThrows(ArithmeticException.class, () -> calc.divide(1, 0));
    }

    @ParameterizedTest(name = "factorial({0}) = {1}")
    @CsvSource({"0,1", "1,1", "5,120", "12,479001600"})
    void factorialBoundaries(int n, int expected) {
        assertEquals(expected, calc.factorial(n));
    }

    @Test
    @DisplayName("factorial: за межами 0..12 — IllegalArgumentException")
    void factorialOutOfRange() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> calc.factorial(-1)),
            () -> assertThrows(IllegalArgumentException.class, () -> calc.factorial(13))
        );
    }
}
