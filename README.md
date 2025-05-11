# Grzesbank24 API

## Overview
Grzesbank24 is a robust banking API built with Spring Boot. It provides secure endpoints for user authentication, profile management, and transaction handling. The project emphasizes security, scalability, and modern development practices.

## Features
- **Authentication & Registration:** Secure login, registration, and password reset endpoints.
- **Profile Management:** Endpoints for retrieving and updating user data, including sensitive information and password changes.
- **Transactions:** Users can create and view transactions, with validation and error handling.
- **Security:**
  - **Session Management:** Only one active session per user, with session expiration handling.
  - **Brute-force Protection:** Rate limiting for login and password reset attempts per IP.
  - **Password Security:** Passwords are hashed using BCrypt.
  - **CORS Configuration:** Restricts allowed origins for API access.
  - **Request Logging:** Logs client info, headers, and payloads for auditing.
  - **CSRF Protection:** Disabled for API use, but other security measures are in place.

## Tech Stack
- Java 17
- Spring Boot
- Spring Security
- JPA (MySQL/SQLite)
- Lombok
- MapStruct

## Frontend
See the Flutter frontend at: [Grzesbank App](https://github.com/jewusiak/grzesbank_app)

