# ISMS — Integrated Student Management System

A Spring Boot web application for managing students, faculty, courses, attendance, grades, and payments in an academic institution.

---

## Features

- **JWT-based Authentication** — Secure login with role-based access (Admin, Student, Faculty)
- **Student & Faculty Management** — Bulk import via CSV or manual entry
- **Course & Enrolment** — Manage courses and student enrolments
- **Attendance Tracking** — Admin records attendance; students view their own
- **Grade Management** — Grades are visible to students only when attendance ≥ 75% and fees are fully paid
- **Payment Tracking** — Finance records and scheduled fee management
- **H2 In-Memory Database** — Zero-setup database with a built-in console

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ (or use the included `mvnw` wrapper) |

---

## Build & Run

**Clone the repository**
```bash
git clone <repository-url>
cd ISMS
```

**Run tests**
```bash
./mvnw clean test
```

**Build the JAR**
```bash
./mvnw clean package
```

**Run the application**
```bash
java -jar target/ISMS-0.0.1-SNAPSHOT.jar
```

Or run directly with Maven (skipping tests):
```bash
./mvnw spring-boot:run
```

The app starts on **`http://localhost:9090`**

---

## Default Credentials

> On first startup, sample data (students, faculty, courses, attendance, grades) is loaded automatically.

| Role    | Username / ID     | Password  |
|---------|-------------------|-----------|
| Admin   | `admin`           | `admin`   |
| Student | *(from CSV data)* | *(set during import)* |

---

## H2 Database Console

Access the in-memory database at:

```
http://localhost:9090/h2-console
```

| Field    | Value              |
|----------|--------------------|
| JDBC URL | `jdbc:h2:mem:isms_db` |
| Username | `user`             |
| Password | *(leave blank)*    |

---

## CI/CD

A `Jenkinsfile` is included with two pipeline stages:

1. **Testing** — `mvn clean test`
2. **Build** — `mvn clean package`

Failure notifications are sent via email to the configured `TESTER_EMAIL` / `BUILDER_EMAIL` environment variables in Jenkins.
