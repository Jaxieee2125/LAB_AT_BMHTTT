# BÁO CÁO THỰC HÀNH LAB 7: CHÍNH SÁCH WEB PROTECTION TRÊN pfSense & WEB PROXY

## 1. THÔNG TIN SINH VIÊN & BÀI THỰC HÀNH

* **Họ và tên:** Nguyễn Thành Thái

* **Mã số sinh viên (MSSV):** 1150080036

* **Lớp:** 11_CNPM1

* **Tên bài Lab:** LAB 7 - Xây dựng Chính sách Web Protection trên Mô hình pfSense + Web Proxy (Squid + E2Guardian + ClamAV)

## 2. PHIÊN BẢN VÀ THÔNG SỐ MÔI TRƯỜNG

* **Phần mềm ảo hóa:** Oracle VM VirtualBox 7.x

* **pfSense-LAB6 (Firewall/Gateway):** pfSense CE 2.7.x (4 Card mạng: WAN NAT Network, MGMT Host-Only `192.168.56.254`, LAN Internal `10.6.0.1`, DMZ Internal `172.16.6.1`)

* **Domain Controller (DC01):** Windows Server 2022 (IP: `10.6.0.10/24`, Domain: `lab6.test`, tích hợp DNS Server cho `lab7.test`)

* **Web Proxy chuyên dụng (WEB-PROXY):** Ubuntu Server 24.04 LTS (IP: `10.6.0.30/24`, Gateway: `10.6.0.1`, DNS: `10.6.0.10`)

  * Phần mềm Proxy/Lọc nội dung: Squid 6.x, E2Guardian 5.x, ClamAV Daemon

* **DMZ Web Server (DMZ-WEB):** Ubuntu Server (IP chính: `172.16.6.10/24`, Web Server Nginx đóng vai trò hosting 13 virtual host mô phỏng)

* **Client trạm kiểm thử:** Windows 10/11 (Client-A: `10.6.0.21`, Client-B: `10.6.0.22`, DNS: `10.6.0.10`)

## 3. QUY TRÌNH DỰNG MÔI TRƯỜNG

### Bước 1: Kế thừa và chuẩn bị hạ tầng mạng

1. Giữ nguyên mô hình mạng từ LAB 6 gồm 4 vùng mạng độc lập: `LAB6-WAN`, `Host-Only (192.168.56.0/24)`, `lab6-lan (10.6.0.0/24)`, `lab6-dmz (172.16.6.0/24)`.

2. Tạo máy ảo mới `WEB-PROXY` chạy Ubuntu Server 24.04 LTS, gán card mạng vào `lab6-lan`.

3. Cấu hình IP tĩnh cho WEB-PROXY qua Netplan: IP `10.6.0.30/24`, Gateway `10.6.0.1`, Nameserver `10.6.0.10`.

### Bước 2: Dựng dịch vụ Web Proxy trên máy Ubuntu

1. Cài đặt các gói phần mềm cần thiết:

   ```
   sudo apt update
   sudo apt install -y squid e2guardian clamav clamav-daemon curl openssl
   
   ```

2. Cấu hình phân tầng dịch vụ theo chuỗi kiểm soát:

   * **E2Guardian:** Lắng nghe tại `10.6.0.30:8080` (nhận kết nối từ Client, thực thi chính sách lọc nội dung, quét virus, hiển thị cảnh báo).

   * **Squid:** Lắng nghe tại `127.0.0.1:3128` (chỉ nhận lưu lượng nội bộ từ E2Guardian chuyển sang để làm forward proxy ra ngoài).

   * **ClamAV:** Chạy daemon nội bộ liên kết qua socket để E2Guardian gửi file kiểm tra mã độc.

### Bước 3: Cấu hình Website mô phỏng và DNS

1. Trên `DMZ-WEB` (`172.16.6.10`), chạy script `setup_lab7_dmz_web.sh` để cấu hình 13 Virtual Host Nginx đại diện cho các danh mục web: `normal`, `china`, `mideast`, `jobs`, `weapons`, `politics`, `religion`, `education`, `entertainment`, `games`, `streaming`, `blockedgame`, `download`.

2. Trên `DC01`, mở DNS Manager (hoặc chạy script PowerShell `Add-LAB7-DNS.ps1`), tạo Forward Lookup Zone `lab7.test` và thêm các bản ghi A trỏ toàn bộ 13 FQDN mô phỏng về địa chỉ `172.16.6.10`.

### Bước 4: Ép buộc lưu lượng Web trên pfSense (Chống Bypass)

1. Trên WebGUI pfSense (`https://192.168.56.254`), tạo các Alias:

   * `LAB7_CLIENTS`: `10.6.0.21`, `10.6.0.22`

   * `LAB7_PROXY`: `10.6.0.30`

   * `WEB_PORTS`: `80, 443`

2. Thiết lập bộ lọc Firewall Rule trên Interface LAN theo thứ tự First-Match:

   * Rule 1 (Pass): Cho phép LAN truy vấn DNS tới `10.6.0.10` (Port 53).

   * Rule 2 (Pass): Cho phép `LAB7_CLIENTS` kết nối tới `LAB7_PROXY` port `8080` (TCP).

   * Rule 3 (Block): Chặn `LAB7_CLIENTS` truy cập trực tiếp ra ngoài qua `WEB_PORTS` (80/443) -> *Chống bypass*.

   * Rule 4 (Pass): Cho phép `LAB7_PROXY` truy cập ra ngoài qua `WEB_PORTS` (80/443).

   * Rule 5 (Block): Explicit Deny tất cả lưu lượng còn lại.

## 4. CÁC TÌNH HUỐNG THỰC HIỆN & KẾT QUẢ KIỂM THỬ

| STT | Tình huống kiểm thử | Mô tả cấu hình / Chính sách | Kết quả mong đợi | Kết quả thực tế | Đánh giá | 
| ----- | ----- | ----- | ----- | ----- | ----- | 
| **1** | **Baseline & Chống Bypass** | Cấu hình proxy `10.6.0.30:8080` trên client; sau đó tắt thử proxy. | Bật proxy: vào được `normal.lab7.test`. Tắt proxy: pfSense chặn kết nối trực tiếp port 80/443. | Đúng như mong đợi, pfSense chặn triệt để khi tắt proxy. | **PASS** | 
| **2** | **Hard Block Geo-Sim** | Đưa `china.lab7.test` và `mideast.lab7.test` vào danh sách cấm tuyệt đối (banned site / non-bypass). | Bị chặn hiển thị trang báo cấm; không có nút cho phép vượt qua tạm thời. | Hiển thị template chặn truy cập. | **PASS** | 
| **3** | **Hard Block Job Search** | Thêm `jobs.lab7.test` vào danh sách bannedsitelist của E2Guardian. | Bị chặn toàn bộ, không có ngoại lệ. | Bị chặn thành công. | **PASS** | 
| **4** | **Cảnh báo (Warning / Temporary Bypass)** | Đưa `weapons.lab7.test`, `politics.lab7.test`, `religion.lab7.test` vào danh sách cảnh báo; đặt `bypass = 300` trong `e2guardianf1.conf`. | Xuất hiện màn hình cảnh báo, người dùng xác nhận thì được truy cập tạm trong 5 phút. | Trang cảnh báo xuất hiện, nhấn tiếp tục cho phép duyệt trong 5 phút rồi tự khóa lại. | **PASS** | 
| **5** | **Ngoại lệ (Exception / Whitelist)** | Cấu hình `education.lab7.test` trong `exceptionsitelist`. | Truy cập bình thường, hoàn toàn không có cảnh báo. | Truy cập thẳng vào trang giáo dục không gián đoạn. | **PASS** | 
| **6** | **Giới hạn thời gian (Time Schedule)** | Tạo Schedule `LAB7_ENT_60M` trên pfSense cho `entertainment.lab7.test` và `games.lab7.test`. | Chỉ truy cập được trong khung giờ chỉ định; ngoài khung giờ bị pfSense/Proxy chặn. | Đúng theo khung thời gian kiểm thử; ngoài giờ báo kết nối bị ngắt. | **PASS** | 
| **7** | **Streaming Warning** | Đưa `streaming.lab7.test` vào nhóm cảnh báo băng thông. | Xuất hiện cảnh báo nhưng có nút Bypass tạm thời. | Hoạt động chính xác theo cơ chế cảnh báo. | **PASS** | 
| **8** | **Chặn game chỉ định** | Đưa domain `blockedgame.lab7.test` vào danh sách Hard Block. | Bị chặn hoàn toàn. | Không thể truy cập, trang báo lỗi của proxy hiện lên. | **PASS** | 
| **9** | **Quét virus tải về (Antivirus Scanning)** | Bật module scanner ClamAV Daemon trong E2Guardian; tải file `eicar.com.txt` từ `download.lab7.test`. | E2Guardian bắt được chữ ký EICAR và chặn tải file kèm thông báo phát hiện mã độc. | Chặn tải file thành công, log E2Guardian và ClamAV ghi nhận phát hiện mã độc EICAR. | **PASS** | 
| **10** | **Giới hạn dung lượng file** | Cấu hình `reply_body_max_size` trên Squid để chặn file > 100MB; thử tải `100MB.bin` và `110MB.bin`. | File 100MB tải thành công; file 110MB bị từ chối truyền tải. | File 100MB tải bình thường, file 110MB trả về trang lỗi kích thước vượt ngưỡng. | **PASS** | 

## 5. LỖI GẶP PHẢI VÀ CÁCH KHẮC PHỤC

### Sự cố 1: Client tắt cấu hình proxy nhưng vẫn truy cập được web

* **Nguyên nhân:** Trên pfSense, rule mặc định cho phép LAN đi Any vẫn nằm phía trên rule chặn cổng 80/443 của nhóm máy trạm, hoặc firewall state cũ của phiên duyệt web trước vẫn còn tồn tại trong bộ nhớ.

* **Cách khắc phục:**

  1. Kéo rule `Block LAB7_CLIENTS -> Any Port WEB_PORTS` lên vị trí ưu tiên cao hơn (trước các rule cho phép ra ngoài).

  2. Bấm **Apply Changes**.

  3. Vào menu **Diagnostics -> States -> Reset States** để xóa sạch các phiên kết nối đang lưu tạm trên firewall.

### Sự cố 2: E2Guardian không khởi động được sau khi cấu hình Antivirus

* **Nguyên nhân:** Dịch vụ `clamav-daemon` chưa sẵn sàng hoạt động hoặc file socket `/var/run/clamav/clamd.ctl` bị sai quyền hạn khiến E2Guardian không thể kết nối tới bộ quét virus.

* **Cách khắc phục:**

  1. Kiểm tra trạng thái daemon: `sudo systemctl status clamav-daemon`.

  2. Phân lại quyền truy cập socket hoặc cấu hình User chạy dịch vụ: thêm user `e2guardian` vào group `clamav` bằng lệnh `sudo usermod -aG clamav e2guardian`.

  3. Khởi động lại theo đúng thứ tự: `sudo systemctl restart clamav-daemon e2guardian`.

### Sự cố 3: Không hiển thị trang cảnh báo Temporary Bypass (bị chặn thẳng hoặc báo lỗi 500)

* **Nguyên nhân:** Cấu hình `reportinglevel` trong `e2guardian.conf` chưa chuyển sang giá trị `3` (chế độ sử dụng HTML template nâng cao) hoặc tên miền cần cảnh báo bị ghi nhầm vào file `bannedsitelist` thông thường mà không nằm trong cấu hình bypass.

* **Cách khắc phục:**

  1. Mở file cấu hình `/etc/e2guardian/e2guardian.conf`, đặt lại `reportinglevel = 3`.

  2. Kiểm tra file cấu hình nhóm `/etc/e2guardian/e2guardianf1.conf`, đặt giá trị `bypass = 300`.

  3. Đảm bảo domain cảnh báo chỉ nằm trong danh sách kiểm tra cảnh báo và không nằm trong `bannedregexurllist` hoặc danh sách cấm cứng.

  4. Khởi động lại dịch vụ: `sudo systemctl reload e2guardian`.