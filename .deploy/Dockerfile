FROM amazoncorretto:17-alpine-jdk

ARG TARGET_JAR=/app/build/libs/app.jar

COPY ${TARGET_JAR} /application.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "/application.jar"]