FROM adoptopenjdk:16
EXPOSE 8080
RUN addgroup --system spring
RUN adduser --system spring --ingroup spring
USER spring:spring
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app
COPY ${JAR_FILE} /app
ENTRYPOINT ["java","-jar","frontend.jar"]
