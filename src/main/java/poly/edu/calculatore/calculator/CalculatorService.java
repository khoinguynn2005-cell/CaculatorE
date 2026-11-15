package poly.edu.calculatore.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public String calculate(CalculationRequest request) {
        Objects.requireNonNull(request, "Thông tin phép tính không được để trống.");
        Objects.requireNonNull(request.left(), "Số thứ nhất không được để trống.");
        Objects.requireNonNull(request.operation(), "Phép tính không được để trống.");

        BigDecimal left = parse(request.left());
        BigDecimal result;

        if ("percent".equals(request.operation())) {
            result = left.divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP);
        } else {
            Objects.requireNonNull(request.right(), "Số thứ hai không được để trống.");
            BigDecimal right = parse(request.right());
            result = switch (request.operation()) {
                case "add" -> left.add(right);
                case "subtract" -> left.subtract(right);
                case "multiply" -> left.multiply(right);
                case "divide" -> divide(left, right);
                default -> throw new IllegalArgumentException("Phép tính không được hỗ trợ.");
            };
        }

        return format(result);
    }

    private BigDecimal divide(BigDecimal left, BigDecimal right) {
        if (right.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Không thể chia cho 0.");
        }
        return left.divide(right, 12, RoundingMode.HALF_UP);
    }

    private BigDecimal parse(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException exception) {
            NumberFormatException clearMessage = new NumberFormatException("Giá trị '" + value + "' không phải là số hợp lệ.");
            clearMessage.initCause(exception);
            throw clearMessage;
        }
    }

    private String format(BigDecimal value) {
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.compareTo(BigDecimal.ZERO) == 0 ? "0" : normalized.toPlainString();
    }
}
