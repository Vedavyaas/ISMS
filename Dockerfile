# ── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy dependency manifests first for better layer caching
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Download all dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source and build the fat JAR, skipping tests
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Run ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy the fat JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render injects PORT at runtime; fall back to 9090 locally
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
