import {arg, intArg, list, nonNull, objectType, stringArg} from "nexus";
import {auth, hasRole} from "keycloak-connect-graphql";
import {Context} from "../context";
import {encrypt} from "../helpers/Encryption";
const openpgp = require('openpgp');

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
            resolve: hasRole(["realm:TRUSTED_APPLICATION"])((_: any, args: { id: number, roles: string[], familyName: string, givenName: string, email: string }, context: Context) => {
                return context.prisma.user.update({
                    where: {id: args.id},
                    data: {
                        roles: args.roles,
                        familyName: args.familyName,
                        givenName: args.givenName,
                        email: args.email,
                    },
                });
            })
        });

        t.field("createMember", {
            type: "Member",
            description: "Add a new member to the register",
            args: {
                data: nonNull(arg({ type: "MemberInput"})),
            },
            resolve: auth(async (_:any, args: { id: number, data: any }, context: Context ) => {
                const now = new Date();
                return context.prisma.member.create({
                   data: {
                       familyName: args.data.familyName,
                       givenName: args.data.givenName,
                       gender: args.data.gender,
                       registrationDate: now,
                       owner: {
                           connect: {
                               // @ts-ignore
                               email: context.kauth.accessToken.content.email
                           }
                       },
                       attributes: {
                           create: await Promise.all(args.data.attributes.map(async (attr: { key: string; value: any; }) => {
                               const encrypted = await encrypt(JSON.stringify(attr.value));
                               return({
                                   definition: {
                                       connect: {
                                           key: "school"
                                       }
                                   },
                                   updatedDate: now,
                                   createdDate: now,
                                   value: ({
                                       encrypted: encrypted
                                   })
                               })
                           }))
                       }
                   }
                });
            })
        });

        t.field("updateMember", {
            type: "Member",
            description: "Update an existing member",
            args: {
                id: nonNull(intArg()),
                data: nonNull(arg({ type: "MemberInput" }))
            },
            resolve: auth(async (_:any, args: { id: number, data: any }, context: Context ) => {
                const now = new Date();

                return context.prisma.member.update({
                    where: {
                        id: args.id,
                    },
                    data: {
                        familyName: args.data.familyName,
                        givenName: args.data.givenName,
                        gender: args.data.gender,
                        attributes: {
                            upsert: await Promise.all(args.data.attributes.map(async (attr: { key: string; value: any; }) => {
                                const encrypted = await encrypt(JSON.stringify(attr.value));
                                const attribute = await context.prisma.attributeDefinition.findUnique({
                                    where: {
                                        key: attr.key
                                    }
                                });
                                return ({
                                    where: {
                                        memberId_attributeId: {
                                            memberId: args.id,
                                            attributeId: attribute ? attribute?.id : 0
                                        }
                                    },
                                    update: {
                                        value: ({
                                            encrypted: encrypted
                                        }),
                                        updatedDate: now
                                    },
                                    create: {
                                        value: ({
                                            encrypted: encrypted
                                        }),
                                        createdDate: now,
                                        updatedDate: now,
                                        definition: {
                                            connect: {
                                                key: attr.key
                                            }
                                        }
                                    }
                                });
                            }))
                        }
                    }
                });
            })
        });
    }
});