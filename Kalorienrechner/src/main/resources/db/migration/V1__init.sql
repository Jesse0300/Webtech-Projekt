CREATE TABLE IF NOT EXISTS category (
                                        id   BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL UNIQUE
    );

-- Lebensmittel
CREATE TABLE IF NOT EXISTS food (
                                    id          BIGSERIAL PRIMARY KEY,
                                    name        VARCHAR(200) NOT NULL,
    calories    DOUBLE PRECISION,
    protein     DOUBLE PRECISION NOT NULL DEFAULT 0,
    carbs       DOUBLE PRECISION NOT NULL DEFAULT 0,
    fat         DOUBLE PRECISION NOT NULL DEFAULT 0,
    category_id BIGINT,
    CONSTRAINT fk_food_category
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
    );
CREATE INDEX IF NOT EXISTS idx_food_name ON food(name);
CREATE INDEX IF NOT EXISTS idx_food_category_id ON food(category_id);

-- Meals
CREATE TABLE IF NOT EXISTS meal (
                                    id         BIGSERIAL PRIMARY KEY,
                                    name       VARCHAR(255) NOT NULL,
    date_time  TIMESTAMP NOT NULL DEFAULT NOW(),
    meal_type  VARCHAR(50)    -- falls @Enumerated(EnumType.STRING)
    );

-- Meal-Items (Zuordnung Meal ↔ Food mit Menge in g)
CREATE TABLE IF NOT EXISTS meal_item (
                                         id      BIGSERIAL PRIMARY KEY,
                                         meal_id BIGINT NOT NULL,
                                         food_id BIGINT NOT NULL,
                                         grams   DOUBLE PRECISION NOT NULL DEFAULT 0,
                                         CONSTRAINT fk_meal_item_meal
                                         FOREIGN KEY (meal_id) REFERENCES meal(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_item_food
    FOREIGN KEY (food_id) REFERENCES food(id) ON DELETE RESTRICT
    );
CREATE INDEX IF NOT EXISTS idx_meal_item_meal_id ON meal_item(meal_id);
CREATE INDEX IF NOT EXISTS idx_meal_item_food_id ON meal_item(food_id);
-- doppelte Einträge (gleiches Food im gleichen Meal) verhindern
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes
    WHERE schemaname='public' AND indexname='ux_meal_item_unique'
  ) THEN
CREATE UNIQUE INDEX ux_meal_item_unique ON meal_item(meal_id, food_id);

ALTER TABLE public.meal
DROP COLUMN IF EXISTS meal_type;
END IF;
END $$;