CREATE
    UNIQUE INDEX IDX_UNIQUE_NEWS_ATTRIBUTE_NEWS_ID_NAME ON
    news_attribute(
        news_id,
        name
    );