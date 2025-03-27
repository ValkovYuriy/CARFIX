CREATE TABLE IF NOT EXISTS mark(
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
	mark_name VARCHAR(30)
);
