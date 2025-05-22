# 🛍️ Fashion Store System - Microservices Architecture (Spring Boot)

Hệ thống quản lý cửa hàng thời trang được xây dựng theo mô hình **Microservice** sử dụng **Spring Boot**. Mục tiêu là phát triển một hệ thống có khả năng mở rộng, dễ bảo trì và linh hoạt, phục vụ cho các nghiệp vụ bán lẻ ngành thời trang như quản lý sản phẩm, đơn hàng, người dùng, thanh toán,...

---

## 🔧 Kiến trúc hệ thống

Dự án gồm nhiều service nhỏ độc lập giao tiếp qua REST hoặc message broker. Kiến trúc triển khai bao gồm:

- **API Gateway**: Định tuyến request đến các service
- **User Service**: Quản lý tài khoản người dùng, phân quyền
- **Product Service**: Quản lý danh mục, sản phẩm
- **Order Service**: Quản lý giỏ hàng, đơn đặt hàng
- **Payment Service**: Xử lý thanh toán (giả lập)
- **Notification Service**: Gửi email / SMS
- **Discovery Server (Eureka)**: Dò tìm các service tự động
- **Config Server**: Cấu hình tập trung cho các service

---

## 🛠️ Công nghệ sử dụng

- **Java 17 + Spring Boot**
- **Spring Cloud Netflix (Eureka, Gateway, Config)**
- **Spring Security + JWT**
- **MySQL
- **Redis / Kafka (tuỳ chọn nâng cao)**
- **Docker + Docker Compose**
- **Lombok, OpenFeign, RestTemplate**

---
