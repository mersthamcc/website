"use strict";
exports.__esModule = true;
exports.schema = void 0;
var nexus_prisma_1 = require("nexus-prisma");
var schema_1 = require("@nexus/schema");
var News = schema_1.objectType({
    name: 'news',
    definition: function (t) {
        t.model.id();
        t.model.title();
        t.model.body();
        t.model.publish_date();
    }
});
var Query = schema_1.objectType({
    name: 'Query',
    definition: function (t) {
        t.crud.news;
        t.list.field('feed', {
            type: 'news',
            resolve: function (_, args, ctx) {
                return ctx.prisma.news.findMany({
                    orderBy: { publish_date: "desc" },
                    take: 10
                });
            }
        });
    }
});
exports.schema = schema_1.makeSchema({
    types: [Query, News],
    plugins: [nexus_prisma_1.nexusPrismaPlugin()],
    outputs: {
        schema: __dirname + '/../schema.graphql',
        typegen: __dirname + '/generated/nexus.ts'
    },
    typegenAutoConfig: {
        contextType: 'Context.Context',
        sources: [
            {
                source: '@prisma/client',
                alias: 'prisma'
            },
            {
                source: require.resolve('./context'),
                alias: 'Context'
            },
        ]
    }
});
