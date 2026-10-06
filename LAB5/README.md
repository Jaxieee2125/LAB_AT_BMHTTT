# Báo cáo Thực hành: Thiết lập mô hình tường lửa pfSense (Lab 5)

## 1. Thông tin sinh viên
- **Họ và tên:** Nguyễn Thành Thái	
- **MSSV:** 1150080036
- **Tên Lab:** Lab 5 - Thiết lập mô hình tường lửa pfSense (Thực hành An toàn Hệ thống Thông tin)
- **Youtube:** https://youtu.be/daE6iO0w2uc

## 2. Phiên bản môi trường
- **Phần mềm ảo hóa:** VMware Workstation
- **Hệ điều hành tường lửa:** pfSense CE 2.7.2-RELEASE (amd64)
- **Hệ điều hành máy trạm/máy chủ:** Windows Server (Domain Controller)

## 3. Cách dựng môi trường
Mô hình được triển khai hoàn toàn trên nền tảng VMware ảo hóa với cấu hình mạng như sau:
- **pfSense (Tường lửa trung tâm):** Được cấu hình 3 Network Adapter theo thứ tự:
  - Adapter 1 (WAN): Bridged (Kết nối trực tiếp Internet)
  - Adapter 2 (LAN): Host-only (VMnet1) - Quản lý dải mạng 10.0.0.0/8
  - Adapter 3 (DMZ): LAN Segment (dmz-net) - Quản lý dải mạng 172.16.0.0/16
- **Domain Controller (vietnam.local):** 
  - Gắn vào mạng Host-only (VMnet1).
  - IP tĩnh: 10.0.0.2 / Subnet mask: 255.0.0.0 / Gateway: 10.0.0.1 / DNS: 10.0.0.2.
- **Máy thật (Host quản trị):** 
  - Cấu hình tĩnh trên card VMware Network Adapter VMnet1: IP 10.0.0.100 / Subnet mask 255.0.0.0 (Không đặt Gateway và DNS).
  - Tắt dịch vụ DHCP mặc định của VMnet1 trên Virtual Network Editor (Subnet IP: 10.0.0.0).

## 4. Các tình huống đã thực hiện
*(Lưu ý: Hiện tại mới thực hiện đến phần cấu hình nền tảng, chưa vào các bài tập tình huống Firewall cụ thể)*

1. Cài đặt pfSense từ file ISO và gán địa chỉ IP cho cổng LAN (10.0.0.1) thông qua pfSense Console.
2. Cài đặt Active Directory Domain Services và promote máy Windows Server thành Domain Controller (forest: `vietnam.local`).
3. Cấu hình mạng Host-only trên VMware và máy tính thật.
4. Truy cập thành công vào giao diện quản trị WebGUI (Dashboard) của pfSense qua IP `https://10.0.0.1`.

## 5. Kết quả
- **Cấu hình nền tảng:** PASS (Đã vào được trang Dashboard của pfSense).

## 6. Lỗi gặp phải và cách khắc phục
Trong quá trình cấu hình môi trường nền tảng, em đã gặp một số lỗi và xử lý như sau:

- **Lỗi 1:** Đòi mật khẩu đăng nhập tài khoản `VIETNAM\Administrator` sau khi Promote lên Domain Controller.
  - *Nguyên nhân:* Khi nâng cấp lên Domain Controller, tài khoản Local Administrator chuyển thành Domain Administrator.
  - *Cách khắc phục:* Nhập lại mật khẩu của tài khoản Administrator cục bộ đã tạo lúc cài đặt Windows Server ban đầu.
- **Lỗi 2:** Điền nhầm Subnet IP là `10.0.0.100` trong Virtual Network Editor.
  - *Nguyên nhân:* Nhầm lẫn giữa IP của card mạng trên host và Subnet IP đại diện cho toàn mạng LAN.
  - *Cách khắc phục:* Chỉnh lại Subnet IP thành `10.0.0.0` (giữ nguyên Subnet mask `255.0.0.0`).
- **Lỗi 3:** Ping từ máy thật đến pfSense (10.0.0.1) báo lỗi `Destination host unreachable` và `TTL expired in transit`.
  - *Nguyên nhân:* Thiết lập sai thứ tự card mạng trên VMware (Adapter 2 đặt thành LAN Segment, Adapter 3 đặt thành Host-only) khiến máy thật (dùng Host-only) không tìm thấy cổng LAN của pfSense.
  - *Cách khắc phục:* Đổi lại đúng thứ tự Network Adapter trong phần Settings của máy ảo pfSense: Adapter 2 là Host-only và Adapter 3 là LAN Segment.