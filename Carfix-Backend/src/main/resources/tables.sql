CREATE TABLE IF NOT EXISTS "user"(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(128) ,
    phone_number VARCHAR(12),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(15) NOT NULL
);
CREATE TABLE IF NOT EXISTS review(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	review_content VARCHAR(1000),
    rating INT,
	review_date DATE,
	user_id UUID REFERENCES "user"(id)
);
CREATE TABLE IF NOT EXISTS mark(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	mark_name VARCHAR(30)
);
CREATE TABLE IF NOT EXISTS model(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	model_name VARCHAR(30),
	mark_id UUID REFERENCES mark(id)
);
CREATE TABLE IF NOT EXISTS car(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    gov_number VARCHAR(10) UNIQUE,
    vin_number VARCHAR(17) UNIQUE,
	model_id UUID REFERENCES model(id)
);
CREATE TABLE IF NOT EXISTS work(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR,
    description VARCHAR,
    image_url VARCHAR
);
CREATE TABLE IF NOT EXISTS "order"(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    car_id UUID REFERENCES car(id) NOT NULL,
    user_id UUID REFERENCES "user"(id) NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(10) CHECK (status IN ('PENDING','ACCEPTED','REJECTED')),
    description VARCHAR(1000),
    price NUMERIC
);
CREATE TABLE IF NOT EXISTS work_price(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    price NUMERIC NOT NULL ,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    work_id UUID REFERENCES work(id)
);
CREATE TABLE IF NOT EXISTS orders_works(
    order_id UUID REFERENCES "order"(id),
    work_id UUID REFERENCES work(id),
    PRIMARY KEY (order_id,work_id)
);
