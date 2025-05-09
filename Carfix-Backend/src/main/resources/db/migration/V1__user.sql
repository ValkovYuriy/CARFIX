CREATE TABLE IF NOT EXISTS "user"(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR UNIQUE NOT NULL,
    password VARCHAR(128) ,
    phone_number VARCHAR ,
    first_name VARCHAR ,
    last_name VARCHAR ,
    role VARCHAR(15) NOT NULL
)