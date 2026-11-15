Dưới đây là nội dung `README.md` để bạn copy. README viết bằng tiếng Việt, theo các mục thường dùng trong dự án: giới thiệu, tính năng, công nghệ, hướng dẫn chạy, API, kiểm thử và cấu trúc thư mục.

```markdown
# CalculatorE

Ứng dụng máy tính trên web được xây dựng bằng Spring Boot. Người dùng có thể thực hiện các phép tính cơ bản qua giao diện trình duyệt hoặc gửi yêu cầu trực tiếp đến REST API.

## Mục tiêu dự án

- Thực hành xây dựng ứng dụng web bằng Spring Boot và Spring MVC.
- Tách phần giao diện, bộ điều khiển và logic tính toán.
- Thực hành xử lý ngoại lệ và kiểm thử các trường hợp dữ liệu không hợp lệ.
- Tạo giao diện máy tính dễ sử dụng trên máy tính và thiết bị di động.

## Tính năng

- Cộng, trừ, nhân và chia.
- Tính phần trăm bằng cách chia giá trị đang hiển thị cho 100.
- Đổi dấu số dương và số âm.
- Xóa toàn bộ dữ liệu hoặc xóa ký tự cuối bằng phím Backspace.
- Nhập phép tính bằng các nút trên giao diện hoặc bàn phím.
- Hiển thị thông báo khi phép tính không hợp lệ, chẳng hạn chia cho 0.
- Trả kết quả qua REST API.
- Không cần cơ sở dữ liệu.

### Phím tắt

| Phím | Chức năng |
|---|---|
| `0`–`9` | Nhập số |
| `.` hoặc `,` | Nhập dấu thập phân |
| `+`, `-`, `*`, `/` | Chọn phép tính |
| `Enter` hoặc `=` | Tính kết quả |
| `Escape` | Xóa toàn bộ |
| `Backspace` | Xóa ký tự cuối |
| `%` | Chuyển giá trị đang hiển thị thành phần trăm |

## Công nghệ sử dụng

- Java 17
- Spring Boot 4.1.2
- Spring Web MVC
- Thymeleaf
- HTML, CSS và JavaScript
- Maven
- JUnit 5

## Yêu cầu trước khi chạy

- JDK 17 trở lên
- IntelliJ IDEA hoặc một IDE hỗ trợ Maven
- Maven Wrapper đi kèm dự án hoặc Maven đã được cài đặt

## Cách chạy ứng dụng

### Chạy bằng IntelliJ IDEA

1. Mở thư mục dự án `CalculatorE`.
2. Chờ IntelliJ tải cấu hình Maven trong `pom.xml`.
3. Mở lớp `CalculatorEApplication`.
4. Chạy phương thức `main`.
5. Mở trình duyệt tại:

   [http://localhost:8080](http://localhost:8080)

### Chạy bằng Maven Wrapper

Trên Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Trên macOS hoặc Linux:

```bash
./mvnw spring-boot:run
```

Nếu cổng mặc định đang được sử dụng, có thể chạy ứng dụng trên cổng khác. Ví dụ với cổng `8082` trên Windows PowerShell:

```powershell
.\mvnw.cmd "-Dspring-boot.run.arguments=--server.port=8082" spring-boot:run
```

Sau đó mở:

[http://localhost:8082](http://localhost:8082)

## Chạy kiểm thử

Trên Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Trên macOS hoặc Linux:

```bash
./mvnw test
```

Trong IntelliJ IDEA, có thể mở cửa sổ **Maven**, chọn `CalculatorE → Lifecycle → test` và chạy goal `test`.

Các báo cáo kiểm thử chi tiết được lưu trong:

```text
target/surefire-reports
```

### Phạm vi kiểm thử hiện tại

`CalculatorExceptionTest` kiểm thử các ngoại lệ và phép tính hợp lệ. `CalculatorEApplicationTests` kiểm tra ứng dụng khởi động được. Lần chạy kiểm thử gần nhất có 11 test, không có lỗi hoặc test thất bại.

## REST API

### Tính toán

```http
POST /api/calculator/calculate
Content-Type: application/json
```

URL đầy đủ khi chạy cục bộ:

```text
http://localhost:8080/api/calculator/calculate
```

Các phép tính được hỗ trợ:

| `operation` | Phép tính |
|---|---|
| `add` | Cộng |
| `subtract` | Trừ |
| `multiply` | Nhân |
| `divide` | Chia |
| `percent` | Chia số thứ nhất cho 100 |

### Ví dụ yêu cầu cộng

```json
{
  "left": "7",
  "right": "5",
  "operation": "add"
}
```

Phản hồi thành công:

```json
{
  "result": "12"
}
```

### Ví dụ yêu cầu chia cho 0

```json
{
  "left": "10",
  "right": "0",
  "operation": "divide"
}
```

Phản hồi lỗi có mã HTTP `400 Bad Request`:

```json
{
  "message": "Không thể chia cho 0."
}
```

### Ví dụ yêu cầu tính phần trăm

```json
{
  "left": "25",
  "operation": "percent"
}
```

Kết quả là `0.25`. Phép `%` trong ứng dụng chuyển số đang hiển thị thành số thập phân bằng cách chia cho 100.

## Xử lý ngoại lệ

| Ngoại lệ | Trường hợp phát sinh |
|---|---|
| `ArithmeticException` | Chia cho 0 |
| `NumberFormatException` | Chuỗi đầu vào không thể chuyển thành số |
| `NullPointerException` | Request hoặc toán hạng bắt buộc bị `null` |
| `IllegalArgumentException` | Tên phép tính không được hỗ trợ |

Các lỗi đầu vào được controller chuyển thành phản hồi HTTP 400 có thông báo lỗi. Số âm được chấp nhận vì ứng dụng hỗ trợ chức năng đổi dấu.

Phép chia sử dụng `BigDecimal`, làm tròn `HALF_UP` đến tối đa 12 chữ số thập phân. Kết quả được loại bỏ các số 0 thừa ở cuối.

## Cấu trúc dự án

```text
CalculatorE/
├── src/
│   ├── main/
│   │   ├── java/poly/edu/calculatore/
│   │   │   ├── CalculatorEApplication.java
│   │   │   ├── calculator/
│   │   │   │   ├── CalculationRequest.java
│   │   │   │   ├── CalculationResponse.java
│   │   │   │   ├── CalculatorController.java
│   │   │   │   └── CalculatorService.java
│   │   │   └── web/
│   │   │       └── HomeController.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/calculator.css
│   │       │   └── js/calculator.js
│   │       ├── templates/calculator.html
│   │       └── application.properties
│   └── test/
│       └── java/poly/edu/calculatore/
│           ├── CalculatorEApplicationTests.java
│           └── CalculatorExceptionTest.java
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Vai trò các thành phần chính

- `HomeController`: nhận yêu cầu `GET /` và mở trang máy tính.
- `CalculatorController`: nhận yêu cầu tính toán tại `POST /api/calculator/calculate` và trả kết quả hoặc thông báo lỗi.
- `CalculatorService`: thực hiện phép tính và kiểm tra dữ liệu đầu vào.
- `CalculationRequest`: chứa hai số và tên phép tính được gửi đến API.
- `CalculationResponse`: chứa kết quả trả về từ API.
- `calculator.html`: cấu trúc trang máy tính.
- `calculator.css`: giao diện và bố cục responsive.
- `calculator.js`: xử lý nút bấm, phím tắt và gửi yêu cầu đến API.
- `CalculatorExceptionTest`: kiểm thử ngoại lệ và các trường hợp tính toán hợp lệ.

## Giới hạn hiện tại

- Ứng dụng chưa lưu lịch sử phép tính.
- Ứng dụng chưa có đăng nhập hoặc phân quyền.
- Dữ liệu không được lưu sau khi đóng hoặc tải lại trang.
- Phép chia được làm tròn đến 12 chữ số thập phân.
```
