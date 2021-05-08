ARG JAVA_VERSION=16
FROM adoptopenjdk:${JAVA_VERSION}
ARG DEBUG_PORT=8081
ARG JAR_FILE=build/libs/*.jar

EXPOSE 8080
EXPOSE ${DEBUG_PORT}
ENV DEBUG_PORT=${DEBUG_PORT}

RUN addgroup --system spring
RUN adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY ${JAR_FILE} /app
COPY docker-entrypoint.sh /app
ENTRYPOINT ["/app/docker-entrypoint.sh"]
