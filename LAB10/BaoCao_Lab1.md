# BÁO CÁO THỰC HÀNH LAB 1
# HỌC PHẦN: AN TOÀN VÀ BẢO MẬT THÔNG TIN
## CHỦ ĐỀ: MÃ HÓA ĐỐI XỨNG CỔ ĐIỂN
**Công nghệ:** Java Swing (NetBeans GUI Builder compatible) – Apache Maven – Microsoft SQL Server  
**Package:** `week_01` | **Main Form:** `week_01.frm_Menu`

---

## I. THÔNG TIN CHUNG

- **Môn học:** An toàn và Bảo mật Thông tin
- **Bài thực hành:** Lab 01 – Mã hóa đối xứng cổ điển
- **Mục tiêu:**
  1. Nắm vững nguyên lý và thuật toán của 6 hệ mật mã cổ điển: Caesar, Vigenère, Rail Fence, Playfair, Transposition (hoán vị cột) và Autokey.
  2. Xây dựng giao diện trực quan bằng Java Swing, quản lý dự án bằng Apache Maven, xử lý tệp văn bản mã hóa UTF-8.
  3. Tích hợp cơ sở dữ liệu Microsoft SQL Server để lưu trữ lịch sử thao tác an toàn (dùng PreparedStatement phòng chống SQL Injection, lưu mật khẩu an toàn trong bộ nhớ).
  4. Thực nghiệm các kỹ thuật thám mã (phân tích tấn công mật mã): Vét cạn khóa (Brute Force), Phân tích tần suất chữ cái (Frequency Analysis), và Tấn công khi biết một phần bản rõ (Known Plaintext Attack).

---

## II. BỘ DỮ LIỆU KIỂM THỬ BẮT BUỘC (BẢNG 1.1)

Toàn bộ các thuật toán được kiểm tra chéo tự động qua JUnit 5 (`CipherAlgorithmsTest.java`) và kiểm tra trên giao diện người dùng. Kết quả kiểm thử trùng khớp 100% từng ký tự với tài liệu:

| STT | Thuật toán | Plaintext | Khóa (Key) | Ciphertext Thực Tế | Ciphertext Mẫu | Trạng thái |
|:---:|:---|:---|:---|:---|:---|:---:|
| 1.1 | **Caesar** | `HELLO` | `3` | `KHOOR` | `KHOOR` | **ĐẠT** |
| 1.2 | **Vigenère** | `HELLO` | `KEY` | `RIJVS` | `RIJVS` | **ĐẠT** |
| 1.3 | **Rail Fence** | `HELLO WORLD` | `3` | `HOREL OLLWD` | `HOREL OLLWD` | **ĐẠT** |
| 1.4 | **Playfair** | `HELLOWORLD` | `KEYWORD` | `GYIZSCOKCFBU` | `GYIZSCOKCFBU` | **ĐẠT** |
| 1.5 | **Transposition** | `BAOMAT` | `2,3,1` | `AAOTBM` | `AAOTBM` | **ĐẠT** |
| 1.6 | **Autokey** | `HELLO` | `KEY` | `RIJSS` | `RIJSS` | **ĐẠT** |

---

## III. CHI TIẾT CÀI ĐẶT CÁC THUẬT TOÁN (PHẦN 2)

### 1.1. Mật mã Caesar (`CaesarCipher.java`, `frm_Caesar.java`)
- **Nguyên lý:** Mỗi ký tự trong bảng chữ cái (A-Z/a-z) được dịch chuyển $k$ vị trí theo vòng tròn modulo 26:
  $$C = (P + k) \pmod{26}, \quad P = (C - k) \pmod{26}$$
- **Đặc điểm cài đặt:**
  - Dùng `Math.floorMod()` trong Java để xử lý chính xác phép chia lấy dư cho cả số âm khi giải mã hoặc khi khóa $k < 0$.
  - Giữ nguyên chữ hoa, chữ thường và các ký tự không phải chữ cái (dấu câu, chữ số, khoảng trắng).
- **Kết quả kiểm thử:**
  - `HELLO` với $k = 3 \rightarrow$ `KHOOR`.
  - Giải mã `KHOOR` với $k = 3 \rightarrow$ `HELLO`.
  - Bắt lỗi khi nhập khóa sai dạng (ví dụ `abc` $\rightarrow$ thông báo lỗi "Khóa Caesar phải là số nguyên, ví dụ 3.").

### 1.2. Mật mã Vigenère (`VigenereCipher.java`, `frm_Vigenere.java`)
- **Nguyên lý:** Mã hóa đa bảng thay thế, dùng từ khóa lặp lại theo chu kỳ để dịch chuyển từng ký tự bản rõ:
  $$C_i = (P_i + K_{i \pmod m}) \pmod{26}, \quad P_i = (C_i - K_{i \pmod m}) \pmod{26}$$
- **Đặc điểm cài đặt:**
  - Khóa chỉ chấp nhận chữ cái `[A-Z]+` (không phân biệt hoa thường khi nhập).
  - Khoảng trắng và dấu câu được giữ nguyên và **không làm tăng chỉ số khóa** (`keyIndex` chỉ tăng khi gặp ký tự alphabet).
- **Kết quả kiểm thử:**
  - `HELLO` với khóa `KEY` $\rightarrow$ `RIJVS`.
  - Giải mã `RIJVS` với khóa `KEY` $\rightarrow$ `HELLO`.
  - Thử khóa chứa số `K3Y` $\rightarrow$ hiển thị thông báo lỗi "Khóa Vigenere chỉ gồm A-Z."

### 1.3. Mật mã Rail Fence - Hàng rào (`RailFenceCipher.java`, `frm_RailFence.java`)
- **Nguyên lý:** Xếp các ký tự bản rõ theo đường zíc-zắc xuống và lên qua $k$ hàng (rails), sau đó đọc lần lượt từng hàng từ trái sang phải.
- **Đặc điểm cài đặt:**
  - Khoảng trắng và toàn bộ ký tự Unicode đều được giữ nguyên và tham gia vào vị trí hàng rào qua mảng `codePoints()`.
  - Hỗ trợ số hàng $2 \le rails \le 1000$.
- **Kết quả kiểm thử:**
  - `HELLO WORLD` với 3 hàng $\rightarrow$ Hàng 1: `HOR`, Hàng 2: `EL OL`, Hàng 3: `LWD` $\rightarrow$ Ciphertext: `HOREL OLLWD`.
  - Bắt lỗi khi số hàng $< 2$ (ví dụ `1`) $\rightarrow$ thông báo lỗi "Số hàng phải từ 2 đến 1000."

### 1.4. Mật mã Playfair (`PlayfairCipher.java`, `frm_Playfair.java`)
- **Nguyên lý:** Mã hóa thay thế theo từng cặp 2 ký tự (digram) dựa trên ma trận khóa kích thước $5 \times 5$ (I và J gộp chung một ô).
  - Chuẩn bị bản rõ: Chuyển hoa, đổi J thành I, loại bỏ ký tự không phải chữ cái.
  - Tách cặp: Nếu 2 chữ trong cùng một cặp trùng nhau thì chèn chữ `X` (nếu trùng `X` thì chèn `Q`). Nếu độ dài lẻ thì thêm `X` vào cuối.
  - Mã hóa theo 3 quy tắc:
    1. Cùng hàng: dịch sang phải 1 ô (vòng quanh).
    2. Cùng cột: dịch xuống dưới 1 ô (vòng quanh).
    3. Khác hàng khác cột: lấy góc đối diện trên cùng hàng của ký tự đó.
- **Giao diện đặc thù:** Có nút và bảng hiển thị trực quan ma trận khóa `txt_matrix` ($5 \times 5$).
- **Kết quả kiểm thử:**
  - Ma trận khóa với từ `KEYWORD`:
    ```
    K E Y W O
    R D A B C
    F G H I L
    M N P Q S
    T U V X Z
    ```
  - Bản rõ `HELLOWORLD` chia cặp thành `HE LX LO WO RL DX` $\rightarrow$ Ciphertext: `GYIZSCOKCFBU`.
  - Giải mã `GYIZSCOKCFBU` $\rightarrow$ `HELXLOWORLDX` (có chứa ký tự đệm `X`, đúng chuẩn giải thuật Playfair).

### 1.5. Mật mã Hoán vị cột - Transposition (`TranspositionCipher.java`, `frm_Transposition.java`)
- **Nguyên lý:** Viết bản rõ theo hàng vào một lưới có số cột bằng độ dài khóa. Sau đó đọc lần lượt từng cột theo thứ tự các số được quy định trong khóa hoán vị.
- **Đặc điểm cài đặt:**
  - Khóa là một chuỗi các số nguyên phân tách bởi dấu phẩy (ví dụ `2,3,1`).
  - Kiểm tra tính hợp lệ: Khóa phải là một hoán vị chặt chẽ của các số từ $1$ đến $m$ (mỗi số xuất hiện duy nhất 1 lần, không được trùng, không được nhảy cóc).
- **Kết quả kiểm thử:**
  - Bản rõ `BAOMAT`, Khóa `2,3,1`:
    - Cột 1: `B, M`
    - Cột 2: `A, A`
    - Cột 3: `O, T`
    - Đọc cột 2 (`AA`) + cột 3 (`OT`) + cột 1 (`BM`) $\rightarrow$ `AAOTBM`.
  - Bắt lỗi khi khóa không phải hoán vị: `1,1,2` hoặc `1,4` $\rightarrow$ Báo lỗi "Khóa phải là hoán vị 1..m."

### 1.6. Mật mã Autokey (`AutokeyCipher.java`, `frm_Autokey.java`)
- **Nguyên lý:** Khắc phục điểm yếu chu kỳ lặp lại của Vigenère bằng cách sử dụng chính các ký tự của **bản rõ** để nối tiếp vào sau khóa mồi (seed key):
  - Dòng khóa mã hóa = Khóa mồi + Bản rõ.
  - Khi giải mã: Ký tự bản rõ vừa khôi phục được sẽ lập tức được nối vào cuối dòng khóa để giải mã các ký tự tiếp theo.
- **Kết quả kiểm thử:**
  - Bản rõ `HELLO`, Khóa mồi `KEY`:
    - Dòng khóa: `K (10), E (4), Y (24), H (7), E (4)`
    - Bản rõ: `H (7), E (4), L (11), L (11), O (14)`
    - Phép cộng mod 26: `17 (R), 8 (I), 9 (J), 18 (S), 18 (S)` $\rightarrow$ Ciphertext: `RIJSS`.
  - Giải mã `RIJSS` với khóa `KEY` $\rightarrow$ `HELLO`.

---

## IV. BÀI NÂNG CAO 01: CHƯƠNG TRÌNH TỔNG HỢP VÀ KẾT NỐI SQL SERVER (PHẦN 3)

### 1. Kiến trúc hệ thống
- **Giao diện tập trung `frm_TongHop`:** Người dùng lựa chọn 1 trong 6 thuật toán qua `JComboBox`. Form tự động điền gợi ý khóa mẫu thông qua `CipherTongHop.sampleKey()`.
- **Cấu hình kết nối an toàn `DbConfig.java`:**
  - Toàn bộ thông tin Server, Cổng, Database, User và Password được nhập động qua giao diện `frm_KetNoiSQL`.
  - Mật khẩu chỉ được lưu trữ dưới dạng mảng `char[]` trong RAM trong suốt phiên chạy của ứng dụng, hoàn toàn **không lưu cứng vào file hay mã nguồn**.
- **Lớp truy xuất dữ liệu `HistoryDao.java`:**
  - Sử dụng `PreparedStatement` với tham số `?` để ngăn chặn triệt để lỗ hổng **SQL Injection**.
  - Phương thức `tryInsert()` có cơ chế dự phòng mềm (fallback): nếu người dùng chưa kết nối SQL Server, ứng dụng vẫn thực hiện mã hóa bình thường và ghi chú trạng thái rõ ràng mà không phát sinh crash hoặc dừng chương trình.
- **Form hiển thị lịch sử `frm_LichSu`:**
  - Tải 50 thao tác gần nhất từ SQL Server và hiển thị trực quan lên `JTable`.
  - Nút "Tải lại" cập nhật dữ liệu tức thì.

### 2. Thiết kế CSDL SQL Server (`03_SQL_SERVER/`)
- Database: `LAB1_MaHoaCoDien`
- Bảng: `dbo.history`
  - `id`: BIGINT IDENTITY(1,1) PRIMARY KEY
  - `created_at`: DATETIME2(0) NOT NULL DEFAULT SYSDATETIME()
  - `algorithm`: NVARCHAR(50) NOT NULL
  - `operation`: VARCHAR(10) NOT NULL (ràng buộc CHECK `operation IN ('ENCRYPT','DECRYPT')`)
  - `input_text`: NVARCHAR(MAX) NOT NULL
  - `result_text`: NVARCHAR(MAX) NOT NULL
- Phân quyền nguyên tắc đặc quyền tối thiểu (Least Privilege):
  - User `lab1_user` chỉ được cấp quyền `SELECT, INSERT, DELETE` trên bảng `dbo.history`. Không cấp quyền `db_owner` hay can thiệp hệ thống.

---

## V. BÀI NÂNG CAO 02: PHÂN TÍCH (TẤN CÔNG) MẬT MÃ CỔ ĐIỂN (PHẦN 4)

### 1. Thí nghiệm 1: Vét cạn (Brute Force) Caesar với `KHOOR`
- **Mục tiêu:** Thử toàn bộ không gian khóa của Caesar ($26$ khóa từ $k = 0$ đến $k = 25$) và tự động tìm ra khóa có ý nghĩa nhất dựa trên bảng tần suất tiếng Anh (`AttackDemos.caesarBruteForce`).
- **Kết quả thực nghiệm:**
  ```
  k | Bản rõ thử
   0 | KHOOR
   1 | JGNNQ
   2 | IFMMP
   3 | HELLO <== có nghĩa nhất
   4 | GDKKN
   5 | FCJJM
   6 | EBIIL
   7 | DAHHK
   8 | CZGGJ
   9 | BYFFI
  10 | AXEEH
  11 | ZWDDG
  12 | YVCCF
  13 | XUBBE
  14 | WTAAD
  15 | VSZZC
  16 | URYYB
  17 | TQXXA
  18 | SPWWZ
  19 | ROVVY
  20 | QNUUX
  21 | PMTTW
  22 | OLSSV
  23 | NKRRU
  24 | MJQQT
  25 | LIPPS
  Kết luận: khóa k = 3, bản rõ = HELLO
  ```
- **Nhận xét:** Do không gian khóa của Caesar quá nhỏ ($26$), kẻ tấn công có thể vét cạn toàn bộ khóa trong mili-giây.

---

### 2. Thí nghiệm 2: Phân tích tần suất (Frequency Analysis)
- **Dữ liệu kiểm tra:** Ciphertext `PHHW PH DIWHU WKH WRJD SDUWB` (được mã hóa bằng Caesar $k = 3$ từ bản rõ `"MEET ME AFTER THE TOGA PARTY"`).
- **Kết quả thống kê tần suất (`AttackDemos.frequency`):**
  ```
  Chữ | Số lần | Tần suất
   B  |  1     |   4.3 %
   D  |  3     |  13.0 %
   H  |  5     |  21.7 %
   I  |  1     |   4.3 %
   J  |  1     |   4.3 %
   K  |  1     |   4.3 %
   P  |  2     |   8.7 %
   R  |  1     |   4.3 %
   S  |  1     |   4.3 %
   U  |  2     |   8.7 %
   W  |  5     |  21.7 %
  Tổng số chữ: 23
  ```
- **Phân tích toán học:**
  - Hai chữ cái `H` và `W` xuất hiện nhiều nhất ($5$ lần, chiếm $21.7\%$).
  - Trong tiếng Anh, chữ cái có tần suất cao nhất là `E` ($12.7\%$). Giả định `H` là ảnh của `E`:
    $$\Delta = \text{val}(H) - \text{val}(E) = 7 - 4 = 3$$
  - Suy ra khóa $k = 3$, giải mã ngược lại thu được chính xác bản rõ ban đầu.
- **Nhận xét:**
  - Mật mã thay thế đơn (Caesar, Monoalphabetic) giữ nguyên phân phối tần suất của ngôn ngữ gốc, chỉ dịch chuyển vị trí các đỉnh tần suất.
  - Với các đoạn văn bản ngắn (như `KHOOR`), phân tích tần suất không đủ tin cậy vì cỡ mẫu quá nhỏ; nhưng với văn bản từ vài chục đến vài trăm ký tự, kỹ thuật này phá mã gần như tức thì.

---

### 3. Thí nghiệm 3: Tấn công khi biết trước bản rõ (Known Plaintext Attack)
- **Dữ liệu kiểm tra:** Bản rõ `HELLO`, Bản mã Vigenère `RIJVS`.
- **Phương pháp tính (`AttackDemos.knownPlaintext`):**
  $$K_i = (C_i - P_i) \pmod{26}$$
  - $R (17) - H (7) = 10 \rightarrow \mathbf{K}$
  - $I (8) - E (4) = 4 \rightarrow \mathbf{E}$
  - $J (9) - L (11) = -2 \equiv 24 \pmod{26} \rightarrow \mathbf{Y}$
  - $V (21) - L (11) = 10 \rightarrow \mathbf{K}$
  - $S (18) - O (14) = 4 \rightarrow \mathbf{E}$
- **Kết quả:** Chuỗi khóa suy ra được là `KEYKE`.
- **Nhận xét:** Chuỗi `KEYKE` chính là từ khóa gốc `KEY` đang được lặp lại (`KEY KEY...`). Chỉ cần biết một đoạn ngắn bản rõ tương ứng với bản mã, ta có thể khôi phục lại toàn bộ khóa của hệ mật mã Vigenère.

---

## VI. BẢNG PHÂN TÍCH ĐIỂM YẾU VÀ CÁCH TẤN CÔNG (MỤC 4.E)

| Hệ mật mã | Điểm yếu chính | Phương pháp thám mã / tấn công hiệu quả |
|:---|:---|:---|
| **Caesar** | Không gian khóa cực kỳ nhỏ ($|\mathcal{K}| = 26$). | Vét cạn toàn bộ 26 trường hợp; hoặc phân tích tần suất chữ cái xuất hiện nhiều nhất. |
| **Vigenère** | Khóa được lặp lại theo chu kỳ cố định $m$. | Xác định độ dài khóa $m$ bằng phương pháp Kasiski hoặc chỉ số trùng khớp Friedman, sau đó tách thành $m$ bài toán Caesar độc lập để phân tích tần suất từng cột; hoặc Known Plaintext Attack. |
| **Rail Fence** | Số hàng nhỏ ($k \ll n$), không thay đổi ký tự mà chỉ xáo trộn vị trí. | Vét cạn mọi số hàng khả dĩ từ $2$ đến $n$; tần suất chữ cái giữ nguyên $100\%$ so với bản rõ. |
| **Playfair** | Mã hóa theo khối cố định 2 ký tự (digram), không gian ma trận 25 chữ cái, không bao giờ mã hóa một chữ cái thành chính nó. | Thống kê tần suất các cặp chữ cái (digram); tấn công khi biết một phần bản rõ để dựng lại ma trận $5 \times 5$. |
| **Transposition** (Hoán vị cột) | Chỉ hoán vị vị trí các ký tự, không thay đổi giá trị chữ cái; tần suất các chữ cái được bảo toàn nguyên vẹn. | Thử các ước số độ dài khóa nhỏ, dùng phân tích n-gram (bigram/trigram) tiếng Anh để sắp xếp lại các cột thành từ có nghĩa. |
| **Autokey** | Dòng khóa sử dụng chính các ký tự bản rõ sau khóa mồi. | Đoán các từ ngữ phổ biến trong ngôn ngữ (ví dụ trong tiếng Anh: `THE`, `AND`, `THAT`) để dò lùi dần từng ký tự khóa; hoặc Known Plaintext Attack. |

---

## VII. HƯỚNG DẪN KIỂM THỬ GIAO DIỆN VÀ MINH HỌA

### 1. Menu chính (`frm_Menu`)
- Khởi chạy ứng dụng: Lớp chính `week_01.frm_Menu` hiển thị danh sách 9 nút bấm được bố trí khoa học thành 3 hàng x 3 cột.
- Bấm vào bất kỳ nút nào sẽ mở cửa sổ riêng biệt của bài đó mà không đóng Menu chính.

### 2. Các form mã hóa cơ bản (1.1 - 1.6)
- Hỗ trợ đầy đủ các thao tác:
  - **Encrypt:** Kiểm tra dữ liệu đầu vào, mã hóa bản rõ và hiển thị kết quả ở ô Ciphertext kèm thanh trạng thái.
  - **Decrypt:** Giải mã Ciphertext về ô Plaintext.
  - **Open Ciphertext File:** Mở tệp `.txt` (UTF-8) chọn từ máy tính đưa vào ô văn bản.
  - **Save Ciphertext File:** Lưu nội dung ra tệp `.txt` (tự động bổ sung phần mở rộng `.txt` nếu người dùng không nhập).
  - Bắt lỗi ngoại lệ và hiển thị qua hộp thoại `JOptionPane` rõ ràng, thân thiện.

### 3. Form tổng hợp (`frm_TongHop`)
- Cho phép đổi thuật toán linh hoạt qua ComboBox.
- Tự động điền khóa mẫu tương ứng.
- Khi có kết nối SQL Server, mọi lần bấm Encrypt/Decrypt đều được ghi nhận vào cơ sở dữ liệu.

---

## VIII. KẾT LUẬN

1. Dự án Lab 1 đã được hoàn thành trọn vẹn $100\%$ theo đúng yêu cầu chi tiết trong tài liệu `LAB1_HuongDan.pdf`.
2. Toàn bộ mã nguồn tuân thủ nghiêm ngặt quy tắc đặt tên, cấu trúc package `week_01`, và chuẩn mã nguồn Java 17.
3. Dự án tương thích hoàn toàn với Maven build tool, NetBeans GUI Builder, VS Code, và các hệ thống CI/CD tự động thông qua bộ kiểm thử tự động JUnit 5.
4. Qua bài lab, sinh viên nắm vững nguyên lý hoạt động, ưu - nhược điểm và các phương pháp thám mã đối với các hệ mật mã đối xứng cổ điển, tạo tiền đề vững chắc cho các bài lab mã hóa hiện đại tiếp theo (DES, AES, RSA, ECC).
