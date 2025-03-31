CREATE TABLE IF NOT EXISTS car(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    gov_number VARCHAR(10) UNIQUE,
    vin_number VARCHAR(17) UNIQUE,
	year_of_release INT CHECK ( year_of_release > 1900 and year_of_release < 2026),
	model_id UUID REFERENCES model(id),
	user_id UUID REFERENCES "user"(id)
)