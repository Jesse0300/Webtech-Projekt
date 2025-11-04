-- 1) Mahlzeiten-Tabelle
CREATE TABLE IF NOT EXISTS meal (
                                    id          BIGSERIAL PRIMARY KEY,
                                    name        VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    DEFAULT NOW()
    );

-- 2) Positionen einer Mahlzeit (Meal-Items) – verknüpft Meal und Food
CREATE TABLE IF NOT EXISTS meal_item (
                                         id        BIGSERIAL PRIMARY KEY,
                                         meal_id   BIGINT NOT NULL,
                                         food_id   BIGINT NOT NULL,
                                         grams     DOUBLE PRECISION NOT NULL DEFAULT 0,  -- Menge in g

                                         CONSTRAINT fk_meal_item_meal
                                         FOREIGN KEY (meal_id) REFERENCES meal(id) ON DELETE CASCADE,

    CONSTRAINT fk_meal_item_food
    FOREIGN KEY (food_id) REFERENCES food(id) ON DELETE RESTRICT
    );

-- 3) sinnvolle Indizes
CREATE INDEX IF NOT EXISTS idx_meal_item_meal_id ON meal_item(meal_id);
CREATE INDEX IF NOT EXISTS idx_meal_item_food_id ON meal_item(food_id);

-- 4) Optional: Eindeutigkeit (ein Food nicht doppelt in derselben Meal)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public'
          AND indexname = 'ux_meal_item_unique'
    ) THEN
CREATE UNIQUE INDEX ux_meal_item_unique
    ON meal_item(meal_id, food_id);
END IF;
END $$;