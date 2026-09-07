# Library Management System

RESTful backend application for managing a library, built with Java and Spring Boot.

The application supports books, authors, genres, users, book borrowing/returning, JWT authentication and role-based access control.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- MockMvc

## Features

- User registration and login
- JWT-based authentication
- Roles: `USER` and `LIBRARIAN`
- BCrypt password encoding
- CRUD operations for books, authors and genres
- User management for librarians
- Book borrowing and returning
- Active and overdue borrow records
- Access to the current user's borrowing history
- DTO validation
- Global exception handling
- Automated service and security tests

## Authorization

`USER` can:

- view books, authors and genres
- view their own borrow records

`LIBRARIAN` can:

- manage books, authors and genres
- manage users
- borrow and return books
- view all borrow records

Protected endpoints require:

```text
Authorization: Bearer <JWT>
```

## Main API Endpoints

### Authentication

```text
POST /api/auth/login
POST /api/users
```

### Books

```text
GET    /api/books
GET    /api/books/{id}
POST   /api/books
PUT    /api/books/{id}
DELETE /api/books/{id}
```

### Authors

```text
GET    /api/authors
GET    /api/authors/{id}
POST   /api/authors
PUT    /api/authors/{id}
DELETE /api/authors/{id}
```

### Genres

```text
GET    /api/genres
GET    /api/genres/{id}
POST   /api/genres
PUT    /api/genres/{id}
DELETE /api/genres/{id}
```

### Borrow Records

```text
POST /api/borrow-records/borrow
POST /api/borrow-records/{id}/return

GET /api/borrow-records
GET /api/borrow-records/{id}

GET /api/borrow-records/me
GET /api/borrow-records/me/active
GET /api/borrow-records/me/overdue
```

## Testing

The project includes:

- `BorrowRecordServiceTest`
- `UserServiceTest`
- `SecurityAuthorizationTest`
- `JwtAuthenticationFilterTest`

The tests cover business logic, password encoding, role-based authorization, JWT handling and common error scenarios.

Run tests with:

```bash
mvn test
```

## Running the Project

Requirements:

- Java 21
- Maven
- PostgreSQL

Create a PostgreSQL database:

```text
library_management
```
Configure the required environment variables:

```text
DB_URL=jdbc:postgresql://localhost:5432/library_management
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
```

Run:

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```