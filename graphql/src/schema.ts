import {makeSchema, objectType} from 'nexus'
import {nexusPrisma} from 'nexus-plugin-prisma'
import {Context} from "./context";

import * as types from './types'
import {applyMiddleware} from "graphql-middleware";

export const schema = // applyMiddleware(
    makeSchema({
        types: types,
        plugins: [
            nexusPrisma({
                experimentalCRUD: true,
            }),
        ],
        outputs: {
            schema: __dirname + '/../schema.graphql',
            typegen: __dirname + '/generated/nexus.ts',
        },
        contextType: {
            module: require.resolve('./context'),
            export: 'Context',
        },
        sourceTypes: {
            modules: [
                {
                    module: '@prisma/client',
                    alias: 'prisma',
                },
            ],
        },
    })
// )