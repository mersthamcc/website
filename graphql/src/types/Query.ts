import {nonNull, objectType, stringArg} from "nexus";
import {auth, hasRole} from "keycloak-connect-graphql";
import {Context} from "../context";

export const Query = objectType({
    name: "Query",
    definition(t) {

        t.list.field("feed", {
            type: "News",
            resolve: auth((_: any, args: {}, context: Context) => {
                return context.prisma.news.findMany({
                    orderBy: {publishDate: "desc"},
                    take: 10
                });
            }),
        });

        t.list.field("members", {
            type: "Member",
            resolve: hasRole(["realm:ROLE_MEMBERSHIP"])((_: any, args: {}, context: Context) => {
                return context.prisma.member.findMany();
            })
        });

        t.field("userByEmail", {
            type: "User",
            args: {
                emailAddress: nonNull(stringArg())
            },
            resolve: hasRole(["realm:TRUSTED_APPLICATION"])((_: any, args: { emailAddress: string }, context: Context) => {
                return context.prisma.user.findFirst({
                    where: {
                        email: {
                            equals: args.emailAddress
                        }
                    }
                });
            })
        });

        t.field("userByExternalId", {
            type: "User",
            args: {
                externalId: nonNull(stringArg())
            },
            resolve: hasRole(["realm:TRUSTED_APPLICATION"])((_: any, args: { externalId: string }, context: Context) => {
                return context.prisma.user.findFirst({
                    where: {
                        externalId: {
                            equals: args.externalId
                        }
                    }
                });
            })
        });

        t.list.field("membershipCategories", {
            type: "MemberCategory",

            resolve: auth((_: any, args: any, context: Context) => {
                return context.prisma.memberCategory.findMany({
                    orderBy: {
                        key: "asc"
                    },
                    where: args.where || undefined
                });
            })
        });

        t.field("me", {
            type: "User",
            resolve: auth((_: any, args: any, context: Context) => {
                return context.prisma.user.findUnique({
                    where: {
                        // @ts-ignore
                        email: context.kauth.accessToken.content.email
                    }
                })
            })
        });
    },
});
