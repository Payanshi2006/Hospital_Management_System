# Hospital Management System

A Java-based Hospital Management System for managing patients, doctors, appointments, and billing.

## Features
- Patient registration and search
- Doctor lookup by ID or specialty
- Appointment scheduling and status updates
- Bill generation and payment handling
- Exception-based validation and domain-driven logic

## Tech Stack
- Java 17+
- OOP concepts
- Modular Java (`module-info.java`)
- Collections and concurrency utilities

## Run the project
```bash
javac --release 17 -d out $(find app enums exceptions model repository services -name "*.java")
java --module-path out -m hospital.management/app.Hospital
```

## Project structure
- `app/` - Console application entry point
- `model/` - Domain models
- `services/` - Business logic
- `repository/` - In-memory repository
- `enums/` - Enum definitions
- `exceptions/` - Custom exception classes
