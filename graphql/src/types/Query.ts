import {objectType} from "nexus";
import {auth, hasRole} from "keycloak-connect-graphql";
import {Context} from "../context";

export const Query = objectType({
    name: 'Query',
    definition(t) {
        t.list.field('feed', {
            type: 'news',
            resolve: auth((_: any, args: any, ctx: Context) => {
                return ctx.prisma.news.findMany({
                    orderBy: { publish_date: "desc"},
                    take: 10
                })
            }),
        })
        t.list.field('members', {
            type: 'member',
            resolve: hasRole(['realm:ROLE_MEMBERSHIP'])((_: any, args: any, ctx: Context) => {
                return ctx.prisma.member.findMany({
                    orderBy: {
                        family_name: "asc"
                    },

                    take: 10
                })
            })
        })
    },
})
