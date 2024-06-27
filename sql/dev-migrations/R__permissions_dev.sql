GRANT ALL ON
DATABASE "${database_name}" TO "${content_user}";

GRANT USAGE ON
SCHEMA public TO "${content_user}";

GRANT SELECT
    ,
    INSERT
        ,
        UPDATE
            ,
            DELETE
                ON
                ALL TABLES IN SCHEMA public TO "${content_user}";

GRANT USAGE,
SELECT
    ,
    UPDATE
        ON
        ALL SEQUENCES IN SCHEMA public TO "${content_user}";

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT
    ,
    INSERT
        ,
        UPDATE
            ,
            DELETE
                ON
                TABLES TO "${content_user}";

ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE,
SELECT
    ,
    UPDATE
        ON
        SEQUENCES TO "${content_user}";
