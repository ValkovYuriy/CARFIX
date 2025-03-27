CREATE TABLE IF NOT EXISTS model(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	model_name VARCHAR(30),
	mark_id UUID REFERENCES mark(id)
);