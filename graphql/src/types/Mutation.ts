import {intArg, list, nonNull, objectType, stringArg} from "nexus";
import {hasRole} from "keycloak-connect-graphql";
import {Context} from "../context";

export const Mutation = objectType({
    name: "Mutation",
    definition(t) {
        t.field("signupUser", {
            type: "User",
            description: "Create a new user",
            args: {
                externalId: nonNull(stringArg()),
                emailAddress: nonNull(stringArg()),
                familyName: stringArg(),
                givenName: stringArg(),
            },
            resolve: hasRole(["realm:TRUSTED_APPLICATION"])((_: any, args: { externalId: string; emailAddress: string; familyName: string; givenName: string; }, context: Context) => {
                return context.prisma.user.create({
                    data: {
                        externalId: args.externalId,
                        email: args.emailAddress,
                        familyName: args.familyName,
                        givenName: args.givenName,
                        roles: []
                    }
                });
            })
        });

        t.field("updateUserDetails", {
            type: "User",
            description: "Update user details and roles",
            args: {
                id: nonNull(intArg()),
                roles: nonNull(list(nonNull(stringArg()))),
                familyName: stringArg(),
                givenName: stringArg(),
                email: nonNull(stringArg()),
            },
            resolve: hasRole(["realm:TRUSTED_APPLICATION"])((_: any, args: {id: number, roles: string[], familyName: string, givenName: string, email: string}, context: Context) => {
                return context.prisma.user.update({
                    where: { id:  args.id },
                    data: {
                        roles: args.roles,
                        familyName: args.familyName,
                        givenName: args.givenName,
                        email: args.email,
                    },
                });
            })
        });
    }
});