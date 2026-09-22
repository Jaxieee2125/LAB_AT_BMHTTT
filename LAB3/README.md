# LAB 3: Nhận diện và ứng phó các mối đe dọa đến an toàn thông tin

## 1. Thông tin sinh viên
- **Họ và tên:** Nguyễn Thành Thái
- **MSSV:** 1150080036
- **Lớp / Học phần:** 11 ĐH_CNPM1
- **Youtube:** https://youtu.be/jVif22z6Iro

## 2. Phiên bản môi trường
Theo cấu hình tiêu chuẩn từ tài liệu tham chiếu `LAB3_CacMoiDeDoa_ATTT_2026.pdf`:
- **Ảo hóa:** VMware Workstation Pro 26H1 (Mạng Host-only)
- **Máy ảo:** Windows 11 25H2 x64, OS build 26200.9445 (Đã cập nhật KB5124008)
- **Endpoint protection:** Microsoft Defender Antivirus (Bảo vệ thời gian thực và Tamper Protection đang bật)
- **Shell:** Windows PowerShell 5.1 (Run as administrator)
- **Bộ công cụ (Tools):** 
  - Sysmon: 15.22
  - Autoruns: 14.3
  - Process Explorer: 17.14
  - Wireshark: 4.6.8 Stable + Npcap
  - Python: 3.14.7

## 3. Cách dựng môi trường
1. Khởi tạo VM Windows 11 25H2 trên VMware Workstation Pro, cấu hình Network Adapter sang Host-only.
2. Cập nhật Windows, tạo snapshot dự phòng với tên `LAB3_CLEAN_20260914`.
3. Khởi chạy PowerShell (Administrator), tạo cấu trúc thư mục bài Lab tại `C:\LAB3` gồm: `Evidence`, `Tools`, `Downloads`, `Assets`.
4. Giải nén gói dữ liệu bài lab `LAB3_Threats_Assets.zip` vào thư mục Downloads và kiểm tra tính toàn vẹn bằng mã SHA-256.
5. Sử dụng lệnh `winget` để tự động cài đặt Python 3.14.7 và Wireshark 4.6.8.
6. Dùng lệnh `Invoke-WebRequest` để tải trực tiếp bộ 3 công cụ Sysinternals (Sysmon, Autoruns, Process Explorer) từ máy chủ Microsoft và giải nén vào mục `Tools`.

## 4. Các tình huống đã thực hiện & Kết quả
*(Cập nhật đến tiến độ 4.1 - Đang trong quá trình hoàn thiện các phần tiếp theo)*

| Tình huống | Mô tả công việc | Trạng thái | Kết quả |
| :--- | :--- | :---: | :---: |
| **Baseline** | Thu thập thông tin OS, Defender, Firewall, Network | Đã hoàn thành | PASS |
| **TH 1** | Xác định tài sản, lỗ hổng, mối đe dọa và rủi ro | Đã hoàn thành | PASS |
| **TH 2** | Mã độc: kiểm chứng detection bằng EICAR | Đã hoàn thành | PASS |
| **TH 3** | Tấn công mật khẩu và nguy cơ keylogging | Đã hoàn thành | PASS |
| **TH 4.1** | Cài đặt Sysmon và thu thập baseline Autoruns | Đã hoàn thành | PASS |
| **TH 4.2 - 4.3** | Tạo persistence và HTTP listener, truy vết backdoor | Chưa thực hiện | - |
| **TH 5** | Sniffing, MITM và Spoofing (HTTP vs HTTPS) | Chưa thực hiện | - |
| **TH 6** | DoS, DDoS và Mail Bombing | Chưa thực hiện | - |
| **TH 7** | Social Engineering, Phishing | Chưa thực hiện | - |
| **Cleanup** | Cô lập, dọn dẹp, phục hồi và verify | Chưa thực hiện | - |

## 5. Lỗi gặp phải và cách khắc phục
Trong quá trình chạy các câu lệnh cấu hình và thực hành từ Baseline đến mục 4.1, tôi đã gặp một số lỗi cú pháp trên PowerShell và đã xử lý thành công:

1. **Lỗi đứt đoạn câu lệnh và rớt tham số:**
   - **Mô tả:** Khi copy lệnh từ file PDF, các tham số như `-Width 220`, `/success:enable` bị rớt xuống dòng, hoặc dấu ngắt dòng backtick (`) bị dính khoảng trắng khiến PowerShell báo lỗi thiếu tham số.
   - **Khắc phục:** Sắp xếp và nối các thành phần của câu lệnh lên cùng một dòng (ví dụ lệnh `Out-File ... -Width 220` hoặc `Set-Content ...`). Nếu danh sách dài, sử dụng ký hiệu đường ống `|` ở cuối dòng để PowerShell hiểu lệnh còn tiếp tục.
2. **Lỗi sai cú pháp của tham số Select-Object:**
   - **Mô tả:** Lệnh `Select First 5` báo lỗi không hợp lệ.
   - **Khắc phục:** Thêm dấu gạch ngang và dùng tên đầy đủ: `Select-Object -First 5`.
3. **Lỗi nhận diện GUID trong lệnh Auditpol:**
   - **Mô tả:** Lệnh `auditpol /set /subcategory:{0CCE...}` bị lỗi do PowerShell nhầm cặp ngoặc nhọn `{}` là một script block.
   - **Khắc phục:** Đặt toàn bộ chuỗi GUID vào trong dấu ngoặc kép: `"{0CCE9215-69AE-11D9-BED3-505054503030}"`.
4. **Lỗi không thể nhập mật khẩu khi dùng lệnh runas (RUNAS ERROR):**
   - **Mô tả:** Lệnh `runas /user:.\lab3user cmd.exe` xuất hiện thông báo `Unable to acquire user password` dù mật khẩu gõ chính xác.
   - **Khắc phục:** Nguyên nhân do chạy `runas` bên trong IDE/PowerShell không hỗ trợ luồng nhập mật khẩu ẩn. Khắc phục bằng cách mở trực tiếp Command Prompt (CMD) truyền thống để chạy lệnh `runas`, hoặc đổi sang lệnh PowerShell hiện đại: `Start-Process cmd.exe -Credential ".\lab3user"`.