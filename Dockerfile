ARG JAVA_VERSION=21
FROM amazoncorretto:${JAVA_VERSION}-alpine
ARG COMPONENT
LABEL org.opencontainers.image.source=https://github.com/mersthamcc/website
LABEL org.opencontainers.image.description="Club website ${COMPONENT} component"
RUN apk add --no-cache bash curl

RUN addgroup --system spring
RUN adduser --system spring --ingroup spring
USER spring:spring
WORKDIR /app
COPY ./${COMPONENT}/build/libs/*.jar /app/
COPY ./docker-entrypoint.sh /app
ENV COMPONENT=${COMPONENT}
ENTRYPOINT ["/app/docker-entrypoint.sh"]
CMD []
