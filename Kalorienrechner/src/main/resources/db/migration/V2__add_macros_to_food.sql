-- 1) Spalten hinzufügen (idempotent)
ALTER TABLE IF EXISTS food
    ADD COLUMN IF NOT EXISTS protein DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS carbs   DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS fat     DOUBLE PRECISION;

-- 2) Defaults setzen (für neue Zeilen; optional)
ALTER TABLE IF EXISTS food
ALTER COLUMN protein SET DEFAULT 0,
    ALTER COLUMN carbs   SET DEFAULT 0,
    ALTER COLUMN fat     SET DEFAULT 0;

-- 3) Bestehende NULLs auffüllen (damit NOT NULL gleich klappt)
UPDATE food SET protein = 0 WHERE protein IS NULL;
UPDATE food SET carbs   = 0 WHERE carbs   IS NULL;
UPDATE food SET fat     = 0 WHERE fat     IS NULL;

-- 4) (Optional) NOT NULL nur setzen, wenn du es wirklich brauchst
ALTER TABLE IF EXISTS food
ALTER COLUMN protein SET NOT NULL,
    ALTER COLUMN carbs   SET NOT NULL,
    ALTER COLUMN fat     SET NOT NULL;