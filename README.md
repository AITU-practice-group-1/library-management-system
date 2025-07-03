# 📚 Library Management System

**Full-Stack Web Application built with Spring Boot (MVC Architecture)**


- [🧠 Description](#-description)
- [🛠️ Tech Stack](#️-tech-stack)
- [🚀 How to Run](#-how-to-run)

---

## 🧠 Description

  ![Image](https://github.com/user-attachments/assets/451c90a3-ea97-4038-879d-dc03bf843528)

This application is a **Library Management System** where users can:

- 🔍 Search all available and unavailable books


  ![Image](https://github.com/user-attachments/assets/1f2b0be0-159b-492a-8ffb-f14accafd4b4)
- 📚 Reserve and take books for a loan
- 📝 Add and report book reviews


  ![Image](https://github.com/user-attachments/assets/e45f1173-b269-4ff0-a522-24c032c8929e)
- 👤 View profiles of other users and their stats


  ![Image](https://github.com/user-attachments/assets/bb3be832-bf89-4f4b-a156-4bdc8ea7ac3e)
- ❤️ Create favorite book lists


  ![Image](https://github.com/user-attachments/assets/f431fcee-9a50-48bb-a8eb-96ed569d4618)

- 📄 Automatically generate contracts when taking loans


![Image](https://github.com/user-attachments/assets/5911c203-9ace-49ff-b406-34ab12ddbdef)
![Image](https://github.com/user-attachments/assets/d738704d-506c-4f1d-98d8-7e3331697dba)
### User Roles:
- **Default User**: Can explore, reserve books, manage favorites, and leave reviews

![Image](https://github.com/user-attachments/assets/c5ac3336-6050-4a55-98f4-c4fc232c71a5)

- **Librarian**: Can approve/reject reservations, manage loans, sign contracts, assign penalties

![Image](https://github.com/user-attachments/assets/deb63dfb-2a92-4535-aadd-c5389e9f31ec)

![Image](https://github.com/user-attachments/assets/ca9c0281-ec0f-4dc5-97e3-bfd2f4efb87d)

- **Admin**: Full control — manage reports, books, users, and enforce bans

![Image](https://github.com/user-attachments/assets/f17ca25b-b407-46b2-8c8a-3c81d0317375)

![Image](https://github.com/user-attachments/assets/2232eddd-986b-462c-a046-99b5234a7b1a)

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

For questions or bug reports, feel free to reach out via email:

- [Alikhan](mailto:khamidulla.alikhan@gmail.com)
- [Beksultan](mailto:beksultanaitkazy123@gmail.com)
- [Ali](mailto:ALI's email)
- [Madiyar](mailto:MADIYAR's email)
- [Rustemdastan](mailto:RUSTEMDASTAN's email)