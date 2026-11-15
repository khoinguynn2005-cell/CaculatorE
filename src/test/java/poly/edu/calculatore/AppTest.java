package poly.edu.calculatore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

import poly.edu.calculatore.calculator.CalculationRequest;
import poly.edu.calculatore.calculator.CalculatorService;

/**
 * Bài 1 - Demo vòng đời kiểm thử JUnit 5.
 *
 * Thứ tự chạy mong đợi:
 *   @BeforeAll (1 lần)
 *     -> [@BeforeEach -> @Test -> @AfterEach]  lặp lại cho từng test
 *   @AfterAll (1 lần)
 *
 * Test bị @Disabled sẽ bị bỏ qua hoàn toàn: KHÔNG chạy @BeforeEach/@AfterEach cho nó.
 */
@DisplayName("Bài 1 - Vòng đời kiểm thử JUnit với CalculatorService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // cố định thứ tự để log dễ đọc
class AppTest {

    // static: dùng chung cho cả class, sống qua mọi test
    private static int testCounter;

    // không static: mỗi test có một instance AppTest mới -> một calculator mới
    private CalculatorService calculator;

    @BeforeAll
    static void initAll() {
        testCounter = 0;
        System.out.println("==================================================");
        System.out.println("[BeforeAll ] Chạy 1 lần duy nhất TRƯỚC tất cả test");
        System.out.println("==================================================");
    }

    @BeforeEach
    void init(TestInfo testInfo) {
        testCounter++;
        calculator = new CalculatorService();
        System.out.println();
        System.out.println("  [BeforeEach] #" + testCounter + " Khởi tạo Calculator cho: "
                + testInfo.getDisplayName());
    }

    @Test
    @Order(1)
    @DisplayName("Test 1 - Cộng hai số: 2 + 3 = 5")
    void testAdd() {
        System.out.println("    [Test     ] Đang chạy: 2 + 3");
        assertEquals("5", calculate("2", "3", "add"));
    }

    @Test
    @Order(2)
    @Disabled("Demo @Disabled - test này bị tạm tắt")
    @DisplayName("Test 2 - Bị vô hiệu hóa")
    void testDisabled() {
        System.out.println("    [Test     ] Dòng này sẽ KHÔNG bao giờ được in ra");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3 - Chia cho 0 phải ném ArithmeticException")
    void testDivideByZero() {
        System.out.println("    [Test     ] Đang chạy: 10 / 0");
        assertThrows(ArithmeticException.class, () -> calculate("10", "0", "divide"));
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        calculator = null; // dọn dẹp tài nguyên của test vừa chạy
        System.out.println("  [AfterEach ] Dọn dẹp sau: " + testInfo.getDisplayName());
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("[AfterAll  ] Chạy 1 lần duy nhất SAU tất cả test");
        System.out.println("             Tổng số test đã chạy: " + testCounter);
        System.out.println("==================================================");
    }

    private String calculate(String left, String right, String operation) {
        return calculator.calculate(new CalculationRequest(left, right, operation));
    }
}
