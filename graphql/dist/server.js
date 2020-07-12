"use strict";
exports.__esModule = true;
var apollo_server_1 = require("apollo-server");
var schema_1 = require("./schema");
var context_1 = require("./context");
new apollo_server_1.ApolloServer({ schema: schema_1.schema, context: context_1.createContext }).listen({ port: process.env.LISTEN_PORT, host: '0.0.0.0' }, function () {
    return console.log("\uD83D\uDE80 GraphQL Server ready at: http://localhost:" + process.env.LISTEN_PORT);
});
