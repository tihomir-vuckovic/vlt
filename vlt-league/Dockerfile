FROM gradle:8.14.3-jdk21 AS build
WORKDIR /workspace
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle --no-daemon quarkusBuild

FROM eclipse-temurin:21-jre
WORKDIR /work
COPY --from=build /workspace/build/quarkus-app/ ./
EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]