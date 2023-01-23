FROM maven:3.8.6-openjdk-11-slim AS builder
WORKDIR /project
ADD . /project
RUN mvn --batch-mode -Dmaven.test.skip=true --no-transfer-progress package

FROM gcr.io/distroless/java11-debian11:nonroot
ARG jarName
ENV jarName=$jarName
COPY --from=builder --chown="nonroot:nonroot" /project/target/$jarName /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar" ]
