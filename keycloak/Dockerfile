FROM gradle:jdk11 AS extension-builder
COPY sms-2fa /src
WORKDIR /src
RUN gradle build jar


FROM quay.io/keycloak/keycloak:11.0.3
COPY theme /opt/jboss/keycloak/themes/mcc
COPY --from=extension-builder /src/build/libs/*.jar /opt/jboss/keycloak/providers/