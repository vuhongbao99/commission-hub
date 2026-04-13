# CommissionHub 💰

> Internal commission management system for businesses — built with Spring Boot, JWT, MySQL

##  Tech Stack

**Backend**
- Java 17 + Spring Boot 4
- Spring Security + JWT Authentication
- Spring Data JPA + Hibernate 7
- MySQL 8

**Features**
- Apache POI (Excel export/import)
- WebSocket + STOMP (real-time)
- Scheduled jobs
- Docker + docker-compose
- GitHub Actions CI/CD

##  Business Flow

Accountant → Tạo hoa hồng → Thêm task → Hoàn thành task
Director   → Duyệt task → Tạo phiếu → Xuất Excel → Import kết quả NH
System     → Task tự PAID → Notification → OKRs tracking

## 🗄️ Database — 13 tables

| Group | Tables |
|-------|--------|
| Users | `users`, `teams` |
| Commission | `commissions`, `commission_tasks`, `commission_adjustments` |
| Payment | `payment_vouchers`, `bulk_payment_batches` |
| System | `notifications`, `audit_logs`, `refresh_tokens`, `system_config` |
| Extended | `okrs`, `bank_transfer_results` |

##  API Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Login | Public |
| POST | `/api/auth/refresh` | Refresh token | Public |
| POST | `/api/commissions` | Tạo hoa hồng | Accountant |
| POST | `/api/commissions/{id}/tasks` | Thêm task | Accountant |
| PUT | `/api/tasks/{id}/complete` | Hoàn thành task | Accountant |
| PUT | `/api/tasks/{id}/approve` | Duyệt task | Director |
| PUT | `/api/tasks/{id}/reject` | Từ chối task | Director |
| POST | `/api/vouchers` | Tạo phiếu thanh toán | Director |
| GET | `/api/excel/export-bulk-payment` | Xuất Excel bulk | Director |
| POST | `/api/excel/import-bank-result` | Import kết quả NH | Director |
| GET | `/api/stats/dashboard` | Dashboard stats | All |
| GET | `/api/stats/revenue-by-month` | Biểu đồ doanh thu | Director |
| GET | `/api/notifications` | Thông báo | All |
| PUT | `/api/users/{id}/resign` | Nhân viên nghỉ việc | Admin |
| GET | `/api/admin/users` | Danh sách nhân viên | Admin |
| POST | `/api/admin/users` | Tạo nhân viên | Admin |

##  Run Locally

**Prerequisites:** Java 17, MySQL 8, Maven

```bash
# Clone repo
git clone https://github.com/vuhongbao99/commission-hub.git
cd commission-hub

# Configure database in application.yaml

# Run
./mvnw spring-boot:run
```

**With Docker:**

```bash
docker-compose up -d
```

##  Test Accounts

| Email | Password | Role |
|-------|----------|------|
| admin@commissionhub.com | Password123@ | ADMIN |
| director@commissionhub.com | Password123@ | DIRECTOR |
| nva@commissionhub.com | Password123@ | ACCOUNTANT |

## ⚙️ Environment Variables

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | MySQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | MySQL username |
| `SPRING_DATASOURCE_PASSWORD` | MySQL password |
| `JWT_SECRET` | JWT secret key (min 32 chars) |
| `JWT_EXPIRATION` | Access token expiry (ms) |

##  Project Structure

src/main/java/com/commissionhub/
├── controller/     — REST API endpoints
├── service/        — Business logic
├── repository/     — Data access layer
├── entity/         — JPA entities (13 tables)
├── dto/            — Request/Response objects
├── security/       — JWT + Spring Security
├── exception/      — Global exception handling
└── enums/          — Enumerations



## 👨‍💻 Author

**Vũ Hồng Bảo** — [github.com/vuhongbao99](https://github.com/vuhongbao99)

