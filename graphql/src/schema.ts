import {makeSchema} from "nexus";
import {nexusPrisma} from "nexus-plugin-prisma";
import {Context} from "./context";

import * as types from "./types";

export const schema =
    makeSchema({
        types,
        plugins: [
            nexusPrisma({
                experimentalCRUD: false,
            }),
        ],
        outputs: {
            schema: __dirname + "/../schema.graphql",
            typegen: __dirname + "/generated/nexus.ts",
        },
        contextType: {
            module: require.resolve("./context"),
            export: "Context",
        },
        sourceTypes: {
            modules: [
                {
                    module: "@prisma/client",
                    alias: "prisma",
                },
            ],
        },
    });
