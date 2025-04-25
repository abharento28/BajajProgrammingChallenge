# MutualFollowersApplication

MutualFollowersApplication is a Spring Boot application designed to manage and analyze mutual followers. This project is built using Java and Spring Boot, providing a robust and scalable backend solution.

---

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

---

## Features
- Analyze mutual followers between users.
- RESTful APIs for managing followers.
- Scalable and modular architecture.
- Easy to extend and integrate with other systems.

---

## Technologies Used
- **Java**: Programming language.
- **Spring Boot**: Framework for building the application.
- **Maven**: Dependency management and build tool.
- **H2 Database** (or any other database): For data persistence.
- **Spring Data JPA**: For database interaction.
- **Spring Web**: For building RESTful APIs.

---

## Getting Started

### Prerequisites
Ensure you have the following installed on your system:
- **Java 17** or later
- **Maven 3.8+**
- **Git** (optional, for cloning the repository)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/abharento28/BajajProgrammingChallenge.git
   cd BajajProgrammingChallenge


2. Build the project using Maven:
   ```bash
   mvn clean install

3. Run the application:
   ```bash
   mvn spring-boot:run

4. Access the application at:
   ```bash
   http://localhost:8080/

### Usage
API Endpoints:
The application exposes RESTful APIs for managing followers. 

Example endpoints:
GET /followers: Retrieve all followers.

POST /followers: Add a new follower.

GET /mutual-followers: Retrieve mutual followers between users.

Database:
By default, the application uses an in-memory H2 database. You can configure a different database in the application.properties file.

