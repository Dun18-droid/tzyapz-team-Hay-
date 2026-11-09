package ua.kpi.tzyapz.starter;

/** Мінімальний клас-зразок для демонстрації юніт-тестів JUnit 5. */
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Ділення на нуль");
        }
        return a / b;
    }

    /** Факторіал для n у діапазоні 0..12 (більше — переповнення int). */
    public int factorial(int n) {
        if (n < 0 || n > 12) {
            throw new IllegalArgumentException("n має бути в межах 0..12");
        }
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
