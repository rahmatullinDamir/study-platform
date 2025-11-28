# Stage 1: build
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 1️⃣ Копируем только pom.xml
COPY pom.xml .

# 2️⃣ Загружаем зависимости (кэшируется!)
RUN mvn dependency:go-offline -B

# 3️⃣ Теперь копируем исходники
COPY src ./src

# 4️⃣ Собираем
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
