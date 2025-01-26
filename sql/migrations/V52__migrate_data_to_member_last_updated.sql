UPDATE member
   SET last_updated = (SELECT added_date FROM member_subscription WHERE member_id = id ORDER BY added_date DESC LIMIT 1)
 WHERE last_updated IS NULL;