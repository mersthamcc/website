import express from 'express';
import { ApolloServer } from 'apollo-server-express'
import { schema } from './schema'
import { createContext } from './context'
import {KeycloakSchemaDirectives, KeycloakTypeDefs} from "keycloak-connect-graphql";

const Keycloak = require('keycloak-connect')
const GRAPHQL_PATH = '/graphql'

const app = express()

const keycloak = new Keycloak({}, {
    "realm": process.env["KEYCLOAK_REALM"],
    "auth-server-url": process.env["KEYCLOAK_URL"],
    "ssl-required": "none",
    "resource": process.env["KEYCLOAK_CLIENT_ID"],
    "use-resource-role-mappings": true,
    "confidential-port": 0,
    "credentials": {
        "secret": process.env["KEYCLOAK_CLIENT_SECRET"]
    }
})
app.use(keycloak.middleware())

app.get('/', function (req, res) {
    res.redirect(GRAPHQL_PATH)
})

const server = new ApolloServer({
    schema,
    typeDefs: [KeycloakTypeDefs],
    schemaDirectives: KeycloakSchemaDirectives,
    playground: (process.env["GRAPHQL_PLAYGROUND"] == "true"),
    context: ({req}) => createContext({req: req}),
})

server.applyMiddleware({app})
app.listen(
    { port: 4000 },
    () =>
        console.log(
            `🚀 Server ready at: http://localhost:4000 ⭐️`,
        ),
)
