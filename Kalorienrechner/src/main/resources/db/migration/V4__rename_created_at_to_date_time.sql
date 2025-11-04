-- 1) Falls es date_time noch nicht gibt: anlegen (mit Default)
ALTER TABLE IF EXISTS meal
    ADD COLUMN IF NOT EXISTS date_time TIMESTAMP DEFAULT NOW();

-- 2) Daten übernehmen (nur wo date_time noch NULL ist)
UPDATE meal
SET date_time = created_at
WHERE date_time IS NULL
  AND created_at IS NOT NULL;

-- 3) (Optional) NOT NULL durchsetzen, wenn deine Entity kein null erlaubt
ALTER TABLE IF EXISTS meal
ALTER COLUMN date_time SET NOT NULL;

-- 4) (Optional) alte Spalte weg, um sauber zu bleiben
ALTER TABLE IF EXISTS meal
DROP COLUMN IF EXISTS created_at;