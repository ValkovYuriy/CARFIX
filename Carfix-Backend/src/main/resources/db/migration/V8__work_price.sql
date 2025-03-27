CREATE TABLE IF NOT EXISTS work_price(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    price NUMERIC,
    date TIMESTAMP,
    work_id UUID REFERENCES work(id)
)