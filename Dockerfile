# ====== STAGE 1: build ======
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ====== STAGE 2: run ======
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Render passa la PORT come env var (ok tenerla)
ENV PORT=8080
EXPOSE 8080

# ✅ JVM options (Render / Alpine): forza IPv4 + TLS 1.2
ENV JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false -Djdk.tls.client.protocols=TLSv1.2"

ENTRYPOINT ["java","-jar","app.jar"]
