import {objectType} from "nexus";

export const News = objectType({
    name: 'news',
    definition(t) {
        t.model.id()
        t.model.title()
        t.model.created_date()
        t.model.publish_date()
        t.model.body()
        t.model.news_comment()
    },
})

export const Comment = objectType({
    name: 'news_comment',
    definition(t) {
        t.model.author()
        t.model.body()
    }
})