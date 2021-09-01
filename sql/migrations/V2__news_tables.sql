CREATE
    TABLE
        news(
            id SERIAL NOT NULL,
            created_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
            publish_date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
            title VARCHAR(255) NOT NULL,
            body TEXT NOT NULL,
            PRIMARY KEY(id)
        );

CREATE
    TABLE
        news_attribute(
            id SERIAL NOT NULL,
            news_id INT NOT NULL,
            name VARCHAR(64) NOT NULL,
            value VARCHAR(1024) DEFAULT NULL,
            PRIMARY KEY(id)
        );

CREATE
    INDEX IDX_NEWS_ATTRIBUTE_NEWS_ID ON
    news_attribute(news_id);

ALTER TABLE
    news_attribute ADD CONSTRAINT FK_NEWS_ATTRIBUTE_NEWS_ID FOREIGN KEY(news_id) REFERENCES news(id) NOT DEFERRABLE INITIALLY IMMEDIATE;

CREATE
    TABLE
        news_comment(
            id SERIAL NOT NULL,
            news_id INT NOT NULL,
            author VARCHAR(255) DEFAULT NULL,
            body TEXT NOT NULL,
            PRIMARY KEY(id)
        );

CREATE
    INDEX IDX_NEWS_COMMENT_NEWS_ID ON
    news_comment(news_id);

ALTER TABLE
    news_comment ADD CONSTRAINT FK_NEWS_COMMENT_NEWS_ID FOREIGN KEY(news_id) REFERENCES news(id) NOT DEFERRABLE INITIALLY IMMEDIATE;
