CREATE TABLE IF NOT EXISTS user_data (
    user_id uuid PRIMARY KEY,
    name text,
    device_identifier text,
    last_status_update timestamp,
    sentiment text,
    stock_avatar text
 );

CREATE TABLE IF NOT EXISTS group_data (
     group_id uuid PRIMARY KEY,
     group_name text,
     members uuid -- TODO array
 );

CREATE TABLE IF NOT EXISTS message_data (
     message_id uuid PRIMARY KEY,
     group_id uuid,
     created_at timestamp,
     sender_user_id uuid,
     recipient_user_id uuid,
     text text
);
