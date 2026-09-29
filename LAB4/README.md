# BÁO CÁO THỰC HÀNH

**Họ tên:** Nguyễn Thành Thái
**MSSV:** 1150080036
**Tên lab:** LAB 4 - KHẢO SÁT VÀ ĐÁNH GIÁ BỀ MẶT MẠNG BẰNG NMAP
**Youtube:** https://youtu.be/3XlW3QKPZOs
---

## 1. Phiên bản môi trường
* **Máy thật (Host):** Windows 10 hoặc Windows 11 64-bit
* **Nền tảng ảo hóa:** VirtualBox
* **Máy ảo quét (VM 1):** Kali Linux
* **Máy ảo đích (VM 2):** Metasploitable 2
* **Công cụ sử dụng:** Nmap, Npcap, Zenmap

## 2. Cách dựng môi trường
* Thiết lập mạng VirtualBox Host-Only Network với dải IP `192.168.56.0/24`.
* Cấu hình máy ảo Kali Linux kết nối vào mạng Host-Only với địa chỉ IP `192.168.56.129`.
* Cấu hình máy ảo Metasploitable 2 (máy đích có lỗ hổng) kết nối vào mạng Host-Only với địa chỉ IP `192.168.56.130`.
* Cài đặt Nmap và Npcap bản mới nhất trên máy thật Windows.
* Cập nhật và cài đặt Nmap trên Kali Linux bằng lệnh `sudo apt update` và `sudo apt install nmap`.

## 3. Các tình huống đã thực hiện & Kết quả (PASS/FAIL)
* **Kiểm tra kết nối trước khi quét:** Sử dụng lệnh `ping -c 4 192.168.56.130` từ Kali sang Metasploitable 2 để đảm bảo hai máy thông mạng.
  * **Kết quả:** PASS 
* **Nhiệm vụ 1 - Phát hiện các host đang hoạt động:** Sử dụng lệnh `sudo nmap -sn 192.168.56.0/24` để quét toàn bộ dải mạng Host-Only.
  * **Kết quả:** PASS
* **Khảo sát cổng bằng TCP Connect scan:** Sử dụng lệnh `nmap -sT 192.168.56.130` để quét cổng trên máy Metasploitable 2.
  * **Kết quả:** PASS
* **Khảo sát cổng bằng SYN scan:** Sử dụng lệnh `sudo nmap -sS 192.168.56.130` để quét cổng mục tiêu.
  * **Kết quả:** PASS

## 4. Lỗi gặp phải và cách khắc phục
* **Lỗi:** Gõ lệnh nmap trên Windows nhưng hệ thống báo "nmap is not recognized...".
  * **Cách khắc phục:** Đóng và mở lại Command Prompt/PowerShell hoặc cài lại Nmap và tích vào tùy chọn đăng ký PATH.
* **Lỗi:** Ping từ Kali sang Metasploitable 2 thất bại.
  * **Cách khắc phục:** Kiểm tra lại xem cả hai máy ảo đã được cấu hình chung mạng Host-Only Network chưa, kiểm tra IP có bị trùng không và tường lửa có chặn ICMP không.
* **Lỗi:** Chạy lệnh SYN scan (`-sS`) bị hệ thống từ chối do thiếu quyền.
  * **Cách khắc phục:** Thêm `sudo` trước câu lệnh vì kỹ thuật quét SYN yêu cầu quyền root hoặc đặc quyền tương đương.