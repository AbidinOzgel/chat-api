# Real-Time Chat API

This is a real-time chat backend application developed using **Spring Boot**, **WebSocket**, **JWT**, and **MySQL**. The project supports real-time private messaging between authenticated users and is structured with best practices in mind for a robust, scalable backend system.

## 📁 Project Structure

```
chat-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── AbidinOzgel/
│   │   │           ├── config/
│   │   │           ├── controller/
│   │   │           ├── model/
│   │   │           ├── repository/
│   │   │           ├── security/
│   │   │           └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
├── .gitignore
├── pom.xml
└── README.txt
```

## ⚙ Technologies Used

- Java 17
- Spring Boot
- Spring Security with JWT
- WebSocket (STOMP Protocol)
- MySQL
- Maven

## 🔐 Authentication

JWT is used for securing endpoints. All endpoints except for login and register require authentication. Tokens must be sent in the `Authorization` header as a Bearer token.

## 💬 WebSocket Endpoints

- WebSocket handshake endpoint: `/ws`
- STOMP destinations for messaging:
  - `/app/chat.sendMessage`
  - `/user/{username}/queue/messages`

## 🧪 Sample Endpoints

- `POST /api/auth/register`: Register a new user
- `POST /api/auth/login`: Authenticate and receive JWT
- `GET /api/users`: Get list of available users
- WebSocket for sending messages to users

## 📦 Database Configuration

MySQL is used as the database. Configure your local instance accordingly in `application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/chat_api
spring.datasource.username=root
spring.datasource.password=your_password
```

## 🏁 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/AbidinOzgel/chat-api.git
cd chat-api
```

### 2. Configure MySQL and application.properties

Create a database named `chat_api` and set your MySQL credentials in the `application.properties` file.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The server will start on port `8080` by default.

## 📜 License

This project is open-source and available under the MIT License.
