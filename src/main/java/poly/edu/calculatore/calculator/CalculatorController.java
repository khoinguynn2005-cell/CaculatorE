package poly.edu.calculatore.calculator;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calculator")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/calculate")
    public CalculationResponse calculate(@RequestBody CalculationRequest request) {
        return new CalculationResponse(calculatorService.calculate(request));
    }

    @ExceptionHandler({IllegalArgumentException.class, ArithmeticException.class, NullPointerException.class})
    public ResponseEntity<Map<String, String>> handleInvalidCalculation(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", exception.getMessage() == null ? "Dữ liệu phép tính không hợp lệ." : exception.getMessage()));
    }
}
