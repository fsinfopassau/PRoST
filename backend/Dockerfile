FROM docker.io/library/maven:3.8.3-openjdk-17 AS backend
WORKDIR /app/backend
COPY ./pom.xml ./
COPY ./src ./src/
RUN mvn package -DskipTests

# Run
FROM docker.io/library/openjdk:17-slim
WORKDIR /app
COPY --from=backend /app/backend/target/*.jar ./backend.jar
EXPOSE 8081
ENV DATA_LOCATION /data
CMD ["java", "-jar", "backend.jar"]