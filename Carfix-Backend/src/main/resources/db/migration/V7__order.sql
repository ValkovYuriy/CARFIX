CREATE TABLE IF NOT EXISTS "order"(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    car_id UUID REFERENCES car(id),
    order_date TIMESTAMP,
    price NUMERIC
)