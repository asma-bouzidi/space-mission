# Space Mission Control

A Java Swing desktop application for spacecraft operations, mission planning, telemetry monitoring, anomaly management, and operational reporting.

## Overview

This project implements a full-stack-style desktop application with a layered architecture:

- Model layer for domain entities
- DAO layer for JDBC persistence
- Service layer for business logic and validation
- Swing UI layer for the desktop experience
- MySQL database for persistent storage

The app includes real functionality for:

- Mission creation and tracking
- Spacecraft management
- User authentication and authorization
- Resource management
- Telemetry monitoring
- Alert and anomaly handling
- Communication logs
- Mission planning
- Reports and dashboard analytics
- Settings persistence

## Technology stack

- Java 17
- Maven
- Swing
- FlatLaf
- MySQL 8
- JDBC
- JFreeChart
- BCrypt
- JUnit 5

## Prerequisites

Before running the project, install:

- Java 17 or newer
- Maven 3.8 or newer
- MySQL 8 or newer

## Database setup

1. Create the database and user:

```sql
CREATE DATABASE spacemission_db;
CREATE USER 'spacemission_app'@'%' IDENTIFIED BY 'change_me';
GRANT ALL PRIVILEGES ON spacemission_db.* TO 'spacemission_app'@'%';
FLUSH PRIVILEGES;
```

2. Import the schema:

```bash
mysql -u spacemission_app -p spacemission_db < database/spacemission_db.sql
```

3. Configure environment variables instead of hardcoding service hostnames:

```bash
export SPACE_MISSION_DB_URL="jdbc:mysql://localhost:3306/spacemission_db"
export SPACE_MISSION_DB_USER="spacemission_app"
export SPACE_MISSION_DB_PASSWORD="change_me"
```

The default connection settings are defined in `src/main/java/com/spacemission/config/DatabaseConfig.java`.

## Run the application

From the project root, launch the desktop app:

```bash
mvn exec:java -Dexec.mainClass=com.spacemission.ui.SpaceMissionApp
```

This starts the Swing login screen and then opens the dashboard after successful authentication.

## REST API

The project also includes a Spring Boot REST API alongside the desktop Swing app. It is served from the configured API host and port, for example:

```text
http://space-mission-api:8080/api
```

Start the REST service with:

```bash
mvn spring-boot:run
```

### Default login

```json
{
  "username": "admin",
  "password": "admin"
}
```

The API uses a bearer token returned from `/api/auth/login` and expects the Authorization header on protected routes.

### Postman

Import the Postman collection and environment from the `docs/postman` directory and run the `Auth > Login` request first to populate the `token` variable.

## Desktop app default login

```text
Username: admin
Password: admin
```

## Build and test

Run the test suite:

```bash
mvn test
```

Build the project:

```bash
mvn package
```

## Project structure

```text
space-mission/
├── database/
│   └── spacemission_db.sql
├── src/
│   ├── main/java/
│   │   └── com/spacemission/
│   └── test/java/
├── pom.xml
├── README.md
└── .gitignore
```

## Notes

- The app is designed for a desktop GUI environment.
- In a headless Linux environment, GUI support may need to be configured before launching the Swing window.
- The project follows a professional DAO/service/UI pattern and keeps the business logic separate from persistence and presentation.
