# Online Library Management System

A web-based library management application built with **Java, Spring Boot, Thymeleaf and MySQL**.
Librarians manage books, members, transactions and notifications, while members search the catalog, borrow and return books, and track their borrowing history.

> College Java project (Review 1: project structure, database design, database connectivity and UI design).

## Team

| Name | Role |
|---|---|
| Nagendra Rana | Leader |
| Jatin Kumar | Team Member |
| Mohd Junaid | Team Member |

## Features

### Librarian
- **Book management**: add, edit, delete books (title, author, ISBN, genre, copies)
- **Member management**: create, view, delete member accounts
- **Transaction management**: issue books, mark returns, overdue status tracking
- **Notification management**: send a message to one member or broadcast to all
- **Dashboard**: total books, members and transactions

### Member
- **Book search** by title, author or genre
- **Borrow and return** books (borrow is disabled when no copies are available)
- **Borrowing history** with issue date, due date, return date and status
- **Profile management**: update name, phone and password
- **Notifications**: due-date reminder on borrow, new-arrival alerts, messages from the librarian

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3 (Web, Data JPA, Validation) |
| Database | MySQL (via JDBC / Spring Data JPA) |
| Frontend | Thymeleaf, Bootstrap 5 |
| Build tool | Maven |
| Security | Session-based login, SHA-256 password hashing, role filter |

## Project Structure

```
library-management-system/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/library/
    │   ├── LibraryManagementSystemApplication.java
    │   ├── entity/        User, Book, Transaction, Notification, enums
    │   ├── repository/    Spring Data JPA repositories
    │   ├── service/       Business logic (borrow/return, search, notifications)
    │   ├── controller/    Auth, Dashboard, Book, Member, Transaction, Notification
    │   ├── filter/        AuthFilter (role-based access guard)
    │   ├── config/        DataSeeder (default librarian account)
    │   └── util/          PasswordUtil (SHA-256 hashing)
    └── resources/
        ├── templates/     Thymeleaf HTML pages
        ├── static/css/    Stylesheet
        └── application.properties
```

## Database Design

| Table | Main columns |
|---|---|
| `users` | id, name, email, password (hash), phone, membershipId, role (LIBRARIAN / MEMBER) |
| `books` | id, title, author, isbn, genre, totalCopies, availableCopies |
| `transactions` | id, book_id, member_id, issueDate, dueDate, returnDate, status |
| `notifications` | id, member_id, message, type, createdAt, isRead |

Relationships: a transaction links one book and one member (many-to-one each); a notification belongs to one member.

## Requirements

- JDK 17 or higher
- Maven 3.8+ (or use the Maven support built into IntelliJ IDEA / Eclipse)
- MySQL Server running on `localhost:3306`

## How to Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/library-management-system.git
   cd library-management-system
   ```

2. **Configure the database** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=<your-mysql-password>
   ```
   The `library_db` database and all tables are created automatically on first run.

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or open the project in your IDE and run `LibraryManagementSystemApplication`.

4. **Open in browser**: http://localhost:8080/login

## Default Login

A librarian account is created automatically on first startup:

| Role | Email | Password |
|---|---|---|
| Librarian | `admin@library.com` | `admin123` |

### Demo Member Logins

Created automatically on first startup (for testing only):

| Name | Email | Password |
|---|---|---|
| Prashant | `prashant@library.com` | `prashant0000` |
| Jatin | `jatin@library.com` | `jatin0000` |
| Diyanshu | `diyanshu@library.com` | `diyanshu0000` |
| Muskan | `muskan@library.com` | `mushkan0000` |
| Riya | `riya@library.com` | `riya0000` |
| Khushi | `khushi@library.com` | `khushi0000` |

Members can also register themselves from the **Register** page, or the librarian can create member accounts.

## Core Java Concepts Used

- **OOP**: encapsulation (entities with private fields and getters/setters), constructor overloading, interfaces (`JpaRepository`, `Filter`, `CommandLineRunner`), method overriding
- **Enums**: `Role`, `TransactionStatus`, `NotificationType`
- **Exception handling**: `IllegalArgumentException` / `IllegalStateException` thrown in services and handled in controllers
- **Collections and Generics**: `List<Book>`, `Optional<User>`, `JpaRepository<Book, Long>`
- **Servlets and web integration**: `AuthFilter` (servlet `Filter`), `HttpSession`, Spring MVC on top of the Servlet API
- **Database integration**: MySQL via the JDBC driver (`mysql-connector-j`) and Spring Data JPA

## Screenshots

_Add screenshots of the Login page, Librarian Dashboard and Book Search page here._

```
![Login](docs/login.png)
![Librarian Dashboard](docs/librarian-dashboard.png)
![Book Search](docs/books.png)
```

## Notes

- The loan period is 14 days from the issue date.
- Passwords are stored as SHA-256 hashes, never as plain text.
- Access to `/librarian/**` and `/member/**` pages is restricted by role through `AuthFilter`.
