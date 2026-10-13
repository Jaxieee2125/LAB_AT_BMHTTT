# BÁO CÁO THỰC HÀNH AN TOÀN HỆ THỐNG THÔNG TIN

## BÀI THỰC HÀNH: LAB 6 - XÁC THỰC ACTIVE DIRECTORY VÀ CHÍNH SÁCH TRUY CẬP TRÊN PFSENSE

### I. THÔNG TIN SINH VIÊN & BÀI THỰC HÀNH

* **Họ và tên:** Nguyễn Thành Thái

* **Mã số sinh viên (MSSV):** 1150080036

* **Lớp:** 11_CNPM1

* **Tên bài thực hành:** LAB 6 - Xác thực Active Directory và Chính sách truy cập trên pfSense

* **Mục tiêu:**

  * Tích hợp pfSense với Active Directory thông qua giao thức LDAP.

  * Cấu hình Captive Portal trên vùng LAN xác thực bằng tài khoản Domain.

  * Thiết lập Network Aliases, Schedules và triển khai bộ quy tắc tường lửa (Firewall Rules) đa kịch bản.

  * Phân tích cơ chế Stateful Inspection, First-match, kỹ thuật cô lập DMZ và phân biệt hành vi Block/Reject.

### II. PHIÊN BẢN MÔI TRƯỜNG & THÔNG SỐ HỆ THỐNG

* **Nền tảng ảo hóa:** VMware Workstation Pro / Oracle VM VirtualBox (v7.x)

* **pfSense-LAB6:** pfSense Community Edition v2.7.2-RELEASE (amd64)

  * Cấu hình phần cứng: 2 vCPU, 2 GB RAM, 16 GB Disk.

* **Domain Controller (DC01):** Windows Server 2019 / 2022 Datacenter

  * Cấu hình phần cứng: 2 vCPU, 2 - 4 GB RAM, AD DS + DNS Service.

  * Domain Name: `lab6.test`

* **Clients (Client-A & Client-B):** Windows 10 / Windows 11 Enterprise

  * Cấu hình phần cứng: 1 - 2 vCPU, 2 - 4 GB RAM.

* **DMZ Web Server (DMZ-WEB):** Ubuntu Server 22.04 LTS

  * Web Server: Nginx (mặc định lắng nghe trên cổng TCP/80).

### III. KIẾN TRÚC MẠNG & CÁCH DỰNG MÔI TRƯỜNG

#### 1. Topology mạng chi tiết

* **Vùng WAN:** Subnet `192.168.250.0/24` (NAT Network), pfSense nhận IP qua DHCP.

* **Vùng MGMT:** Subnet `192.168.56.0/24` (Host-Only Adapter), pfSense IP `192.168.56.254/24` (dành riêng WebGUI).

* **Vùng LAN (LAN-DOMAIN):** Subnet `10.6.0.0/24` (Internal Network / LAN Segment), pfSense IP `10.6.0.1/24`.

* **Vùng DMZ:** Subnet `172.16.6.0/24` (Internal Network / LAN Segment), pfSense IP `172.16.6.1/24`.

#### 2. Các bước dựng môi trường

##### Bước 1: Khởi tạo và gán card mạng pfSense

1. Gán 4 card mạng cho máy ảo pfSense tương ứng với 4 phân vùng: WAN, MGMT, LAN, DMZ.

2. Tại màn hình Console pfSense, chọn tùy chọn `1) Assign Interfaces`:

   * WAN: `em0` (hoặc `vtnet0`)

   * LAN: `em1` (hoặc `vtnet1`)

   * OPT1 (đổi tên MGMT): `em2`

   * OPT2 (đổi tên DMZ): `em3`

3. Chọn tùy chọn `2) Set interface(s) IP address` để đặt IP tĩnh theo sơ đồ mạng. Tắt toàn bộ DHCP Server trên LAN và DMZ (sử dụng IP tĩnh cho máy trạm để kiểm soát chính xác policy).

##### Bước 2: Thiết lập Domain Controller (DC01)

1. Đặt IP tĩnh: `10.6.0.10/24`, Gateway: `10.6.0.1`, DNS: `10.6.0.10`.

2. Cài đặt Server Role: **Active Directory Domain Services** và **DNS Server**.

3. Nâng cấp (Promote) máy chủ thành Domain Controller với Forest mới: `lab6.test`.

4. Mở **DNS Manager**, cấu hình **Forwarders** trỏ về Gateway hoặc DNS upstream (`8.8.8.8` / `1.1.1.1`).

5. Mở **Active Directory Users and Computers**:

   * Tạo Organizational Unit (OU): `LAB6-Users`.

   * Tạo 02 tài khoản người dùng: `sv_a` và `sv_b`.

   * Tạo Service Account: `svc_pfsense_ldap` (thuộc nhóm mặc định `Domain Users`, không cấp quyền `Domain Admins`, thiết lập *Password never expires*).

##### Bước 3: Thiết lập DMZ-WEB Server

1. Cài đặt hệ điều hành Ubuntu Server, gán địa chỉ IP chính `172.16.6.10/24`, Gateway `172.16.6.1`.

2. Cài đặt Nginx:

   ```
   sudo apt update && sudo apt install -y nginx
   
   ```

3. Gán thêm 3 địa chỉ IPv4 phụ (Secondary IP) trên cùng card mạng để mô phỏng nhóm dịch vụ:

   ```
   sudo ip addr add 172.16.6.20/24 dev enp0s3
   sudo ip addr add 172.16.6.30/24 dev enp0s3
   sudo ip addr add 172.16.6.40/24 dev enp0s3
   
   ```

   * `172.16.6.10`: GENERAL SITE

   * `172.16.6.20`: JOB SITE

   * `172.16.6.30`: SOCIAL SITE

   * `172.16.6.40`: RESTRICTED SITE

##### Bước 4: Tích hợp LDAP & Captive Portal trên pfSense

1. **LDAP Authentication Server:** Truy cập **System > User Manager > Authentication Servers**, kết nối tới DC01 (`10.6.0.10:389`), Base DN: `DC=lab6,DC=test`, Bind DN: `CN=svc_pfsense_ldap,CN=Users,DC=lab6,DC=test`.

2. **Captive Portal:** Truy cập **Services > Captive Portal**, tạo Zone `LAB6_USERS` gán vào interface LAN, chọn phương thức xác thực qua Backend `LAB6-AD`.

##### Bước 5: Khởi tạo Aliases và Schedules

* **Host Aliases:**

  * `CLIENT_A`: `10.6.0.21`

  * `CLIENT_B`: `10.6.0.22`

  * `POLICY_CLIENTS`: `10.6.0.21`, `10.6.0.22`

  * `GENERAL_SITE`: `172.16.6.10`

  * `JOB_SITE`: `172.16.6.20`

  * `SOCIAL_SITE`: `172.16.6.30`

  * `RESTRICTED_SITE`: `172.16.6.40`

* **Port Aliases:**

  * `WEB_PORTS`: `80`, `443`

* **Schedules:**

  * `LUNCH_WINDOW`: 11:30 - 13:00 (Thứ 2 đến Thứ 6)

  * `WORK_HOURS`: 08:00 - 17:00 (Thứ 2 đến Thứ 6)

### IV. BẢNG TỔNG HỢP KẾT QUẢ CÁC TÌNH HUỐNG THỰC HIỆN

| STT | Kịch bản / Mục tiêu kiểm thử | Trạng thái | Đánh giá chi tiết | 
| ----- | ----- | ----- | ----- | 
| **01** | **Xác thực LDAP tập trung** (`sv_a`, `sv_b`) | **PASS** | Kiểm thử tại *Diagnostics > Authentication* trả về kết quả *"User sv_a / sv_b authenticated successfully"*. | 
| **02** | **Xác thực qua Captive Portal** | **PASS** | Client-A và Client-B khi mở trình duyệt bị chặn chuyển hướng về cổng Captive Portal; sau khi nhập thông tin xác thực AD thì phiên duyệt web mở ra bình thường. | 
| **03** | **Kịch bản 1: Client-A bị chặn JOB SITE** | **PASS** | Client-A truy cập `172.16.6.10` bình thường; truy cập `172.16.6.20` bị Drop/Block; Firewall Log ghi nhận gói tin bị hủy. | 
| **04** | **Kịch bản 2: Cấm RESTRICTED SITE toàn bộ** | **PASS** | Cả Client-A và Client-B đều không thể truy cập `172.16.6.40`. | 
| **05** | **Kịch bản 3: Client-B truy cập SOCIAL trong giờ nghỉ** | **PASS** | Khi gán schedule kiểm thử hoạt động (Active), Client-B vào được `172.16.6.30`. Khi hết khung giờ (Inactive), rule Block phía dưới lập tức chặn kết nối. | 
| **06** | **Kịch bản 4: Giới hạn HTTP/HTTPS giờ hành chính** | **PASS** | Trong giờ hành chính, client chỉ gửi được lưu lượng Web (cổng 80, 443); ping ICMP hoặc dịch vụ khác bị chặn hoàn toàn. | 
| **07** | **Kịch bản 5: Cô lập vùng DMZ** | **PASS** | Chiều từ LAN sang DMZ hoạt động bình thường theo quy tắc cho phép; chiều từ DMZ chủ động kết nối/ping sang DC01 và LAN bị chặn đứng ở Interface DMZ. | 
| **08** | **Kịch bản 6: So sánh cơ chế Block và Reject** | **PASS** | Hành vi với **Block**: Client chờ hết thời gian phản hồi (*Connection Timed Out*). Hành vi với **Reject**: pfSense gửi cờ `TCP RST`, Client nhận thông báo từ chối kết nối ngay tức thì (*Connection Refused*). | 
| **09** | **Kịch bản 7: Kiểm tra State Table & Firewall Logs** | **PASS** | Ghi nhận chính xác IP nguồn, đích, cổng và hành động lọc. Xóa state tại *Diagnostics > States* giúp rule mới có hiệu lực ngay lập tức. | 

### V. LỖI GẶP PHẢI VÀ BIỆN PHÁP KHẮC PHỤC

#### 1. Lỗi không nhận đủ 4 card mạng trên pfSense

* **Hiện tượng:** Máy ảo đã thêm 4 Network Adapters trong phần mềm ảo hóa nhưng màn hình pfSense chỉ hiển thị `WAN (em0)` và `LAN (em1)`.

* **Nguyên nhân:** pfSense mặc định khi cài đặt chỉ tự động gán 2 card đầu tiên; 2 card mạng còn lại ở trạng thái Unassigned.

* **Cách khắc phục:** Truy cập Console chọn menu `1) Assign Interfaces` hoặc đăng nhập WebGUI vào `Interfaces > Assignments` để nhấn nút **Add** và gán thủ công `em2`, `em3`.

#### 2. Lỗi kiểm tra xác thực LDAP thất bại (Authentication Failed)

* **Hiện tượng:** Tại mục *Diagnostics > Authentication*, kiểm tra user `sv_a` báo lỗi kết nối hoặc sai thông tin.

* **Nguyên nhân:**

  1. Sai đường dẫn **Base DN** hoặc **Authentication Containers** (`OU=LAB6-Users,DC=lab6,DC=test`).

  2. Định dạng **Bind DN** của Service Account không chính xác hoặc gõ sai mật khẩu tài khoản `svc_pfsense_ldap`.

  3. Firewall của Windows Server chặn cổng TCP/389.

* **Cách khắc phục:**

  * Kiểm tra lại đường dẫn phân cấp trong Active Directory Users and Computers.

  * Mở Inbound Rules trên Windows Firewall cho dịch vụ Active Directory Domain Controller (LDAP Port 389).

  * Đặt phạm vi tìm kiếm (**Search Scope**) thành `Entire Subtree`.

#### 3. Thay đổi Rule tường lửa nhưng kết quả không đổi (Chặn không có tác dụng)

* **Hiện tượng:** Đã thêm rule Block đối với một website nhưng trên Client vẫn truy cập bình thường.

* **Nguyên nhân:** pfSense hoạt động theo cơ chế **Stateful Firewall**. Các phiên kết nối cũ trước khi đổi rule vẫn còn hiệu lực lưu trong **State Table**, khiến các gói tin tiếp theo bypass qua firewall rules.

* **Cách khắc phục:**

  1. Đảm bảo đã nhấn **Apply Changes** trên giao diện pfSense.

  2. Vào mục **Diagnostics > States > Reset States**, bấm **Reset** để xóa toàn bộ bảng trạng thái, buộc client phải tái thiết lập kết nối từ đầu.

#### 4. Rule Schedule ngoài giờ không tự động chặn truy cập

* **Hiện tượng:** Đã hết khung giờ cấu hình trong Schedule nhưng máy khách vẫn vào được website bị cấm.

* **Nguyên nhân:** Khi Schedule rơi vào trạng thái Inactive, pfSense chỉ vô hiệu hóa rule Pass đó (xem như rule không tồn tại) chứ không đảo ngược hành động thành Block. Nếu không có rule Block chỉ định, gói tin sẽ tiếp tục lọt xuống rule `Pass Any` mặc định phía dưới.

* **Cách khắc phục:** Luôn đặt một rule **Block rõ ràng (Explicit Block)** cùng đối tượng nằm ngay bên dưới rule Pass có gắn Schedule.

### VI. KẾT LUẬN

* Hoàn thành đầy đủ các yêu cầu của bài thực hành LAB 6.

* Nắm vững nguyên lý tích hợp hệ thống thư mục tập trung AD/LDAP vào thiết bị mạng và kiểm soát an ninh đa lớp dựa trên trạng thái gói tin.