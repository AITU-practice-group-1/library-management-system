# Build Stage
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
RUN apk add --no-cache maven curl
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src/main/resources ./src/main/resources
COPY src ./src
# Copy application.yml explicitly (optional redundancy)
COPY src/main/resources/application.yml ./application.yml
RUN ./mvnw clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy only the built JAR and resources from the builder stage
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/src/main/resources ./src/main/resources

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]