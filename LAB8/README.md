# BÁO CÁO THỰC HÀNH AN TOÀN HỆ THỐNG THÔNG TIN

## BÀI THỰC HÀNH: LOCAL DNS ATTACK - DNS SPOOFING VÀ CACHE POISONING TRONG MẠNG ẢO CÔ LẬP

### 1. THÔNG TIN SINH VIÊN

* **Họ và tên:** Nguyễn Thành Thái

* **Mã số sinh viên (MSSV):** 1150080036

* **Lớp / Học phần:** Thực hành An toàn Hệ thống Thông tin

* **Tên bài Lab:** LAB 8 (Mô hình Local DNS Attack kế thừa trên nền pfSense)

### 2. PHIÊN BẢN MÔI TRƯỜNG & THÔNG SỐ HỆ THỐNG

| Máy ảo / Thành phần | Hệ điều hành / Dịch vụ | Địa chỉ IP | Giao tiếp mạng (VirtualBox) | Ghi chú cấu hình | 
| ----- | ----- | ----- | ----- | ----- | 
| **pfSense** | pfSense CE 2.7.x | `10.9.0.1/24` (DNSLAB - opt2) | Adapter 5: Internal Network (`dns-lab`) | Router & Gateway cho subnet DNSLAB | 
| **DNS-SERVER** | Ubuntu Server 24.04 LTS | `10.9.0.10/24` | Internal Network (`dns-lab`) | BIND9 recursive/caching resolver | 
| **VICTIM** | Ubuntu Desktop/Server 24.04 | `10.9.0.100/24` | Internal Network (`dns-lab`) | Client truy vấn, trỏ DNS về 10.9.0.10 | 
| **ATTACKER** | Kali Linux 2024.x | `10.9.0.200/24` | Internal Network (`dns-lab`) | Promiscuous Mode: `Allow VMs`; Scapy, Python3, Wireshark | 
| **APP-TEST / AUTH-DNS** | Ubuntu (DMZ kế thừa) | `172.16.6.20/24` | DMZ Internal Network | Authoritative DNS mô phỏng (delay 1.5s) | 
| **DMZ-WEB** | Ubuntu (DMZ kế thừa) | `172.16.6.10/24` | DMZ Internal Network | Web server hợp lệ | 

### 3. CÁCH DỰNG MÔI TRƯỜNG THỰC HÀNH

#### Bước 1: Sao lưu và tạo mạng mới trên pfSense & VirtualBox

1. **Tạo Snapshot:** Tạo snapshot `LAB_BEFORE_DNS_ATTACK` trên pfSense và Kali Linux để dự phòng.

2. **Cấu hình Adapter 5 pfSense:**

   * Tắt máy pfSense > **Settings** > **Network** > **Adapter 5**: Bật card, chọn **Attached to: Internal Network**, đặt tên `dns-lab`.

3. **Gán Interface trên pfSense WebGUI:**

   * Đăng nhập WebGUI pfSense qua MGMT (`192.168.56.1`).

   * Vào **Interfaces** > **Assignments** > Thêm card mạng mới (OPT). Đặt tên: `DNSLAB`.

   * Cấu hình IPv4: **Static IPv4**, IP: `10.9.0.1/24`, **Upstream Gateway: None** (tuyệt đối không gán gateway).

4. **Thiết lập Firewall Rules cô lập DNSLAB:**

   * `Rule 1 (Pass)`: Source `10.9.0.10` -> Dest `172.16.6.20` Port `UDP/TCP 53` (Resolver forward truy vấn tới Auth-DNS).

   * `Rule 2 (Pass)`: Source `10.9.0.0/24` -> Dest `172.16.6.10` Port `TCP 80` (Client truy cập Web DMZ).

   * `Rule 3 (Block)`: Source `10.9.0.0/24` -> Dest `10.6.0.0/24` (Bảo vệ LAN-DOMAIN).

   * `Rule 4 (Block)`: Source `10.9.0.0/24` -> Dest `192.168.56.0/24` (Bảo vệ mạng MGMT).

   * `Rule 5 (Block)`: Source `10.9.0.0/24` -> Any (Cô lập hoàn toàn kết nối Internet/WAN trong pha tấn công).

#### Bước 2: Dựng máy DNS-SERVER (BIND9)

1. Gán card mạng vào `Internal Network: dns-lab`. Cấu hình IP tĩnh `10.9.0.10/24`, Gateway `10.9.0.1`.

2. Cài đặt BIND9:

   ```
   sudo apt update && sudo apt install -y bind9 dnsutils
   
   ```

3. Cấu hình `/etc/bind/named.conf.options` và `/etc/bind/named.conf.local`:

   * Cho phép đệ quy cho dải `10.9.0.0/24`.

   * Thiết lập zone local `lab9.test` và cấu hình forward zone `external.lab9.test` tới `172.16.6.20`.

   * Tắt DNS cookie cho mục tiêu thử nghiệm: `server 172.16.6.20 { send-cookie no; };`.

4. Kiểm tra và khởi động:

   ```
   sudo named-checkconf
   sudo systemctl restart bind9
   
   ```

#### Bước 3: Cấu hình VICTIM và ATTACKER

1. **VICTIM (10.9.0.100):** Cấu hình Netplan trỏ nameserver duy nhất về `10.9.0.10`.

   ```
   sudo netplan apply
   resolvectl status
   
   ```

2. **ATTACKER Kali (10.9.0.200):**

   * Đặt card mạng `Internal Network: dns-lab`, **Promiscuous Mode = Allow VMs**.

   * Cài đặt thư viện: `sudo apt install -y python3-scapy wireshark tcpdump`.

   * Dựng Web giả lập:

     ```
     mkdir -p ~/LAB/fake_site
     cp fake_index.html ~/LAB/fake_site/index.html
     sudo python3 -m http.server 80 --directory ~/LAB/fake_site
     
     ```

#### Bước 4: Chạy Authoritative DNS mô phỏng và Baseline

1. Trên **APP-TEST (172.16.6.20)**:

   ```
   sudo python3 auth_dns_delayed.py --bind 172.16.6.20 --delay 1.5
   
   ```

2. Kiểm tra **Baseline** tại VICTIM:

   ```
   dig @10.9.0.10 www.external.lab9.test A +short
   # Kết quả bắt buộc phải ra: 172.16.6.10
   curl http://www.external.lab9.test
   
   ```

### 4. CÁC TÌNH HUỐNG ĐÃ THỰC HIỆN & KẾT QUẢ KIỂM THỬ

| STT | Kịch bản / Tình huống | Mô tả hành động | Kết quả kỳ vọng | Kết quả thực tế | Trạng thái | 
| ----- | ----- | ----- | ----- | ----- | ----- | 
| **0** | **Kiểm tra Baseline** | Truy vấn tên miền `www.external.lab9.test` khi chưa tấn công. | Phân giải chính xác về IP DMZ `172.16.6.10`. | Trả về đúng `172.16.6.10`. | **PASS** | 
| **1** | **Task 1: Chuyển hướng bằng HOSTS File** | Sửa file `/etc/hosts` trên Victim: thêm `10.9.0.200 www.lab9.test`. | `curl` ra web Attacker (`10.9.0.200`), nhưng lệnh `dig` trực tiếp vẫn ra IP gốc. | `curl` nhận trang fake site, `dig` không bị ảnh hưởng. | **PASS** | 
| **2** | **Task 2: Direct DNS Spoofing** | Kali lắng nghe gói tin query từ Victim tới DNS Server và gửi phản hồi giả mạo trực tiếp tới Victim. | Victim nhận gói giả mạo (`10.9.0.200`) trước khi phản hồi thật kịp về; web chuyển hướng sang Kali. | `resolvectl query` và `curl` trỏ về `10.9.0.200`. Wireshark bắt được gói giả mạo trùng TXID. | **PASS** | 
| **3** | **Task 3: DNS Cache Poisoning** | Kali sniff query giữa DNS-SERVER forward sang AUTH-DNS và gửi phản hồi giả mạo về Resolver để đầu độc cache. | Cache BIND9 bị đầu độc bản ghi `10.9.0.200` (TTL 120s). Mọi client truy vấn sau đó đều nhận IP giả. | Lệnh `rndc dumpdb -cache` thấy bản ghi bị sửa thành `10.9.0.200`. Tắt script tấn công, Victim vẫn trỏ về fake site. | **PASS** | 
| **4** | **Phục hồi & Phòng thủ** | Xóa cache (`rndc flush`), trả card Promiscuous về Deny, xóa send-cookie bypass. | DNS Server phân giải lại bản ghi chuẩn `172.16.6.10`, ngăn chặn tấn công sniffing. | Hệ thống an toàn trở lại, query trả đúng IP DMZ gốc. | **PASS** | 

### 5. LỖI GẶP PHẢI VÀ CÁCH KHẮC PHỤC (TROUBLESHOOTING)

#### Lỗi 1: Kali Linux không bắt được các gói tin DNS của máy Victim và DNS-SERVER

* **Hiện tượng:** Wireshark trên Kali chỉ thấy gói tin broadcast/multicast (ARP, MDNS), script `dns_spoof_lab9.py` không in log nhận gói tin truy vấn.

* **Nguyên nhân:** Adapter mạng của Kali trong VirtualBox chưa được cấu hình chế độ nghe lén gói tin nội bộ.

* **Cách khắc phục:** Tắt Kali (hoặc chỉnh card mạng), vào VirtualBox > **Settings** > **Network** > Chọn card `dns-lab` > Mở rộng **Advanced** > Chuyển **Promiscuous Mode** thành **`Allow VMs`** (cho phép bắt gói tin unicast giữa các máy ảo trên cùng switch ảo).

#### Lỗi 2: Tấn công Task 2 / Task 3 thất bại (Victim luôn nhận IP đúng 172.16.6.10)

* **Hiện tượng:** Chạy tool giả mạo nhưng Victim vẫn truy cập trang web chuẩn trong DMZ.

* **Nguyên nhân:**

  1. Bản ghi hợp lệ đang nằm trong cache từ truy vấn trước đó nên không có gói tin query mới gửi đi.

  2. Gói tin trả lời thật từ AUTH-DNS phản hồi quá nhanh, đến trước gói tin giả của Attacker.

* **Cách khắc phục:**

  1. Xóa cache trên cả máy chủ và máy trạm trước mỗi lần chạy thử:

     ```
     # Trên DNS-SERVER
     sudo rndc flush
     # Trên VICTIM
     sudo resolvectl flush-caches
     
     ```

  2. Tăng tham số độ trễ trên `APP-TEST` lên 2.0s:

     ```
     sudo python3 auth_dns_delayed.py --bind 172.16.6.20 --delay 2.0
     
     ```

#### Lỗi 3: Resolver BIND9 từ chối gói tin giả mạo ở Task 3

* **Hiện tượng:** Gói tin giả mạo đã gửi tới port 53 của DNS-SERVER nhưng kiểm tra dump cache không có dữ liệu bị đầu độc.

* **Nguyên nhân:** Cơ chế DNS COOKIE mặc định của BIND9 phát hiện thiếu thông tin xác thực cookie hoặc sai lệch UDP Client Tuple.

* **Cách khắc phục:** Đảm bảo cấu hình `/etc/bind/named.conf.options` có cấu hình bỏ qua kiểm tra cookie đối với server mô phỏng:

  ```
  server 172.16.6.20 {
      send-cookie no;
  };
  
  ```

  Sau khi chỉnh sửa, chạy `sudo systemctl restart bind9`.

#### Lỗi 4: Không tìm thấy file dump cache khi chạy `rndc dumpdb`

* **Hiện tượng:** Chạy `sudo rndc dumpdb -cache` thành công nhưng mở `/var/cache/bind/named_dump.db` thì báo file không tồn tại.

* **Nguyên nhân:** Phiên bản BIND9 trên Ubuntu 24.04 lưu file dump vào đường dẫn mặc định theo profile AppArmor hoặc tên file dump khác biệt.

* **Cách khắc phục:** Dùng lệnh tìm kiếm tự động:

  ```
  sudo find /var/cache/bind/ -name "*dump*"
  # Đọc nội dung file tìm được:
  sudo grep -A 4 -B 4 "external.lab9.test" /var/cache/bind/named_dump.db
  
  ```

### 6. KẾT LUẬN & BIỆN PHÁP BẢO MẬT ĐỀ XUẤT

1. **Kiểm soát phân đoạn mạng (Segmentation):** Không đặt máy người dùng (Client) và máy chủ DNS dùng chung một Collision/Broadcast Domain với các thiết bị lạ.

2. **DNS Egress Filtering:** Cấu hình tường lửa pfSense chặn triệt để các truy vấn DNS trực tiếp ra Internet từ client; ép buộc mọi truy vấn phải đi qua DNS Resolver được ủy quyền.

3. **Triển khai DNSSEC:** Xác thực nguồn gốc và toàn vẹn của dữ liệu phản hồi DNS bằng chữ ký số mã hóa, loại bỏ hoàn toàn khả năng nhận dữ liệu giả mạo.

4. **Bật DNS COOKIE & Randomization:** Giữ mặc định cơ chế DNS Cookie (RFC 7873) và ngẫu nhiên hóa Transaction ID/Source Port trên BIND9 để phòng chống giả mạo trên đường truyền (on-path).