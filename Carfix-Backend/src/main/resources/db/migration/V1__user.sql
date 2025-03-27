CREATE TABLE IF NOT EXISTS "user"(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(128) ,
    phone_number VARCHAR(12),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(15) NOT NULL
)