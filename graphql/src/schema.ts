import { nexusPrismaPlugin } from 'nexus-prisma'
import { intArg, makeSchema, objectType, stringArg } from '@nexus/schema'

const News = objectType({
  name: 'news',
  definition(t) {
    t.model.id()
    t.model.title()
    t.model.body()
    t.model.publish_date()
  },
})

const Query = objectType({
  name: 'Query',
  definition(t) {
    t.crud.news
    t.list.field('feed', {
      type: 'news',
      resolve: (_, args, ctx) => {
        return ctx.prisma.news.findMany({
          orderBy: { publish_date: "desc" },
          take: 10
        })
      },
    })
  },
})

export const schema = makeSchema({
  types: [Query, News],
  plugins: [nexusPrismaPlugin()],
  outputs: {
    schema: __dirname + '/../schema.graphql',
    typegen: __dirname + '/generated/nexus.ts',
  },
  typegenAutoConfig: {
    contextType: 'Context.Context',
    sources: [
      {
        source: '@prisma/client',
        alias: 'prisma',
      },
      {
        source: require.resolve('./context'),
        alias: 'Context',
      },
    ],
  },
})
