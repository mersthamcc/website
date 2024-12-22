ARG JAVA_VERSION=17
FROM eclipse-temurin:${JAVA_VERSION}-jre-focal
ARG COMPONENT
LABEL org.opencontainers.image.source=https://github.com/mersthamcc/website
LABEL org.opencontainers.image.description="Club website ${COMPONENT} component"

EXPOSE 8090
RUN addgroup --system spring
RUN adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY ./${COMPONENT}/build/libs/*.jar /app/
COPY ./docker-entrypoint.sh /app
ENV COMPONENT=${COMPONENT}
ENTRYPOINT ["/app/docker-entrypoint.sh"]
CMD []
