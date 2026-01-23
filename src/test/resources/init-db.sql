CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
--DROP TABLE IF EXISTS incidents;
--DROP TABLE IF EXISTS protected_zones;
--DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS users (
   id SERIAL PRIMARY KEY,
   name VARCHAR(100),
   email VARCHAR(100),
   password VARCHAR(100),
   role VARCHAR(12)
);

CREATE TABLE IF NOT EXISTS protected_zones (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    geom GEOMETRY(Polygon, 4326)
);

CREATE TABLE IF NOT EXISTS incidents (
   id SERIAL PRIMARY KEY,
   category VARCHAR(100),
   description TEXT,
   urgency VARCHAR(12),
   report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   geom GEOMETRY(POINT, 4326),
   zone_id INTEGER REFERENCES protected_zones(id),
   user_id INTEGER REFERENCES users(id)
);

INSERT INTO users(name, email, password, role) VALUES ('Koko Popom', 'koko.popom@gmail.com', crypt('koko', gen_salt('bf')), 'ADMIN');

INSERT INTO protected_zones (name, geom)
VALUES ('Reserve Akwa', ST_GeomFromText('POLYGON((9.7 4.0, 9.8 4.0, 9.8 4.1, 9.7 4.1, 9.7 4.0))', 4326));

INSERT INTO incidents (category, description, urgency, geom, zone_id, user_id)
VALUES ('Fire', 'Fire detected', 'HIGH', ST_GeomFromText('POINT(9.75 4.05)', 4326), 1, 1);



