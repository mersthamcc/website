import {objectType} from "nexus";

export const User = objectType({
    name: "User",
    definition(t) {
        t.model.id();
        t.model.email();
        t.model.externalId();
        t.model.familyName();
        t.model.givenName();
        t.model.roles();
        t.model.members();
    },
});
