# 🚀 Production-Grade 3-Tier Microservices Deployment with Docker Compose
An end-to-end containerized 3-Tier Web Application deployed on AWS EC2 using Docker Compose. Built with microservices architecture principles, featuring dynamic UI interactions, secure database connectivity, and isolated custom networks.

---

## 🏗️ Architecture & Flow Diagram

```text
┌─────────────────────────────────────────────────────────────────────────┐
│                              AWS EC2 INSTANCE                           │
│                                                                         │
│  ┌───────────────────────┐   Port 80    ┌────────────────────────────┐  │
│  │   Frontend Service    │ ───────────> │ Apache Web Server Container│  │
│  │ (HTML5/CSS3 Glass UI) │              │      (httpd:alpine)        │  │
│  └───────────────────────┘              └────────────────────────────┘  │
│                                                       │                 │
│                                               User Form Submission      │
│                                                       │                 │
│  ┌───────────────────────┐  Port 8080   ┌────────────────────────────┐  │
│  │    Backend Service    │ ───────────> │    Java REST Server (JDK)  │  │
│  │   (REST APIs + CORS)  │              │ (eclipse-temurin:17-jdk)   │  │
│  └───────────────────────┘              └────────────────────────────┘  │
│                                                       │                 │
│                                              JDBC Connection (Port 3306)│
│                                                       ▼                 │
│  ┌───────────────────────┐              ┌────────────────────────────┐  │
│  │   Database Service    │ ───────────> │     MariaDB Container      │  │
│  │(Relational Data Store)│              │      (mariadb:latest)      │  │
│  └───────────────────────┘              └────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘



----------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------




📂 Project Directory Structure

.
├── backend/
│   ├── App.java            # Java REST API with JDBC MySQL Connector
│   └── Dockerfile          # Builds Java runtime container & compiles source
├── frontend/
│   └── index.html          # Dynamic Web Form with Glassmorphism CSS & Fetch API
├── docker-compose.yml      # Multi-container orchestration specification
├── .gitignore              # Pre-configured git exclusion rules
└── README.md               # Architecture documentation & deployment guide


-----------------------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------------------



Note on Configuration: To keep the backend microservice ultra-lightweight and dependency-free (avoiding heavy Spring Boot overheads), database connection properties (DB_URL, DB_USER, DB_PASS) are managed directly via core Java JDBC constants and Docker internal DNS resolution (db:3306).



⚡ Key Features
   - Microservices Isolation: Each service runs in its own lightweight Linux container connected via Docker's default bridge network.

   - Service Discovery: Backend connects to the database via hostname resolution (jdbc:mysql://db:3306/student_db) without hardcoding IP addresses.

   - Cross-Origin Resource Sharing (CORS): Backend features explicit CORS handling to accept requests securely across different ports.

   - Responsive Dark Glassmorphism UI: Clean user interface with dynamic hostname resolution (window.location.hostname) preventing hardcoded endpoint dependencies.

   - Data Privacy: Personal submission receipt card renders instantly upon successful DB insertion without exposing global user records.
