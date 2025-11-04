CREATE TABLE IF NOT EXISTS category (
                                        id   BIGSERIAL PRIMARY KEY,     -- PRIMARY KEY impliziert bereits NOT NULL
                                        name VARCHAR(255) NOT NULL UNIQUE
    );

-- Lebensmittel
CREATE TABLE IF NOT EXISTS food (
                                    id          BIGSERIAL PRIMARY KEY,
                                    name        VARCHAR(255) NOT NULL,
    calories    DOUBLE PRECISION,   -- entspricht Java Double
    protein     DOUBLE PRECISION,
    carbs       DOUBLE PRECISION,
    fat         DOUBLE PRECISION,
    category_id BIGINT,
    CONSTRAINT fk_food_category
    FOREIGN KEY (category_id)
    REFERENCES category(id)
    ON DELETE SET NULL
    );

-- sinnvolle Indizes
CREATE INDEX IF NOT EXISTS idx_food_name         ON food(name);
CREATE INDEX IF NOT EXISTS idx_food_category_id  ON food(category_id);