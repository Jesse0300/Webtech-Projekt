-- === Kategorie-Tabelle ===
CREATE TABLE IF NOT EXISTS category (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(120) NOT NULL UNIQUE
    );

-- === Food-Tabelle ===
CREATE TABLE IF NOT EXISTS food (
                                    id BIGSERIAL PRIMARY KEY,
                                    name VARCHAR(200) NOT NULL,
    calories DOUBLE PRECISION,          -- kcal je 100g
    protein  DOUBLE PRECISION NOT NULL DEFAULT 0,
    carbs    DOUBLE PRECISION NOT NULL DEFAULT 0,
    fat      DOUBLE PRECISION NOT NULL DEFAULT 0,
    category_id BIGINT REFERENCES category(id) ON DELETE SET NULL
    );

-- === Checks und Indexe ===
ALTER TABLE food
    ADD CONSTRAINT IF NOT EXISTS chk_food_macros_nonneg
    CHECK (protein >= 0 AND carbs >= 0 AND fat >= 0);

CREATE UNIQUE INDEX IF NOT EXISTS ux_food_name_lower ON food (LOWER(name));