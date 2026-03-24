# 🛟 Rescue & Event Management System (Backend API)

A robust and secure RESTful API built with **Spring Boot** to manage rescue operations, volunteer events, categories, and user registrations. The system features a comprehensive Role-Based Access Control (RBAC) mechanism for Admins and Customers.

## 🚀 Features

- **Authentication & Authorization:** Secure login and registration using Spring Security and JWT (JSON Web Tokens).
- **User Management:** CRUD operations for user accounts with strict role-based permissions (Admin/Customer).
- **Event & Category Management:** Create, read, update, and delete events and categories.
- **Event Registration:** Users can register/unregister for events, check their status, and Admins can manage check-ins.
- **API Documentation:** Interactive and auto-generated API documentation using Swagger UI (OpenAPI 3).

## 🛠️ Tech Stack

- **Framework:** Java Spring Boot 3
- **Database:** MySQL
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security, BCrypt Password Encoder
- **Documentation:** Springdoc OpenAPI (Swagger UI)

## ⚙️ Prerequisites

Before you begin, ensure you have met the following requirements:
- **Java:** JDK 17 or higher
- **Maven:** 3.6+
- **Database:** MySQL Server (running locally on port 3306)

## 🛠️ Local Setup & Installation

Follow these steps to get the project running on your local machine:

**1. Clone the repository:**
```bash
git clone [https://github.com/hfuoc-04/event_management.git](https://github.com/hfuoc-04/event_management.git)
cd event_management
```
**2. Configure the Database:**
   Open your MySQL Workbench or terminal and create a new database:
```SQL
CREATE DATABASE rescue_db;
```
**3. Update Database Credentials:**
Navigate to ```src/main/resources/application.properties``` and update the database username and password to match your local MySQL configuration:
```Properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```
**4. Run the Application:**
You can run the application using your IDE (IntelliJ/Eclipse) or via Maven command line:
```bash
mvn spring-boot:run
```
**Note: Hibernate is configured to update (ddl-auto), so all database tables will be created automatically upon the first run.**

## 📚 API Documentation (Swagger)
Once the application is running (default port: 8081), you can access the interactive API documentation to test the endpoints:

- Swagger UI: http://localhost:8081/swagger-ui/index.html

- OpenAPI JSON: http://localhost:8081/v3/api-docs

(Note: For secured endpoints, you need to register/login first, copy the Token, and click the "Authorize" button 🔒 in Swagger UI to authenticate your requests).

## 👤 Author
GitHub: @hfuoc-04

