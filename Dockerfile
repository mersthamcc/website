ARG JAVA_VERSION=16
FROM adoptopenjdk:${JAVA_VERSION}
ARG DEBUG_PORT=8081
ARG DEBUG_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT}"
ARG JAR_FILE=build/libs/*.jar

EXPOSE 8080
EXPOSE ${DEBUG_PORT}
RUN addgroup --system spring
RUN adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY ${JAR_FILE} /app
ENTRYPOINT ["java", "${DEBUG_OPTIONS}", "-jar","frontend.jar"]
