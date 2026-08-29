-- Seed data so the system is usable immediately after first startup.
-- Passwords below are BCrypt hashes of the plaintext shown in each comment —
-- generated with Spring Security's BCryptPasswordEncoder (strength 10).

-- username: admin      | password: Admin@123
INSERT INTO users (username, password_hash, full_name, role)
SELECT 'admin', '$2b$10$Sg5zfTxUnKmVo5Ix6c7Ag.sput5mgn.c6h8c4DHDNy/st6TWadUm2', 'Nadeesha Perera', 'ADMINISTRATOR'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- username: reception  | password: Front@123
INSERT INTO users (username, password_hash, full_name, role)
SELECT 'reception', '$2b$10$SVmonTvV9pPnpJJhV7nwDOxb5wANestIjwm.FTgam4/rAAa0okJcW', 'Kasun Silva', 'RECEPTIONIST'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'reception');

INSERT INTO dentists (name, specialization)
SELECT * FROM (SELECT 'Dr. Ruwan Fernando' AS name, 'General Dentistry' AS specialization) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM dentists WHERE name = 'Dr. Ruwan Fernando');

INSERT INTO dentists (name, specialization)
SELECT * FROM (SELECT 'Dr. Ishara Gunawardena' AS name, 'Orthodontics' AS specialization) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM dentists WHERE name = 'Dr. Ishara Gunawardena');

INSERT INTO treatment_types (treatment_name, consultation_fee)
SELECT * FROM (SELECT 'General Checkup' AS treatment_name, 2500.00 AS consultation_fee) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM treatment_types WHERE treatment_name = 'General Checkup');

INSERT INTO treatment_types (treatment_name, consultation_fee)
SELECT * FROM (SELECT 'Scaling & Polishing' AS treatment_name, 4500.00 AS consultation_fee) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM treatment_types WHERE treatment_name = 'Scaling & Polishing');

INSERT INTO treatment_types (treatment_name, consultation_fee)
SELECT * FROM (SELECT 'Tooth Extraction' AS treatment_name, 6000.00 AS consultation_fee) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM treatment_types WHERE treatment_name = 'Tooth Extraction');

INSERT INTO treatment_types (treatment_name, consultation_fee)
SELECT * FROM (SELECT 'Root Canal Treatment' AS treatment_name, 15000.00 AS consultation_fee) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM treatment_types WHERE treatment_name = 'Root Canal Treatment');
