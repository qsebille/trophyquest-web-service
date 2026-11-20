# TrophyQuest Backend

Backend web service for the "TrophyQuest" project. This service manages PlayStation Network (PSN) trophy data, player
statistics, and game information, integrating with IGDB for enhanced game metadata.

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Data JPA** with **Hibernate**
- **PostgreSQL**
- **Liquibase** (Database migrations)
- **AWS SDK** (Lambda integration)
- **Lombok**
- **Testcontainers** (For integration tests)

## 📋 Prerequisites

- **JDK 17** or higher
- **Docker** (recommended for running PostgreSQL and tests)
- **Gradle** (included via `./gradlew`)

## ⚙️ Configuration

The application uses Spring profiles for configuration:

- `default`: Base configuration in `application.yaml`
- `local`: Development configuration in `application-local.yaml` (PostgreSQL on localhost)
- `prod`: Production configuration
- `test`: Configuration for running tests

### Database Setup

By default, the `local` profile expects a PostgreSQL database:

- **URL**: `jdbc:postgresql://localhost:5432/trophyquest?currentSchema=app`
- **Username**: `postgres`
- **Password**: `postgres`

Migrations are handled automatically by Liquibase on startup.

## 🛠️ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-repo/trophyquest-web-service.git
cd trophyquest-web-service
```

### 2. Build the project

```bash
./gradlew build
```

### 3. Run the application

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 🔌 API Endpoints

The API is accessible under `/api`. Key controllers include:

- `/api/home` serves featured games, players and a background image.
- `/api/game/search` and `/api/game/details` serve game search, details, suites, recent players and background images.
- `/api/player/search` and `/api/player/profile` serve player search, profiles, played suites, earned trophies and
  background images.
- `/api/suite/{suiteId}` serves suite details. Its `/players` and `/trophies` routes list players and trophies for the
  suite.
- `/api/association` manages associations between PlayStation suites and IGDB games. All association endpoints require
  the `admin` group.
- `/api/auth/me` returns the current authenticated user, including `isAdmin`, on reads and nickname updates.

## 🧪 Testing

Integration tests use **Testcontainers** to spin up a PostgreSQL instance.
Docker must be running before executing the tests.

```bash
./gradlew test
```

With Colima on macOS, start the runtime and point Testcontainers to its socket:

```bash
colima start
DOCKER_HOST="unix://$HOME/.colima/default/docker.sock" \
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock \
bash gradlew test
```

When running tests from an IDE, set the same `DOCKER_HOST` (using an absolute path)
and `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE` environment variables in the test run configuration.

## 📄 License

This project is licensed under the terms of the MIT license (or specify your license).
