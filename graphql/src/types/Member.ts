import {inputObjectType, objectType} from "nexus";
import {Context} from "../context";
import {GraphQLResolveInfo} from "graphql";
import {decrypt} from "../helpers/Encryption";

export const Member = objectType({
    name: "Member",
    definition(t) {
        t.model.id();
        t.model.registrationDate();
        t.model.attributes();
        t.model.owner();
    }
});

export const MemberAttribute = objectType({
    name: "MemberAttribute",
    async definition(t) {
        t.model.member();
        t.model.definition();
        t.model.createdDate();
        t.model.updatedDate();
        t.model.value({
            resolve: async (root: any, args: any, ctx: Context, info: GraphQLResolveInfo, originalResolver: any) => {
                const { encrypted: data } = await originalResolver(root, args, ctx, info);
                const decrypted = await decrypt(data);
                return JSON.parse(decrypted);
            }
        });
    },
});

export const MemberCategory = objectType({
   name: "MemberCategory",
   description: "",
   definition(t) {
       t.model.key();
       t.model.registrationCode();
       t.model.form();
       t.model.pricelistItem({
           filtering: true,
           ordering: true,
       });
   }
});

export const AttributeDefinition = objectType({
    name: "AttributeDefinition",
    definition(t) {
        t.model.key();
        t.model.type();
        t.model.choices();
    }
});

export const MemberFormSection = objectType({
    name: "MemberFormSection",
    description: "",
    definition(t) {
        t.model.key();
        t.model.attribute();
    }
});

export const MemberCategoryFormSection = objectType({
    name: "MemberCategoryFormSection",
    definition(t) {
        t.model.sortOrder();
        t.model.section();
        t.model.category();
    }
});

export const MemberFormSectionAttribute = objectType({
   name: "MemberFormSectionAttribute",
   definition(t) {
       t.model.sortOrder();
       t.model.mandatory();
       t.model.definition();
       t.model.section();
   }
});

export const PricelistItem = objectType({
    name: "PricelistItem",
    definition(t) {
        t.model.id();
        t.model.description();
        t.model.minAge();
        t.model.maxAge();
        t.model.includesMatchFees();
        t.model.memberCategory();
        t.model.pricelist({
            filtering: true,
            ordering: true,
        });
        t.field("currentPrice", {
            type: "Float",
            description: "The current price of the item",
            resolve: async (root: any, args: any, ctx: Context) => {
                let now = new Date();
                let result = await ctx.prisma.priceList.findFirst({
                    where: {
                        pricelistItemId: root.id,
                        dateFrom: {
                            lte: now
                        },
                        dateTo: {
                            gte: now
                        }
                    },
                    select: {
                        price: true
                    }
                });
                return result?.price == null ? 0.00 : result?.price;
            }
        })
    }
});

export const Pricelist = objectType({
    name: "PriceList",
    definition(t) {
        t.model.pricelistItem();
        t.date("dateFrom");
        t.date("dateTo");
        t.model.price();
    }
});

export const MemberInput = inputObjectType({
    name: "MemberInput",
    description: "A definition of a member",
    definition(t) {
        t.field("givenName", { type: "String" });
        t.field("familyName", { type: "String" });
        t.field("gender", { type: "Gender" });
        t.field("registrationDate", { type: "DateTime" });
        t.field("category", { type: "String" });
        t.list.field("attributes", { type: "AttributeInput" });
    }
});

export const AttributeInput = inputObjectType({
    name: "AttributeInput",
    description: "",
    definition(t) {
        t.field("key", { type: "String" });
        t.field("value", { type: "Json" });
    }
});