ARG JAVA_VERSION=11
ARG GRADLE_VERSION=7
FROM gradle:${GRADLE_VERSION}-jdk${JAVA_VERSION} AS graphql-builder
RUN mkdir /app
COPY build.gradle /app
COPY settings.gradle /app
COPY src /app/src
WORKDIR /app
RUN gradle build -x test

ARG JAVA_VERSION=11
FROM adoptopenjdk:${JAVA_VERSION}
ARG JAR_FILE=build/libs/*.jar

EXPOSE 8090
RUN addgroup --system spring
RUN adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY --from=graphql-builder /app/${JAR_FILE} /app
COPY docker-entrypoint.sh /app
ENTRYPOINT ["/app/docker-entrypoint.sh"]
