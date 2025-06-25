-- Добавляем поле is_banned в таблицу users
ALTER TABLE users ADD COLUMN is_banned BOOLEAN DEFAULT FALSE NOT NULL;

-- Добавляем поле is_ban в таблицу blacklist
ALTER TABLE blacklist ADD COLUMN is_ban BOOLEAN DEFAULT FALSE NOT NULL;
