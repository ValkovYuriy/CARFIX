CREATE TABLE IF NOT EXISTS work(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR,
    description VARCHAR,
    image bytea
)