# LAB 1: MÃ HÓA ĐỐI XỨNG CỔ ĐIỂN
**Môn học:** An toàn và Bảo mật Thông tin  
**Công nghệ:** Java Swing (NetBeans GUI Builder compatible) – Apache Maven – Microsoft SQL Server  
**Package:** `week_01`  
**Lớp chính:** `week_01.frm_Menu`

---

## 1. Cấu trúc Project

```
LAB10/
├── 03_SQL_SERVER/
│   ├── 01_Tao_CSDL.sql             # Tạo database LAB1_MaHoaCoDien & bảng dbo.history
│   ├── 02_Tao_Login_MAU.sql        # Tạo login & user lab1_user (mật khẩu: LabUser@2026)
│   └── 03_Kiem_Tra_Du_Lieu.sql     # Script kiểm tra lịch sử thao tác
├── src/
│   ├── main/java/week_01/
│   │   ├── FileTextIO.java         # Tiện ích đọc / ghi file .txt (UTF-8)
│   │   ├── CaesarCipher.java       # Thuật toán Caesar (k dịch chuyển A-Z)
│   │   ├── frm_Caesar.java/.form   # Giao diện mã hóa Caesar
│   │   ├── VigenereCipher.java     # Thuật toán Vigenère
│   │   ├── frm_Vigenere.java/.form # Giao diện mã hóa Vigenère
│   │   ├── RailFenceCipher.java    # Thuật toán hàng rào (Rail Fence)
│   │   ├── frm_RailFence.java/.form# Giao diện mã hóa Rail Fence
│   │   ├── PlayfairCipher.java     # Thuật toán Playfair ma trận 5x5
│   │   ├── frm_Playfair.java/.form # Giao diện mã hóa Playfair
│   │   ├── TranspositionCipher.java# Thuật toán Hoán vị cột (Transposition)
│   │   ├── frm_Transposition.java/.form # Giao diện mã hóa Hoán vị cột
│   │   ├── AutokeyCipher.java      # Thuật toán Autokey Cipher
│   │   ├── frm_Autokey.java/.form  # Giao diện mã hóa Autokey
│   │   ├── CipherTongHop.java      # Bộ điều phối tổng hợp 6 thuật toán
│   │   ├── frm_TongHop.java/.form  # Giao diện chương trình tổng hợp
│   │   ├── DbConfig.java           # Quản lý kết nối SQL Server (an toàn bộ nhớ)
│   │   ├── HistoryDao.java         # Ghi/đọc lịch sử dbo.history (PreparedStatement)
│   │   ├── frm_KetNoiSQL.java/.form# Giao diện cấu hình & kiểm tra kết nối SQL
│   │   ├── frm_LichSu.java/.form   # Giao diện xem lịch sử thao tác JTable
│   │   ├── AttackDemos.java        # Các phương pháp phân tích/tấn công mật mã
│   │   ├── frm_TanCong.java/.form  # Giao diện phân tích & tấn công mật mã
│   │   └── frm_Menu.java/.form     # Menu chính điều hướng toàn bộ bài Lab
│   └── test/java/week_01/
│       └── CipherAlgorithmsTest.java # Bộ kiểm thử tự động JUnit 5 đầy đủ
├── pom.xml                         # Cấu hình Maven, JDK 17, mssql-jdbc, JUnit 5
├── BaoCao_Lab1.md                  # Báo cáo thực hành chi tiết theo mẫu
└── README.md                       # Hướng dẫn cài đặt và sử dụng
```

---

## 2. Hướng dẫn Biên dịch và Chạy

### Yêu cầu môi trường
- **JDK:** OpenJDK 17 trở lên (`java -version`)
- **Maven:** Apache Maven 3.8+ (`mvn -version`)
- **IDE:** Có thể dùng **Apache NetBeans 20+**, **VS Code**, **IntelliJ IDEA** hoặc dòng lệnh.

### Lệnh chạy với Maven:
1. **Biên dịch toàn bộ dự án:**
   ```bash
   mvn clean compile
   ```
2. **Chạy bộ kiểm thử tự động (JUnit 5):**
   ```bash
   mvn test
   ```
3. **Chạy ứng dụng (Cửa sổ Menu chính `frm_Menu`):**
   ```bash
   mvn exec:java
   ```
4. **Đóng gói file JAR thực thi:**
   ```bash
   mvn package
   ```
   Sau khi đóng gói, chạy trực tiếp file JAR:
   ```bash
   java -jar target/LAB1_MaHoaCoDien-1.0-SNAPSHOT.jar
   ```

---

## 3. Cấu hình Microsoft SQL Server (Tùy chọn cho Phần 3)

1. Mở **SQL Server Configuration Manager** -> Bật giao thức **TCP/IP** cho instance SQL Server và thiết lập cổng cố định `14333`.
2. Khởi động lại dịch vụ SQL Server.
3. Mở **SSMS** (SQL Server Management Studio), chạy lần lượt 2 script:
   - `03_SQL_SERVER/01_Tao_CSDL.sql`
   - `03_SQL_SERVER/02_Tao_Login_MAU.sql`
4. Trên ứng dụng Java, mở form **Kết nối SQL Server**:
   - Server: `127.0.0.1`
   - Port: `14333`
   - Database: `LAB1_MaHoaCoDien`
   - User: `lab1_user`
   - Password: `LabUser@2026`
   - Bấm **Test Connection** để kiểm tra kết nối.
5. Khi đã kết nối thành công, mọi thao tác Encrypt/Decrypt trên **Form Tổng hợp** sẽ tự động lưu vào bảng `dbo.history`.

*Lưu ý: Nếu chưa cấu hình SQL Server, Form Tổng hợp vẫn hoạt động bình thường cho mọi thuật toán mã hóa/giải mã (hiển thị thông báo "Chưa kết nối SQL Server nên chưa lưu lịch sử").*
