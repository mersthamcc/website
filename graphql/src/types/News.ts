import {objectType} from "nexus";

export const News = objectType({
    name: "News",
    definition(t) {
        t.model.id();
        t.model.title();
        t.model.createdDate();
        t.model.publishDate();
        t.model.body();
        t.model.attributes();
        t.model.comments();
    },
});

export const NewsComment = objectType({
    name: "NewsComment",
    definition(t) {
        t.model.news();
        t.model.author();
        t.model.body();
    }
});

export const NewsAttribute = objectType({
    name: "NewsAttribute",
    definition(t) {
        t.model.news();
        t.model.name();
        t.model.value();
    }
});
