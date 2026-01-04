-- sichere Nulls ab
UPDATE meal
SET date_time = CURRENT_TIMESTAMP
WHERE date_time IS NULL;

-- umbenennen
ALTER TABLE meal
    RENAME COLUMN date_time TO date;

-- TIMESTAMP -> DATE
ALTER TABLE meal
ALTER COLUMN date TYPE DATE
USING date::date;

-- optional: NOT NULL (passt zur Entity)
ALTER TABLE meal
    ALTER COLUMN date SET NOT NULL;
