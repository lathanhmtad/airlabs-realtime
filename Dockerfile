# Stage 1: Build JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -ntp -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -ntp -DskipTests package

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Tạo user không phải root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
# Chạy app; có thể truyền thêm JVM opts qua JAVA_OPTS
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
