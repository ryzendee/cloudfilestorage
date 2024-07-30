FROM gradle:8.9.0-jdk21-alpine AS build
WORKDIR /app
COPY src /app/src
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle
COPY gradle /app/gradle
COPY gradlew /app/gradlew
RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
CMD ["--spring.profiles.active=prod"]
ENTRYPOINT ["java", "-jar", "app.jar"]
