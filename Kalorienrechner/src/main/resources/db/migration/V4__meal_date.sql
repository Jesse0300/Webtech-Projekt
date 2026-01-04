-- Falls alte Daten existieren und NULL sind, setze ein Default-Datum
UPDATE meal
SET date_time = CURRENT_TIMESTAMP
WHERE date_time IS NULL;

-- Spalte umbenennen
ALTER TABLE meal
    RENAME COLUMN date_time TO date;

-- Typ von TIMESTAMP auf DATE konvertieren
ALTER TABLE meal
ALTER COLUMN date TYPE DATE
USING date::date;

-- Optional: NOT NULL erzwingen (passt zu deiner Entity)
ALTER TABLE meal
    ALTER COLUMN date SET NOT NULL;
