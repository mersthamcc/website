FROM gradle:6.5-jdk11 AS extension-builder
COPY keycloak-sms2fa /src
WORKDIR /src
RUN gradle --no-daemon build jar

FROM quay.io/keycloak/keycloak:11.0.3
COPY keycloak-theme /opt/jboss/keycloak/themes/mcc
COPY --from=extension-builder /src/build/libs/*.jar /opt/jboss/keycloak/providers/