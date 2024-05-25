ARG JAVA_VERSION=17
ARG GRADLE_VERSION=8.1
FROM gradle:${GRADLE_VERSION}-jdk${JAVA_VERSION} AS builder
ARG COMPONENT

RUN mkdir /app
COPY build.gradle /app
COPY settings.gradle /app
COPY ${COMPONENT}/ /app/${COMPONENT}/
COPY shared/ /app/shared/
WORKDIR /app
RUN gradle :${COMPONENT}:bootJar -x test -x spotlessCheck

ARG JAVA_VERSION=17
FROM eclipse-temurin:${JAVA_VERSION}-jre
ARG COMPONENT
LABEL org.opencontainers.image.source=https://github.com/mersthamcc/website
LABEL org.opencontainers.image.description="Club website ${COMPONENT} component"

EXPOSE 8090
RUN addgroup --system spring
RUN adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY --from=builder /app/${COMPONENT}/build/libs/*.jar /app/
COPY ./docker-entrypoint.sh /app
ENV COMPONENT=${COMPONENT}
ENTRYPOINT ["/app/docker-entrypoint.sh"]
CMD []
