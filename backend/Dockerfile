FROM maven:3.8.3-openjdk-17 AS backend
WORKDIR /app/backend
COPY ./pom.xml ./
COPY ./src ./src/
RUN mvn package -DskipTests

# Run
FROM openjdk:17-slim
WORKDIR /app
COPY --from=backend /app/backend/target/*.jar ./backend.jar
EXPOSE 8081
ENV DATA_LOCATION /data
ARG LDAP_URI
ENV LDAP_URI $LDAP_URI
CMD ["java", "-jar", "backend.jar"]