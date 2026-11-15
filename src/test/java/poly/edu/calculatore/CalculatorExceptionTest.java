package poly.edu.calculatore;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import poly.edu.calculatore.calculator.CalculationRequest;
import poly.edu.calculatore.calculator.CalculatorService;

class CalculatorExceptionTest {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    @Test
    void throwsArithmeticExceptionWhenDividingByZero() {
        ArithmeticException exception = assertThrows(ArithmeticException.class,
                () -> calculate("12", "0", "divide"));
        assertEquals("Không thể chia cho 0.", exception.getMessage());
    }

    @Test
    void throwsArithmeticExceptionWhenDividingByDecimalZero() {
        assertThrows(ArithmeticException.class,
                () -> calculate("-5.5", "0.000", "divide"));
    }

    @Test
    void throwsNumberFormatExceptionWhenLeftValueIsNotANumber() {
        NumberFormatException exception = assertThrows(NumberFormatException.class,
                () -> calculate("abc", "2", "add"));
        assertEquals("Giá trị 'abc' không phải là số hợp lệ.", exception.getMessage());
    }

    @Test
    void throwsNumberFormatExceptionWhenRightValueIsNotANumber() {
        assertThrows(NumberFormatException.class,
                () -> calculate("2", "3x", "multiply"));
    }

    @Test
    void throwsNullPointerExceptionWhenRequestIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> calculatorService.calculate(null));
        assertEquals("Thông tin phép tính không được để trống.", exception.getMessage());
    }

    @Test
    void throwsNullPointerExceptionWhenRequiredOperandIsNull() {
        assertThrows(NullPointerException.class,
                () -> calculate("2", null, "add"));
    }

    @Test
    void throwsIllegalArgumentExceptionWhenOperationIsUnsupported() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calculate("2", "3", "power"));
        assertEquals("Phép tính không được hỗ trợ.", exception.getMessage());
    }

    @Test
    void throwsIllegalArgumentExceptionWhenOperationNameHasWrongCase() {
        assertThrows(IllegalArgumentException.class,
                () -> calculate("2", "3", "ADD"));
    }

    @Test
    void doesNotThrowForValidPositiveCalculation() {
        assertDoesNotThrow(() -> assertEquals("5", calculate("2", "3", "add")));
    }

    @Test
    void doesNotThrowForValidNegativeCalculation() {
        assertDoesNotThrow(() -> assertEquals("-6", calculate("-2", "3", "multiply")));
    }

    private String calculate(String left, String right, String operation) {
        return calculatorService.calculate(new CalculationRequest(left, right, operation));
    }
}
