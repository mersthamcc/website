FROM node:14.4.0

ENV DATABASE_URL=postgresql://johndoe:mypassword@localhost:5432/mydb?schema=public
ENV LISTEN_PORT=4000

WORKDIR /app

COPY *.json /app/
COPY src/*.ts /app/src/
COPY prisma/schema.prisma /app/prisma/

RUN npm install
#RUN npm ci --only=production

ENTRYPOINT npm run
