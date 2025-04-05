CREATE TABLE IF NOT EXISTS car(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    gov_number VARCHAR(10) UNIQUE,
    vin_number VARCHAR(17) UNIQUE,
	model_id UUID REFERENCES model(id)
)