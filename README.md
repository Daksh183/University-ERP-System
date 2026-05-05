# University ERP System

A desktop-based Enterprise Resource Planning system for university management, built with Java and Swing. Supports three user roles — Student, Instructor, and Admin — each with a dedicated dashboard and role-specific features.

---

## Demo

[Watch the full app walkthrough →](https://github.com/Daksh183/University-ERP-System/releases/tag/v1.0)

---

## Features

### Student
- Browse the course catalog and register for sections
- Drop courses before the drop deadline
- View personal timetable and current registrations
- Check grades by component (Quiz, Midterm, Final, etc.)
- View full academic transcript
- Duplicate course enrollment prevention

### Instructor
- View assigned sections and enrolled students
- Enter and update grades by component with weightage
- Publish final grades using configurable letter-grade cutoffs
- View class statistics and performance charts
- Export grade rosters to CSV

### Admin
- Create, update, and delete users (Students & Instructors)
- Reset user passwords
- Manage courses and sections (capacity, room, schedule)
- Toggle maintenance mode (disables registration and grade submission)
- Set registration and drop deadlines
- Backup and restore the ERP database

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| GUI | Java Swing + FlatLaf (Dark Theme) |
| Database | MySQL 8.0 |
| Build Tool | Maven |
| Password Hashing | jBCrypt |
| Charts | JFreeChart |

---

## Project Structure

```
src/edu/univ/erp/
├── Main.java                  # Entry point
├── access/                    # Role-based access control
├── auth/                      # Login, session, password hashing
├── data/                      # DAO interfaces and MySQL implementations
├── domain/                    # POJOs: User, Student, Instructor, Course, Section, Grade...
├── service/                   # Business logic: AdminService, StudentService, InstructorService
├── ui/
│   ├── admin/                 # Admin dashboard panels
│   ├── instructor/            # Instructor dashboard panels
│   ├── student/               # Student dashboard panels
│   ├── auth/                  # Login window, profile dialog
│   └── common/                # Shared components (maintenance banner)
└── util/                      # CSV/PDF export, schedule formatter
```

---

## Database Setup

The system uses two separate MySQL databases.

**1. Import the SQL dumps**

```bash
mysql -u root -p < auth_dump.sql
mysql -u root -p < erp_dump.sql
```

**2. Update the database password**

Open `src/edu/univ/erp/data/DatabaseConnector.java` and set your MySQL root password:

```java
private static final String AUTH_DB_PASSWORD = "your_password";
private static final String ERP_DB_PASSWORD  = "your_password";
```

| Database | Purpose |
|---|---|
| `university_auth_db` | User credentials and roles |
| `university_erp_db` | All operational data (courses, grades, enrollments) |

---

## How to Run

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/Daksh183/University-ERP-System.git

# 2. Import the databases (see Database Setup above)

# 3. Update your DB password in DatabaseConnector.java

# 4. Build the project
mvn package

# 5. Run the JAR
java -jar target/University-ERP-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or open the project in IntelliJ IDEA and run `Main.java` directly.

---

## Architecture

The project follows a layered architecture with clear separation of concerns:

- **UI Layer** — Swing panels per role, sidebar navigation, tabbed content
- **Service Layer** — Business logic with validation and access control checks
- **DAO Layer** — Interface-based data access with MySQL implementations
- **Domain Layer** — Plain Java objects (POJOs) for all entities
- **Auth Layer** — Session management (Singleton), BCrypt password hashing
- **Access Control** — Ownership enforcement (instructors can only grade their own sections), maintenance mode, admin-only operations

---

## Contributors

| Name | Role |
|---|---|
| Daksh Sheoran | Primary Developer |
| Divyaraj Chauhan | Co-Developer |

---

## Documents

- [Project Report](./Project%20Overall%20Report.pdf)
- [Test Plan](./Test%20Plan_%20University%20ERP%20System.pdf)
- [System Diagrams](./University%20ERP%20System%20-%20Required%20Project%20Diagrams.pdf)
- [How to Run (PDF)](./HOW%20TO%20RUN%20-%20University%20ERP%20System.pdf)
