# 📚 Library Management System

**Full-Stack Web Application built with Spring Boot (MVC Architecture)**


- [🧠 Description](#-description)
- [🛠️ Tech Stack](#️-tech-stack)
- [🚀 How to Run](#-how-to-run)

---

## 🧠 Description

This application is a **Library Management System** where users can:

- 🔍 Search all available and unavailable books
- 📚 Reserve and take books for a loan
- 👤 View profiles of other users and their stats
- ❤️ Create favorite book lists
- 📝 Add and report book reviews
- 📄 Automatically generate contracts when taking loans

### User Roles:
- **Default User**: Can explore, reserve books, manage favorites, and leave reviews
- **Librarian**: Can approve/reject reservations, manage loans, sign contracts, assign penalties
- **Admin**: Full control — manage books, users, and enforce bans

---

## 🛠️ Tech Stack

| Category               | Technology                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| Main Framework         | `Spring Boot`, `Spring Core`, `Spring Web` (MVC)                           |
| Validation             | `Jakarta Validator`, `Hibernate Validator`                                 |
| Security               | `Spring Security` (CSRF, CORS, session control, endpoint protection)       |
| Persistence            | `Spring Data JPA`                                                           |
| Database               | `PostgreSQL`                                                                |
| Caching                | `Redis`                                                                     |
| Messaging              | `Kafka`                                                                     |
| Containerization       | `Docker`                                                                    |
| File Storage           | `Cloudinary`                                                                |
| Authentication         | `Google Authenticator` (2FA)                                                |
| Document Generation    | `OpenHTML`                                                                  |
| Frontend Template      | `Thymeleaf`                                                                 |
| Logging                | `SLF4J`                                                                      |

---

## 🚀 How to Run

Before starting, **configure your credentials**:

### 1. `application.yml` – Mail & Data Source Config:
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: YOUR_EMAIL
    password: YOUR_APP_PASSWORD
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

  datasource:
    url: jdbc:postgresql://YOUR_HOST:YOUR_PORT/YOUR_DB_NAME
    username: YOUR_DB_USERNAME
    password: YOUR_DB_PASSWORD
```

⚠️ NOTE: YOUR_APP_PASSWORD is NOT your email password — it must be an App Password
generated via your email provider (e.g. Google).

    custom Section (also inside application.yml)
    custom:
      user_email: "YOUR_EMAIL"
      
cloudinary Section (still in application.yml)'

          cloudinary:          
            cloud_name: "your_cloud_name"            
            api_key: "your_api_key"            
            api_secret: "your_api_secret"
  

🐳 Docker Support

The application is containerized. To start:

   ```
   docker-compose up --build
  ```


💬 Contact

For questions or bug reports, feel free to reach out via email: khamidulla.alikhan@gmail.com
