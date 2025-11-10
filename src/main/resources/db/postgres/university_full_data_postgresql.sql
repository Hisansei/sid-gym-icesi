-- =========================
-- Countries
-- =========================
INSERT INTO COUNTRIES (code, name) VALUES (1, 'Colombia');

-- =========================
-- Departments
-- =========================
INSERT INTO DEPARTMENTS (code, name, country_code) VALUES
                                                        (1,  'Valle del Cauca', 1),
                                                        (2,  'Cundinamarca',    1),
                                                        (5,  'Antioquia',       1),
                                                        (8,  'Atlántico',       1),
                                                        (11, 'Bogotá D.C.',     1);

-- =========================
-- Cities
-- =========================
INSERT INTO CITIES (code, name, dept_code) VALUES
                                                (101, 'Cali',         1),
                                                (102, 'Bogotá',      11),
                                                (103, 'Medellín',     5),
                                                (104, 'Barranquilla', 8),
                                                (105, 'Soledad',      8);

-- =========================
-- Campuses
-- =========================
INSERT INTO CAMPUSES (code, name, city_code) VALUES
                                                (1, 'Campus Cali',        101),
                                                (2, 'Campus Bogotá',      102),
                                                (3, 'Campus Medellín',    103),
                                                (4, 'Campus Barranquilla',104);

-- =========================
-- Employee Types
-- =========================
INSERT INTO EMPLOYEE_TYPES (name) VALUES
                                        ('Docente'),
                                        ('Administrativo'),
                                        ('Instructor');

-- =========================
-- Contract Types
-- =========================
INSERT INTO CONTRACT_TYPES (name) VALUES ('Planta'), ('Cátedra');

-- =========================
-- Faculties (dean_id se setea al final)
-- =========================
INSERT INTO FACULTIES (code, name, location, phone_number, dean_id) VALUES
                                                                        (1, 'Facultad de Ciencias', 'Call3', '555-1234', NULL),
                                                                        (2, 'Facultad de Ingeniería', 'Call4', '555-5678', NULL);

-- =========================
-- Employees
-- =========================
INSERT INTO EMPLOYEES (
    id, first_name, last_name, email,
    contract_type, employee_type, faculty_code, campus_code, birth_place_code
) VALUES
        ('1001', 'Juan',   'Pérez',  'juan.perez@univcali.edu.co',  'Planta',  'Docente',        1, 1, 101),
        ('1002', 'María',  'Gómez',  'maria.gomez@univcali.edu.co', 'Planta',  'Administrativo', 1, 2, 102),
        ('1003', 'Carlos', 'López',  'carlos.lopez@univcali.edu.co','Cátedra', 'Docente',        2, 1, 103),
        ('1004', 'Carlos', 'Mejía',  'carlos.mejia@univcali.edu.co','Planta',  'Docente',        1, 3, 103),
        ('1005', 'Sandra', 'Ortiz',  'sandra.ortiz@univcali.edu.co','Cátedra', 'Docente',        2, 4, 104),
        ('1006', 'Julián', 'Reyes',  'julian.reyes@univcali.edu.co','Planta',  'Administrativo', 2, 1, 105),
        ('1007', 'Paula',  'Ramírez','paula.ramirez@univcali.edu.co','Planta', 'Instructor',     1, 1, 101),
        ('1008', 'Andrés', 'Castro', 'andres.castro@univcali.edu.co','Cátedra','Instructor',     1, 3, 103),
        ('1009', 'Pepito', 'Arcos', 'pepito.arcos@univcali.edu.co', 'Planta',  'Administrativo', 2, 1, 105); -- Administrador del gym [Prueba]

-- =========================
-- Areas
-- =========================
INSERT INTO AREAS (code, name, faculty_code, coordinator_id) VALUES
                                                                    (1, 'Área de Sociales',   1, '1001'),
                                                                    (2, 'Área de Ingeniería', 2, '1003'),
                                                                    (3, 'Área de Bienestar',  1, '1007');

-- =========================
-- Programs
-- =========================
INSERT INTO PROGRAMS (code, name, area_code) VALUES
                                                    (1, 'Psicología',              1),
                                                    (2, 'Ingeniería de Sistemas',  2);

-- =========================
-- Subjects
-- =========================
INSERT INTO SUBJECTS (code, name, program_code) VALUES
                                                    ('S101', 'Psicología General',  1),
                                                    ('S102', 'Cálculo I',           2),
                                                    ('S103', 'Programación',        2),
                                                    ('S104', 'Estructuras de Datos',2),
                                                    ('S105', 'Bases de Datos',      2),
                                                    ('S106', 'Redes de Computadores',2),
                                                    ('S107', 'Sistemas Operativos', 2),
                                                    ('S108', 'Algoritmos Avanzados',2);

-- =========================
-- Groups
-- =========================
INSERT INTO GROUPS (NRC, number, semester, subject_code, professor_id) VALUES
                                                                            ('G101', 1, '2023-2', 'S101', '1001'),
                                                                            ('G102', 2, '2023-2', 'S102', '1003'),
                                                                            ('G103', 3, '2023-2', 'S103', '1004'),
                                                                            ('G104', 4, '2023-2', 'S105', '1005'),
                                                                            ('G105', 5, '2023-2', 'S106', '1004');

-- =========================
-- Students
-- =========================
INSERT INTO STUDENTS (
    id, first_name, last_name, email, birth_date, birth_place_code, campus_code
) VALUES
        ('2001', 'Laura', 'Hernández', 'laura.hernandez@univcali.edu.co', '2000-03-15', 101, 1),
        ('2002', 'Pedro', 'Martínez',  'pedro.martinez@univcali.edu.co',  '1999-07-22', 103, 1),
        ('2003', 'Ana',   'Suárez',    'ana.suarez@univcali.edu.co',      '2001-01-05', 102, 2),
        ('2004', 'Luis',  'Ramírez',   'luis.ramirez@univcali.edu.co',    '1998-11-30', 104, 3),
        ('2005', 'Sofía', 'García',    'sofia.garcia@univcali.edu.co',    '2000-09-12', 105, 2);

-- =========================
-- Enrollments
-- =========================
INSERT INTO ENROLLMENTS (student_id, NRC, enrollment_date, status) VALUES
                                                                        ('2001', 'G101', '2023-08-01', 'Active'),
                                                                        ('2001', 'G102', '2023-08-01', 'Active'),
                                                                        ('2002', 'G103', '2023-08-02', 'Active'),
                                                                        ('2003', 'G103', '2023-08-02', 'Active'),
                                                                        ('2004', 'G104', '2023-08-03', 'Withdrawn'),
                                                                        ('2005', 'G105', '2023-08-03', 'Active');

-- =========================
-- Users
-- =========================
INSERT INTO USERS (
    username, password_hash, role, student_id, employee_id, is_active, created_at
) VALUES
        -- Estudiantes
        ('laura.h',  'hash_lh123', 'STUDENT',  '2001', NULL,   TRUE, CURRENT_TIMESTAMP),
        ('pedro.m',  'hash_pm123', 'STUDENT',  '2002', NULL,   TRUE, CURRENT_TIMESTAMP),
        ('ana.s',    'hash_as123', 'STUDENT',  '2003', NULL,   TRUE, CURRENT_TIMESTAMP),
        ('luis.r',   'hash_lr123', 'STUDENT',  '2004', NULL,   TRUE, CURRENT_TIMESTAMP),
        ('sofia.g',  'hash_sg123', 'STUDENT',  '2005', NULL,   TRUE, CURRENT_TIMESTAMP),

        -- Empleados (profesores/administrativos/instructores)
        ('juan.p',   'hash_jp123', 'EMPLOYEE', NULL,   '1001', TRUE, CURRENT_TIMESTAMP),
        ('maria.g',  'hash_mg123', 'EMPLOYEE', NULL,   '1002', TRUE, CURRENT_TIMESTAMP),
        ('carlos.l', 'hash_cl123', 'EMPLOYEE', NULL,   '1003', TRUE, CURRENT_TIMESTAMP),
        ('carlos.m', 'hash_cm123', 'EMPLOYEE', NULL,   '1004', TRUE, CURRENT_TIMESTAMP),
        ('sandra.o', 'hash_so123', 'EMPLOYEE', NULL,   '1005', TRUE, CURRENT_TIMESTAMP),
        ('paula.r',  'hash_pr123', 'EMPLOYEE', NULL,   '1007', TRUE, CURRENT_TIMESTAMP),
        ('andres.c', 'hash_ac123', 'EMPLOYEE', NULL,   '1008', TRUE, CURRENT_TIMESTAMP),

        -- Administrador(es) [Prueba]
        ('pepito.a', 'hash_pa123', 'ADMIN',    NULL,   '1009', TRUE, CURRENT_TIMESTAMP);

-- =========================
-- Hashes BCrypt
-- =========================
UPDATE users SET password_hash = '$2a$10$FW7gloavwxFmiF8xsf57HObtT3ZpwaBdgzqZVTevtR32470XhnthK' WHERE username = 'laura.h';
UPDATE users SET password_hash = '$2a$10$lb8nh4rIpxmVitUOqyilq.NlR2VmhX3Vkh6K.8UDYPUtXyGPtOwUe' WHERE username = 'pedro.m';
UPDATE users SET password_hash = '$2a$10$ivwNHM87GWZJ6X.n.gNsl.gNABjjyma7HUnmuIF.DKaiT4x.0jM.G' WHERE username = 'ana.s';
UPDATE users SET password_hash = '$2a$10$Sewt/v.7x.7ByYsy.5hzmuQ/AZXXSVLl6/IRkzXU8r88/keNtZDSe' WHERE username = 'luis.r';
UPDATE users SET password_hash = '$2a$10$lRVjl8BacPO6nGSZGTXCR.U6uGGZQDD4RvLVPqHP9x5cLOUyZjl4e' WHERE username = 'sofia.g';

UPDATE users SET password_hash = '$2a$10$LoSFejG3oC4Bikabc3ptiejYkqJZJ9AKAFTFqUWpHc8tX4oRLGK/q' WHERE username = 'juan.p';
UPDATE users SET password_hash = '$2a$10$qrOK2YVTRptSHJB.GzDHA.M6VYknTDC5SctND3AW4Q80BCVmD8dbm' WHERE username = 'maria.g';
UPDATE users SET password_hash = '$2a$10$UOs9LLyZrufzLeP4uWOx6erPnnvIrXCZakld5IDkcVpYsa2nsCtYi' WHERE username = 'carlos.l';
UPDATE users SET password_hash = '$2a$10$BwxnpYTMDMMGZ98jQImCUeGG0mnYW8ShtgN7TNUCocJVI5tRFlraq' WHERE username = 'carlos.m';
UPDATE users SET password_hash = '$2a$10$YkTcVoMIx5rvSiaXHQwAw.odvLzPyhjuVtwel2qcHsMp5e0Rcjy2.' WHERE username = 'sandra.o';
UPDATE users SET password_hash = '$2a$10$BE0YZCnWDVGhXeIjiL.GQe23xZfeL6GwyEO2ysZqROzP6jsquvjCO' WHERE username = 'paula.r';
UPDATE users SET password_hash = '$2a$10$XrRJnujctTzjLnHv/k3sTODxq9ZGKILWe5gGID7NHU./JHrJrxwOC' WHERE username = 'andres.c';

UPDATE users SET password_hash = '$2a$10$E637lQy51VSagThtkZ7Mwuuo1qm72pqGY6gGRbrM3tQWyWQRSXtaC' WHERE username = 'pepito.a';

-- =========================
-- Stats (vacías)
-- =========================
-- trainer_monthly_stats: trainer_username, period, new_assignments, followups_made
-- user_monthly_stats:   user_username,   period, routines_started, followups_made
