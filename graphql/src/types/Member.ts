import {objectType} from "nexus";

export const Member = objectType({
    name: 'member',
    definition(t) {
        t.model.id()
    },
})