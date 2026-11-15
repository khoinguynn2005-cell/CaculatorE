# Bài 1 – Vòng đời kiểm thử trong JUnit 5

> Code demo: [src/test/java/poly/edu/calculatore/AppTest.java](../src/test/java/poly/edu/calculatore/AppTest.java)
> Lớp được test: [CalculatorService.java](../src/main/java/poly/edu/calculatore/calculator/CalculatorService.java)

---

## 0. Lộ trình học (làm theo thứ tự)

| Bước | Việc cần làm | Đạt mục tiêu nào | Tự kiểm tra |
|---|---|---|---|
| 1 | Đọc mục 1–2, vẽ lại sơ đồ vòng đời ra giấy **không nhìn tài liệu** | Hiểu annotation | Vẽ đúng thứ tự cho 3 test? |
| 2 | Gõ lại `AppTest` theo mẫu của thầy (chỉ `println`) và chạy | Áp dụng annotation | Output giống ảnh mẫu? |
| 3 | Đoán output **trước khi chạy**, rồi chạy để so sánh | Hiểu thứ tự | Đoán đúng 100%? |
| 4 | Thay `println` bằng `CalculatorService` + `assertEquals` thật (mục 4) | Lập test đơn vị | Test xanh, có test chia cho 0 |
| 5 | Dùng AI theo prompt ở mục 5, chỉnh log cho dễ đọc | Yêu cầu dùng AI | Log có tên test, số thứ tự |
| 6 | Làm các thí nghiệm ở mục 7 (bỏ `static`, thêm `@Disabled`…) | Hiểu sâu | Giải thích được lỗi |
| 7 | Viết lại mục 6 bằng lời của mình | Giải thích static | Nói được trong 30 giây |

---

## 1. Vòng đời kiểm thử là gì?

Khi chạy một class test, JUnit **không** chỉ gọi các hàm `@Test`. Nó gọi thêm các hàm "chuẩn bị" và "dọn dẹp" theo một thứ tự cố định. Thứ tự đó gọi là **vòng đời kiểm thử (test lifecycle)**.

Mục đích: mỗi test phải **độc lập** – kết quả test này không được ảnh hưởng test khác. Muốn vậy, mỗi test cần một môi trường "sạch".

### Sơ đồ

```
@BeforeAll  (static, 1 lần)
│
├── tạo instance AppTest MỚI #1
│     @BeforeEach
│     @Test testMethod1
│     @AfterEach
│
├── (@Disabled testMethod2 → bỏ qua, KHÔNG chạy BeforeEach/AfterEach)
│
├── tạo instance AppTest MỚI #2
│     @BeforeEach
│     @Test testMethod3
│     @AfterEach
│
@AfterAll   (static, 1 lần)
```

> Điểm quan trọng nhất: **JUnit tạo một object AppTest mới cho mỗi test.** Hiểu câu này là hiểu được vì sao `@BeforeAll/@AfterAll` phải `static`.

---

## 2. Tác dụng từng annotation

| Annotation | Chạy khi nào | Số lần | Bắt buộc `static`? | Dùng để làm gì (ví dụ) |
|---|---|---|---|---|
| `@BeforeAll` | Trước tất cả test trong class | 1 | **Có** | Mở kết nối DB, đọc file cấu hình, khởi tạo thứ tốn kém |
| `@BeforeEach` | Trước **mỗi** test | N (số test được chạy) | Không | Tạo `new CalculatorService()` mới, reset dữ liệu |
| `@Test` | Đánh dấu một phương thức là test case | — | Không | Chứa logic kiểm thử + `assert...` |
| `@AfterEach` | Sau **mỗi** test (kể cả khi test fail) | N | Không | Gán `null`, xóa file tạm, rollback dữ liệu |
| `@AfterAll` | Sau tất cả test | 1 | **Có** | Đóng kết nối DB, giải phóng tài nguyên |
| `@DisplayName` | — | — | — | Đặt tên dễ đọc cho test/class khi hiển thị kết quả |
| `@Disabled` | — | — | — | Tạm tắt test; JUnit báo *Skipped* |

Một số lưu ý hay bị hỏi:
- `@AfterEach` **vẫn chạy** dù test fail hoặc ném exception → giống khối `finally`.
- Test `@Disabled` **không** kích hoạt `@BeforeEach/@AfterEach`.
- Thứ tự giữa các `@Test` **không được đảm bảo** theo thứ tự viết trong file. Muốn cố định dùng `@TestMethodOrder` + `@Order`.
- So sánh JUnit 4 → 5: `@Before`→`@BeforeEach`, `@After`→`@AfterEach`, `@BeforeClass`→`@BeforeAll`, `@AfterClass`→`@AfterAll`, `@Ignore`→`@Disabled`.

---

## 3. Phiên bản theo mẫu của thầy

```java
public class AppTest {
    @BeforeAll
    static void initAll() { System.out.println("@BeforeAll - Run before all methods once"); }

    @BeforeEach
    void init() { System.out.println("  @BeforeEach - Run before each test methods"); }

    @DisplayName("First test")
    @Test
    void testMethod1() { System.out.println("    Test method 1"); }

    @Test
    @Disabled
    void testMethod2() { System.out.println("    Test method 2"); }

    @Test
    void testMethod3() { System.out.println("    Test method 3"); }

    @AfterEach
    void tearDown() { System.out.println("  @AfterEach - Run after each test methods"); }

    @AfterAll
    static void tearDownAll() { System.out.println("@AfterAll - Run after all test methods once"); }
}
```

Output (giống ảnh trong đề) – chú ý **không có** "Test method 2":

```
@BeforeAll - Run before all methods once
  @BeforeEach - Run before each test methods
    Test method 1
  @AfterEach - Run after each test methods
  @BeforeEach - Run before each test methods
    Test method 3
  @AfterEach - Run after each test methods
@AfterAll - Run after all test methods once
```

---

## 4. Phiên bản nâng cấp – áp dụng vào Calculator (đã chạy thật)

File: [AppTest.java](../src/test/java/poly/edu/calculatore/AppTest.java)

Những gì đã cải tiến so với mẫu:

1. **`@BeforeEach` thực sự khởi tạo `CalculatorService`** – đúng yêu cầu đề "khởi tạo đối tượng Calculator trước mỗi bài kiểm thử".
2. **Test có `assert` thật** (`assertEquals`, `assertThrows`), không chỉ in chữ.
3. **Log có nhãn cố định độ rộng** `[BeforeAll ]`, `[BeforeEach]`, `[Test     ]` → căn thẳng cột, dễ đọc.
4. **Thụt lề theo cấp**: All (0) → Each (2) → Test (4) → nhìn ra ngay cấu trúc lồng nhau.
5. **In tên test** bằng `TestInfo.getDisplayName()` → biết BeforeEach/AfterEach đang phục vụ test nào.
6. **Bộ đếm `static testCounter`** → chứng minh BeforeEach chạy đúng N lần, AfterAll in tổng.
7. **`@TestMethodOrder` + `@Order`** → thứ tự ổn định giữa các lần chạy.

Chạy: `mvnw test -Dtest=AppTest` (hoặc bấm ▶ trong IDE). Output thật:

```
==================================================
[BeforeAll ] Chạy 1 lần duy nhất TRƯỚC tất cả test
==================================================

  [BeforeEach] #1 Khởi tạo Calculator cho: Test 1 - Cộng hai số: 2 + 3 = 5
    [Test     ] Đang chạy: 2 + 3
  [AfterEach ] Dọn dẹp sau: Test 1 - Cộng hai số: 2 + 3 = 5

  [BeforeEach] #2 Khởi tạo Calculator cho: Test 3 - Chia cho 0 phải ném ArithmeticException
    [Test     ] Đang chạy: 10 / 0
  [AfterEach ] Dọn dẹp sau: Test 3 - Chia cho 0 phải ném ArithmeticException

==================================================
[AfterAll  ] Chạy 1 lần duy nhất SAU tất cả test
             Tổng số test đã chạy: 2
==================================================
Tests run: 3, Failures: 0, Errors: 0, Skipped: 1
```

**Quan sát:**
- `BeforeAll`/`AfterAll` mỗi cái xuất hiện đúng 1 lần, bao ngoài cùng.
- Mỗi test được "kẹp" giữa một cặp `BeforeEach`/`AfterEach`.
- Test 2 (`@Disabled`) bị bỏ qua hoàn toàn → bộ đếm chỉ là 2, Maven báo `Skipped: 1`.

---

## 5. Prompt dùng AI (yêu cầu của đề)

Prompt mẫu – copy và chỉnh theo ý:

```
Tôi đang học JUnit 5 (Java 17, Maven). Tôi có lớp CalculatorService với hàm
calculate(CalculationRequest(left, right, operation)) trả về String,
operation gồm "add", "subtract", "multiply", "divide", "percent";
chia cho 0 ném ArithmeticException.

Hãy viết class test AppTest:
1. Có đủ @BeforeAll, @BeforeEach, @Test, @AfterEach, @AfterAll,
   thêm @DisplayName và một test @Disabled.
2. @BeforeEach khởi tạo CalculatorService mới.
3. Có ít nhất 2 test với assert thật (1 test phép cộng, 1 test chia cho 0).
4. Mỗi phương thức vòng đời in log ra console, thụt lề theo cấp,
   có nhãn rõ ràng và in tên test đang chạy (dùng TestInfo).
5. Cố định thứ tự test bằng @TestMethodOrder.
6. Sau code, giải thích ngắn tác dụng từng annotation và
   vì sao @BeforeAll/@AfterAll phải là static.
```

Mẹo viết prompt tốt: nêu **bối cảnh** (phiên bản, lớp cần test) → **yêu cầu đánh số** → **định dạng đầu ra** mong muốn. Sau khi nhận code: **luôn chạy thử**, đừng tin ngay – so output với sơ đồ ở mục 1.

---

## 6. Vì sao `@BeforeAll` / `@AfterAll` phải là `static`? (giải thích bằng lời)

> *Bản mẫu – hãy đọc hiểu rồi viết lại bằng lời của bạn.*

Mặc định JUnit 5 tạo **một object mới của class test cho mỗi phương thức `@Test`** (chế độ `PER_METHOD`). Có 2 test thì có 2 object AppTest khác nhau.

`@BeforeAll` phải chạy **trước khi có bất kỳ test nào**, tức là lúc đó JUnit **chưa có object nào** để gọi phương thức. Tương tự, `@AfterAll` chạy **sau khi mọi test đã xong**, lúc các object đó đã dùng xong. Một phương thức không-static thì phải có object mới gọi được, nên JUnit không biết gọi nó trên object nào. Phương thức `static` thuộc về **class**, không cần object → gọi được bất cứ lúc nào, đúng 1 lần.

Hệ quả: trong `@BeforeAll` chỉ dùng được biến `static` (như `testCounter`), không dùng được biến instance (như `calculator`).

**Ngoại lệ:** nếu gắn `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` lên class, JUnit chỉ tạo **một** object cho cả class → khi đó `@BeforeAll/@AfterAll` **không cần** static. Đổi lại, các test dùng chung trạng thái nên dễ ảnh hưởng nhau.

Tóm gọn 1 câu: *"BeforeAll/AfterAll chạy ngoài phạm vi của bất kỳ object test nào, nên chúng phải thuộc về class – tức là static."*

---

## 7. Thí nghiệm tự làm (học bằng cách phá code)

| # | Thử | Dự đoán của bạn | Kết quả thật |
|---|---|---|---|
| 1 | Xóa `static` ở `initAll()` | | JUnit báo lỗi: `@BeforeAll method ... must be static unless the test class is annotated with @TestInstance(Lifecycle.PER_CLASS)` |
| 2 | Làm bước 1 + thêm `@TestInstance(PER_CLASS)` lên class | | Chạy bình thường |
| 3 | Bỏ `@Disabled` ở test 2 | | Có 3 cặp BeforeEach/AfterEach, counter = 3 |
| 4 | Cố ý cho test 1 fail (`assertEquals("6", ...)`) | | `@AfterEach` vẫn in ra |
| 5 | In `System.identityHashCode(this)` trong `@BeforeEach` | | Mỗi test một số khác → chứng minh object mới mỗi test |
| 6 | Bỏ `@TestMethodOrder`, đổi tên method | | Thứ tự có thể thay đổi |

Điền cột "Dự đoán" **trước** khi chạy – đây là cách nhớ lâu nhất.

---

## 8. Câu hỏi tự kiểm tra

1. Class có 5 `@Test`, 1 cái `@Disabled`. `@BeforeEach` chạy mấy lần? `@BeforeAll`? → *4 lần; 1 lần.*
2. Test ném exception thì `@AfterEach` có chạy không? → *Có.*
3. Vì sao nên tạo `CalculatorService` trong `@BeforeEach` thay vì `@BeforeAll`? → *Mỗi test có object sạch, không chia sẻ trạng thái → test độc lập.*
4. Khi nào nên dùng `@BeforeAll`? → *Khi việc khởi tạo tốn kém và dùng chung được, như kết nối DB, khởi động server.*
5. `@BeforeAll` có truy cập được field `calculator` (không static) không? Vì sao? → *Không, vì chưa có object.*

---

## 9. Checklist nộp bài

- [ ] Class `AppTest` có đủ 5 annotation vòng đời + `@DisplayName` + `@Disabled`
- [ ] `@BeforeEach` khởi tạo Calculator
- [ ] Có test với `assert` thật
- [ ] Ảnh chụp output console đúng thứ tự
- [ ] Prompt đã dùng với AI + phần đã chỉnh sửa log
- [ ] Đoạn giải thích `static` bằng lời của mình
