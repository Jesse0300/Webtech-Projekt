-- Nur nötig, wenn du bereits eine Tabelle "food" hattest
ALTER TABLE food
    ADD COLUMN IF NOT EXISTS protein DOUBLE PRECISION NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS carbs   DOUBLE PRECISION NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS fat     DOUBLE PRECISION NOT NULL DEFAULT 0;

ALTER TABLE food
    ADD CONSTRAINT IF NOT EXISTS chk_food_macros_nonneg
    CHECK (protein >= 0 AND carbs >= 0 AND fat >= 0);rotein_per_100g,0) + COALESCE(fat_per_100g,0) + COALESCE(carbs_per_100g,0)) <= 100);