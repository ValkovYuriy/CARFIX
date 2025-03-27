CREATE TABLE IF NOT EXISTS review(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	review_content VARCHAR(1000),
    rating INT,
	review_date DATE,
	user_id UUID REFERENCES "user"(id)
);
