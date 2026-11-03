# BÁO CÁO THỰC HÀNH AN TOÀN HỆ THỐNG THÔNG TIN

## BÀI THỰC HÀNH: LAB 10 - REMOTE DNS CACHE POISONING TRONG MÔI TRƯỜNG pfSense CÔ LẬP

### 1. THÔNG TIN SINH VIÊN

* **Họ và tên:** Nguyễn Thành Thái

* **Mã số sinh viên (MSSV):** 1150080036

* **Lớp:** 11_CNPM1

### 2. TÊN BÀI LAB & MỤC TIÊU

* **Tên Lab:** **LAB 10 - Remote DNS Cache Poisoning trong môi trường pfSense cô lập (Kaminsky Simulation)**.

* **Mục tiêu:**

  * Phân biệt cơ chế tấn công **Local DNS Spoofing** (dựa vào việc nghe lén/sniffing ARP/gói tin trên cùng Broadcast Domain) và **Remote DNS Cache Poisoning / Kaminsky Attack** (tấn công mù từ xa qua nhiều subnet thông qua brute-force / guessing Transaction ID và UDP source port).

  * Thực hiện phân đoạn mạng bằng tường lửa pfSense nhằm cô lập Attacker khỏi DNS-LAB subnet, đảm bảo Attacker không thể bắt gói tin trao đổi giữa DNS-SERVER và Authoritative DNS.

  * Tái hiện điểm yếu của các DNS Resolver cổ điển (cố định source port) thông qua resolver mô phỏng (`vuln_resolver_lab10.py`), quan sát kỹ thuật vượt qua cơ chế lưu đệm DNS (Cache effect) bằng cách sinh subdomain ngẫu nhiên kết hợp chèn bản ghi NS độc hại (NS delegation + glue A record).

  * Đánh giá và áp dụng các giải pháp phòng thủ chuyên sâu (Hardening): Thu hẹp đệ quy (Recursion ACL), kỹ thuật ngẫu nhiên hóa cổng nguồn (Source-Port Randomization trên BIND 9.18+), cấu hình DNSSEC validation và phân vùng tường lửa (Firewall Segmentation).

### 3. PHIÊN BẢN MÔI TRƯỜNG & HỆ THỐNG

* **Phần mềm ảo hóa:** Oracle VM VirtualBox (v7.0+)

* **Hệ điều hành & Dịch vụ:**

  * **pfSense Firewall:** v2.7.x CE (6 interfaces cấu hình mạng cô lập).

  * **DNS-SERVER:** Ubuntu Server 22.04 LTS (IP: `10.9.0.10/24`), BIND 9.18+, Python 3.10+ (`python3-dnspython`).

  * **VICTIM (Client):** Ubuntu Desktop / Linux Client (IP: `10.9.0.100/24`), DNS chỉ định về `10.9.0.10`.

  * **AUTH-DNS / RELAY (APP-TEST):** Ubuntu Server 22.04 LTS (IP: `172.16.6.20/24`), chạy `auth_dns_relay_lab10.py` lắng nghe UDP port 53 & UDP port 5300.

  * **DMZ-WEB (Legitimate Web):** Apache2 / Nginx (IP: `172.16.6.10/24`, port 80).

  * **ATTACKER (Kali Linux):** Kali Linux 2024.x/2025.x (IP: `10.10.0.200/24`), cài sẵn `python3-dnspython`, `dnsutils`.

  * **HOST MGMT:** Host machine (IP: `192.168.56.1`), quản trị pfSense WebGUI qua `192.168.56.254:443/80`.

* **Phạm vi kiểm thử mạng (RFC 2606):**

  * Tên miền thử nghiệm: `example.lab10.test`

  * Dải mạng phân định rõ:

    * `DNS-LAB`: `10.9.0.0/24` (Gateway: `10.9.0.1`)

    * `ATTACK-NET`: `10.10.0.0/24` (Gateway: `10.10.0.1`)

    * `DMZ`: `172.16.6.0/24` (Gateway: `172.16.6.1`)

    * `MGMT`: `192.168.56.0/24` (pfSense: `192.168.56.254`)

### 4. CÁCH DỰNG VÀ CHUẨN BỊ MÔI TRƯỜNG

#### Bước 4.1: Tạo Snapshot và Tái cấu trúc Card Mạng trên pfSense

1. Tắt toàn bộ máy ảo liên quan: `pfSense`, `DNS-SERVER`, `VICTIM`, `Kali`.

2. Tạo snapshot trạng thái an toàn: `LAB10_START_FROM_LAB9`.

3. Bật thêm Adapter 6 trên máy ảo pfSense:

   * **Attached to:** `Internal Network`

   * **Name:** `attack-net`

   * **Cable Connected:** Tick chọn.

4. Bật máy ảo pfSense, truy cập WebGUI (`https://192.168.56.254`):

   * Vào **Interfaces** > **Assignments** > Nhấn **Add** interface mới vừa nhận.

   * Cấu hình interface:

     * **Enable Interface:** Tick chọn.

     * **Description:** `ATTACKNET`

     * **IPv4 Configuration Type:** `Static IPv4`

     * **IPv4 Address:** `10.10.0.1 / 24`

     * **IPv4 Upstream Gateway:** `None` *(Tuyệt đối không gán gateway để tránh bị nhận dạng nhầm thành WAN-type)*.

   * Nhấn **Save** > **Apply Changes**.

#### Bước 4.2: Tách Attacker (Kali) Khỏi DNS-LAB Subnet

1. Trên máy ảo Kali:

   * Tắt máy, gỡ bỏ/disable card mạng kết nối `dns-lab`.

   * Gán card mạng mới: `Internal Network` = `attack-net`.

   * Đặt **Promiscuous Mode** = `Deny` (LAB 10 cấm sniff chéo subnet).

2. Khởi động Kali và cấu hình IP tĩnh:

   ```
   sudo ip addr add 10.10.0.200/24 dev eth0
   sudo ip route add default via 10.10.0.1
   ping -c 3 10.10.0.1
   
   ```

#### Bước 4.3: Thiết lập Quy tắc Tường lửa (Firewall Rules) trên pfSense

Vào mục **Firewall** > **Rules** trên pfSense và áp dụng ma trận phân quyền:

* **Tab `DNSLAB`:**

  1. *Pass* | IPv4 UDP/TCP | Source: `10.9.0.100` | Port: `*` | Dest: `10.9.0.10` | Port: `53` (Victim -> Resolver).

  2. *Pass* | IPv4 UDP | Source: `10.9.0.10` | Port: `*` | Dest: `172.16.6.20` | Port: `53` (Resolver -> AUTH-DNS).

  3. *Pass* | IPv4 UDP | Source: `10.9.0.10` | Port: `*` | Dest: `10.10.0.200` | Port: `53` (Resolver -> Evil DNS).

  4. *Pass* | IPv4 TCP | Source: `10.9.0.0/24` | Port: `*` | Dest: `10.10.0.200` | Port: `80` (Victim truy cập Fake Web).

  5. *Block & Log* | IPv4 \* | Source: `10.9.0.0/24` | Dest: `10.6.0.0/24` (Bảo vệ LAN).

  6. *Block & Log* | IPv4 \* | Source: `10.9.0.0/24` | Dest: `192.168.56.0/24` (Bảo vệ MGMT).

  7. *Block & Log* | IPv4 \* | Source: `10.9.0.0/24` | Dest: `*` (Chặn mặc định / Cách ly).

* **Tab `ATTACKNET`:**

  1. *Pass* | IPv4 UDP/TCP | Source: `10.10.0.200` | Port: `*` | Dest: `10.9.0.10` | Port: `53` (Attacker query Resolver).

  2. *Pass* | IPv4 UDP | Source: `10.10.0.200` | Port: `*` | Dest: `172.16.6.20` | Port: `5300` (Race Harness Relay).

  3. *Block & Log* | IPv4 \* | Source: `10.10.0.0/24` | Dest: `10.6.0.0/24` (Bảo vệ LAN).

  4. *Block & Log* | IPv4 \* | Source: `10.10.0.0/24` | Dest: `192.168.56.0/24` (Bảo vệ MGMT).

  5. *Block & Log* | IPv4 \* | Source: `10.10.0.0/24` | Dest: `*` (Chặn ra WAN và các DMZ khác).

* *Sau khi lưu: Thực hiện **Apply Changes** và vào **Diagnostics** > **States** > **Reset States**.*

#### Bước 4.4: Thiết lập Baseline và Dịch vụ Phía Server

1. **Trên DNS-SERVER (`10.9.0.10`):**

   * Sao lưu file cấu hình BIND9:

     ```
     sudo cp /etc/bind/named.conf.options /etc/bind/named.conf.options.lab9.bak
     sudo cp /etc/bind/named.conf.local /etc/bind/named.conf.local.lab9.bak
     
     ```

   * Cập nhật zone forward `example.lab10.test` trỏ về `172.16.6.20`, thiết lập `recursion` chỉ cho phép `10.9.0.0/24`, cấu hình `dnssec-validation auto;` và `validate-except { "example.lab10.test"; };`.

   * Khởi chạy BIND9:

     ```
     sudo named-checkconf
     sudo systemctl restart bind9
     sudo ss -lunp | grep :53
     
     ```

2. **Trên AUTH-DNS / RELAY (`172.16.6.20`):**

   * Dừng dịch vụ DNS cũ và khởi chạy relay harness của Lab:

     ```
     sudo python3 auth_dns_relay_lab10.py
     
     ```

     *(Trả về IP `172.16.6.10` cho www; tạo trễ 3s cho subdomain ngẫu nhiên để mở race window).*

### 5. CÁC TÌNH HUỐNG ĐÃ THỰC HIỆN & KẾT QUẢ KIỂM THỬ

| STT | Tình huống kiểm thử | Mô tả chi tiết & Lệnh thực thi | Kết quả mong đợi | Kết quả thực tế | Đánh giá | 
| ----- | ----- | ----- | ----- | ----- | ----- | 
| **TH1** | **Xác nhận Baseline DNS hợp lệ** | Từ máy VICTIM (`10.9.0.100`), xóa cache và truy vấn web hợp lệ:  `sudo resolvectl flush-caches`  `resolvectl query www.example.lab10.test`  `curl http://www.example.lab10.test` | Phân giải ra IP `172.16.6.10`; curl hiển thị trang web chuẩn của `DMZ-WEB`. | Phân giải chính xác `172.16.6.10`; phản hồi đúng trang web gốc. | **PASS** | 
| **TH2** | **Kiểm chứng tính "Remote" (Không thể Sniffing)** | Trên Kali (`10.10.0.200`), chạy sniffer:  `sudo tcpdump -ni any udp port 53`  Đồng thời từ VICTIM thực hiện query tên miền liên tục. | Kali chỉ thấy traffic do chính nó tạo ra; hoàn toàn **không** thấy gói tin giữa `10.9.0.10` và `172.16.6.20`. | Không bắt được bất kỳ gói tin DNS nào giữa Resolver và Server DMZ. | **PASS** | 
| **TH3** | **Khởi động Resolver mô phỏng & Dịch vụ Giả mạo** | Trên DNS-SERVER: dừng BIND9, chạy resolver dễ bị tổn thương:  `sudo systemctl stop bind9`  `sudo python3 vuln_resolver_lab10.py`  Trên Kali: mở 2 terminal chạy Evil DNS và Fake Web:  `sudo python3 evil_dns_lab10.py`  `sudo python3 -m http.server 80 --directory ~/LAB10/fake_site` | Resolver cố định source port `33333`; Evil DNS lắng nghe port 53; Fake web lắng nghe port 80 sẵn sàng. | Cả 3 tiến trình khởi động thành công và bind đúng port/IP tương ứng. | **PASS** | 
| **TH4** | **Thực hiện tấn công Remote Cache Poisoning (Kaminsky)** | Trên Kali, kích hoạt script tấn công TXID guessing:  `python3 kaminsky_remote_lab10.py`  Attacker gửi query ngẫu nhiên để kích hoạt Resolver hỏi upstream, đồng thời gửi batch đoán TXID qua relay harness. | AUTH-DNS xuất hiện `[HIT]`; Resolver báo log `[CACHE] delegation poisoned`. Script verify in ra `www.example.lab10.test A = 10.10.0.200`. | Log hiển thị `[HIT]` và `[CACHE]`, bản ghi ủy quyền NS độc hại được nạp vào resolver cache. | **PASS** | 
| **TH5** | **Kiểm tra tác động trên Victim Client** | Trên VICTIM (`10.9.0.100`), thực hiện truy vấn lại:  `sudo resolvectl flush-caches`  `resolvectl query www.example.lab10.test`  `curl http://www.example.lab10.test` | Resolver chuyển hướng truy vấn sang Evil DNS (`10.10.0.200`); `curl` hiển thị nội dung **LAB10 Fake Site**. | Victim bị nhiễm độc DNS hoàn toàn, truy cập trúng trang web giả mạo của Kali. | **PASS** | 
| **TH6** | **Khôi phục BIND9 & Hardening (Chống Poisoning)** | Dừng resolver mô phỏng, khởi chạy lại BIND9 chuẩn:  `sudo pkill -f vuln_resolver_lab10.py`  `sudo systemctl start bind9`  `sudo rndc flush`  Từ VICTIM gửi nhiều query ngẫu nhiên, trên DNS-SERVER bắt gói:  `sudo tcpdump -ni any host 172.16.6.20 and udp port 53` | BIND 9.18 tự động kích hoạt **Source-port randomization** (cổng UDP nguồn thay đổi liên tục, không còn cố định `33333`); Kali bị từ chối recursion. | UDP source port ngẫu nhiên hoàn toàn trên dải động; script tấn công vô tác dụng. | **PASS** | 

### 6. CÁC LỖI GẶP PHẢI VÀ BIỆN PHÁP KHẮC PHỤC

* **Lỗi 1: VICTIM không phân giải được Baseline (Báo NXDOMAIN hoặc SERVFAIL)**

  * *Nguyên nhân:* Dịch vụ `auth_dns_relay_lab10.py` trên APP-TEST chưa chạy, hoặc tường lửa pfSense thiếu rule cho phép gói tin từ Resolver (`10.9.0.10`) sang DMZ AUTH-DNS (`172.16.6.20:53`).

  * *Cách khắc phục:* Kiểm tra trạng thái tiến trình Python trên APP-TEST; bổ sung rule tại tab `DNSLAB` trên pfSense cho phép `10.9.0.10` UDP port 53 đến `172.16.6.20`. Sau đó thực hiện *Reset States* trong pfSense.

* **Lỗi 2: Kali vẫn bắt được gói tin DNS của Resolver và AUTH-DNS khi tcpdump**

  * *Nguyên nhân:* Card mạng ảo của máy Kali vẫn còn gắn vào switch ảo `dns-lab` từ bài Lab trước, hoặc thuộc tính card mạng trong VirtualBox đang bật *Promiscuous Mode = Allow All*.

  * *Cách khắc phục:* Tắt máy ảo Kali, vào Settings VirtualBox > Network: Ngắt hoàn toàn adapter nối với `dns-lab`, chỉ giữ một adapter nối vào `attack-net`, chỉnh lại **Promiscuous Mode** thành **Deny**.

* **Lỗi 3: Chạy script tấn công trên Kali nhưng AUTH-DNS không ghi nhận \[HIT\]**

  * *Nguyên nhân:* Cổng UDP `5300` trên pfSense chưa được mở từ Kali (`10.10.0.200`) đến AUTH-DNS/RELAY (`172.16.6.20`), khiến gói tin batch đoán TXID bị firewall drop âm thầm.

  * *Cách khắc phục:* Bổ sung rule tại tab `ATTACKNET` trên pfSense: Cho phép Source `10.10.0.200` đến Destination `172.16.6.20` Port `UDP/5300`. Nhấn *Apply Changes* và xóa bảng trạng thái firewall.

* **Lỗi 4: Tấn công báo thành công nhưng Victim vẫn truy cập ra IP gốc 172.16.6.10**

  * *Nguyên nhân:* Bộ nhớ cache cục bộ của `systemd-resolved` trên máy VICTIM vẫn đang lưu giữ kết quả truy vấn cũ có TTL dài.

  * *Cách khắc phục:* Chạy lệnh xóa triệt để cache trên VICTIM bằng lệnh `sudo resolvectl flush-caches` trước khi thực hiện truy vấn kiểm tra kết quả.

* **Lỗi 5: BIND9 không thể khởi động lại sau giai đoạn tấn công (Báo lỗi Failed)**

  * *Nguyên nhân:* Tiến trình `vuln_resolver_lab10.py` vẫn đang chạy ngầm và chiếm dụng socket `10.9.0.10:53`.

  * *Cách khắc phục:* Chạy lệnh `sudo pkill -9 -f vuln_resolver_lab10.py` để giải phóng socket, kiểm tra lại bằng `sudo ss -lunp | grep :53`, sau đó chạy `sudo systemctl start bind9`.

### 7. TỔNG KẾT & ĐÁNH GIÁ

* Toàn bộ các kịch bản thực nghiệm từ thiết lập phân vùng pfSense, baseline, mô phỏng tấn công Kaminsky Remote Cache Poisoning cho đến phục hồi và hardening hệ thống BIND9 đều đạt trạng thái **PASS (100%)**.

* **Bài học rút ra:**

  * Cơ chế bảo vệ cốt lõi của DNS chống lại Remote Cache Poisoning phụ thuộc sống còn vào entropy kết hợp giữa **16-bit Transaction ID** ($2^{16} = 65,536$) và **UDP Source-Port Randomization** (\~15-16 bit $\approx 2^{31} - 2^{32}$ không gian đoán). Khi resolver bị ép cổng cố định, cuộc tấn công Kaminsky dễ dàng thành công thông qua kỹ thuật vượt cache bằng chuỗi tên miền ngẫu nhiên.

  * Triển khai cấu hình bảo mật chuẩn yêu cầu: Tắt tính năng Open Recursion (chỉ cho phép subnet tin cậy), sử dụng phiên bản resolver hiện đại có ngẫu nhiên hóa cổng nguồn, phân đoạn firewall nghiêm ngặt và triển khai xác thực toàn vẹn bằng **DNSSEC**.