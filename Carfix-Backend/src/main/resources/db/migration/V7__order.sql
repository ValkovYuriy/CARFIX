CREATE TABLE IF NOT EXISTS "order"(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    car_id UUID REFERENCES car(id) NOT NULL,
    user_id UUID REFERENCES "user"(id) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(10) CHECK (status IN ('PENDING','ACCEPTED','REJECTED')),
    description VARCHAR(1000),
    price NUMERIC
)