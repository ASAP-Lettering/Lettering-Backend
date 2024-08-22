FROM amazoncorretto:17-alpine-jdk

ARG TARGET_JAR=/api/build/libs/api.jar

COPY ${TARGET_JAR} /app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "/app.jar"]