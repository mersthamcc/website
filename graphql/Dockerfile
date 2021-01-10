FROM node:14.4.0

ENV DATABASE_URL="postgresql://johndoe:mypassword@localhost:5432/mydb?schema=public"
ENV LISTEN_PORT=4000
ENV PLAYGROUND_ENABLED=true
ENV KEYCLOAK_URL="http://localhost:8080"
ENV KEYCLOAK_REALM="master"

WORKDIR /app

COPY *.json /app/
COPY src/ /app/src/
COPY prisma/schema.prisma /app/prisma/

RUN npm install
RUN npm run clean && npm run build

ENTRYPOINT npm run start
