import {enumType, scalarType} from "nexus";
import {Kind} from "graphql";

export const DateScalar = scalarType({
    name: "Date",
    asNexusMethod: "date",
    description: "Date custom scalar type",
    parseValue: (value) => {
        return new Date(value);
    },
    serialize: (value) => {
        let iso = value.toISOString();
        return iso.substr(0, iso.indexOf("T"));
    },
    parseLiteral: (ast) => {
        if (ast.kind === Kind.INT) {
            return new Date(ast.value);
        }
        return null;
    },
});

export const GenderScalar = enumType({
    name: "Gender",
    description: "Gender (M/F/N)",
    members: [
        "M",
        "F",
        "N",
    ]
});
