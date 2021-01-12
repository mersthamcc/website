import {objectType} from "nexus";

export const Member = objectType({
    name: "Member",
    definition(t) {
        t.model.id();
        t.model.givenName();
        t.model.familyName();
        t.model.gender();
        t.model.dob();
        t.model.registrationDate();
        t.model.attributes();
    },
});

export const MemberAttribute = objectType({
    name: "MemberAttribute",
    definition(t) {
        t.model.id();
        t.model.createdDate();
        t.model.value();
        t.model.definition();
    },
});

export const AttributeDefinition = objectType({
    name: "AttributeDefinition",
    definition(t) {
        t.model.id();
        t.model.key();
        t.model.type();
    }
})