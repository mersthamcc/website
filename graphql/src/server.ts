import { ApolloServer } from 'apollo-server'
import { schema } from './schema'
import { createContext } from './context'

new ApolloServer({ schema, context: createContext }).listen(
  { port:  process.env.LISTEN_PORT, host: '0.0.0.0' },
  () =>
    console.log(
      `🚀 GraphQL Server ready at: http://localhost:${process.env.LISTEN_PORT}`,
    ),
)
