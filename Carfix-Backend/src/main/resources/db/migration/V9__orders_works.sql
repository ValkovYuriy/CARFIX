CREATE TABLE IF NOT EXISTS orders_works(
    order_id UUID REFERENCES "order"(id) ON DELETE CASCADE,
    work_id UUID REFERENCES work(id),
    PRIMARY KEY (order_id,work_id)
)
