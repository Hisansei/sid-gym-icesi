-- -----------------------------
-- Módulo geográfico
-- -----------------------------
CREATE TABLE COUNTRIES (
                           code  INTEGER      NOT NULL,
                           name  VARCHAR(20)  NOT NULL
);
ALTER TABLE COUNTRIES ADD CONSTRAINT COUNTRIES_PK PRIMARY KEY (code);

CREATE TABLE DEPARTMENTS (
                             code         INTEGER      NOT NULL,
                             name         VARCHAR(20)  NOT NULL,
                             country_code INTEGER      NOT NULL
);
ALTER TABLE DEPARTMENTS ADD CONSTRAINT DEPARTMENTS_PK PRIMARY KEY (code);

CREATE TABLE CITIES (
                        code      INTEGER      NOT NULL,
                        name      VARCHAR(20)  NOT NULL,
                        dept_code INTEGER      NOT NULL
);
ALTER TABLE CITIES ADD CONSTRAINT CITIES_PK PRIMARY KEY (code);

-- -----------------------------
-- Módulo institucional
-- -----------------------------
CREATE TABLE CONTRACT_TYPES (
                                name VARCHAR(30) NOT NULL
);
ALTER TABLE CONTRACT_TYPES ADD CONSTRAINT CONTRACT_TYPES_PK PRIMARY KEY (name);

CREATE TABLE EMPLOYEE_TYPES (
                                name VARCHAR(30) NOT NULL
);
ALTER TABLE EMPLOYEE_TYPES ADD CONSTRAINT EMPLOYEE_TYPES_PK PRIMARY KEY (name);

CREATE TABLE FACULTIES (
                           code         INTEGER      NOT NULL,
                           name         VARCHAR(40)  NOT NULL,
                           location     VARCHAR(15)  NOT NULL,
                           phone_number VARCHAR(15)  NOT NULL,
                           dean_id      VARCHAR(15)  -- se setea luego con UPDATE
);
CREATE UNIQUE INDEX FACULTIES__IDX ON FACULTIES (dean_id);
ALTER TABLE FACULTIES ADD CONSTRAINT FACULTIES_PK PRIMARY KEY (code);

CREATE TABLE CAMPUSES (
                          code      INTEGER      NOT NULL,
                          name      VARCHAR(20),
                          city_code INTEGER      NOT NULL
);
ALTER TABLE CAMPUSES ADD CONSTRAINT CAMPUSES_PK PRIMARY KEY (code);

CREATE TABLE EMPLOYEES (
                           id               VARCHAR(15) NOT NULL,
                           first_name       VARCHAR(30) NOT NULL,
                           last_name        VARCHAR(30) NOT NULL,
                           email            VARCHAR(30) NOT NULL,
                           contract_type    VARCHAR(30) NOT NULL,
                           employee_type    VARCHAR(30) NOT NULL,
                           faculty_code     INTEGER     NOT NULL,
                           campus_code      INTEGER     NOT NULL,
                           birth_place_code INTEGER     NOT NULL
);
ALTER TABLE EMPLOYEES ADD CONSTRAINT EMPLOYEES_PK PRIMARY KEY (id);

CREATE TABLE AREAS (
                       code           INTEGER      NOT NULL,
                       name           VARCHAR(20)  NOT NULL,
                       faculty_code   INTEGER      NOT NULL,
                       coordinator_id VARCHAR(15)  NOT NULL
);
CREATE UNIQUE INDEX AREAS__IDX ON AREAS (coordinator_id);
ALTER TABLE AREAS ADD CONSTRAINT AREAS_PK PRIMARY KEY (code);

CREATE TABLE PROGRAMS (
                          code      INTEGER      NOT NULL,
                          name      VARCHAR(40)  NOT NULL,
                          area_code INTEGER      NOT NULL
);
ALTER TABLE PROGRAMS ADD CONSTRAINT PROGRAMS_PK PRIMARY KEY (code);

CREATE TABLE SUBJECTS (
                          code         VARCHAR(10) NOT NULL,
                          name         VARCHAR(30) NOT NULL,
                          program_code INTEGER     NOT NULL
);
ALTER TABLE SUBJECTS ADD CONSTRAINT SUBJECTS_PK PRIMARY KEY (code);

CREATE TABLE GROUPS (
                        NRC          VARCHAR(10)  NOT NULL,
                        number       INTEGER      NOT NULL,
                        semester     VARCHAR(6)   NOT NULL,
                        subject_code VARCHAR(10)  NOT NULL,
                        professor_id VARCHAR(15)  NOT NULL
);
ALTER TABLE GROUPS ADD CONSTRAINT GROUPS_PK PRIMARY KEY (NRC);

CREATE TABLE STUDENTS (
                          id               VARCHAR(15) NOT NULL,
                          first_name       VARCHAR(30) NOT NULL,
                          last_name        VARCHAR(30) NOT NULL,
                          email            VARCHAR(50) NOT NULL,
                          birth_date       DATE        NOT NULL,
                          birth_place_code INTEGER     NOT NULL,
                          campus_code      INTEGER     NOT NULL
);
ALTER TABLE STUDENTS ADD CONSTRAINT STUDENTS_PK PRIMARY KEY (id);

CREATE TABLE ENROLLMENTS (
                             student_id      VARCHAR(15) NOT NULL,
                             NRC             VARCHAR(10) NOT NULL,
                             enrollment_date DATE        NOT NULL,
                             status          VARCHAR(15) NOT NULL
);
ALTER TABLE ENROLLMENTS ADD CONSTRAINT ENROLLMENTS_PK PRIMARY KEY (student_id, NRC);

-- -----------------------------
-- Módulo de usuarios
-- -----------------------------
CREATE TABLE USERS (
                       username      VARCHAR(100) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       role          VARCHAR(50)  NOT NULL,
                       student_id    VARCHAR(15),
                       employee_id   VARCHAR(15),
                       is_active     BOOLEAN      DEFAULT TRUE,
                       created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE USERS ADD CONSTRAINT USERS_PK PRIMARY KEY (username);

-- -----------------------------
-- Constraints diferidas / FKs
-- -----------------------------
ALTER TABLE STUDENTS   ADD CONSTRAINT STUDENTS_CITIES_FK
    FOREIGN KEY (birth_place_code) REFERENCES CITIES (code);
ALTER TABLE STUDENTS   ADD CONSTRAINT STUDENTS_CAMPUSES_FK
    FOREIGN KEY (campus_code) REFERENCES CAMPUSES (code);

ALTER TABLE AREAS      ADD CONSTRAINT AREAS_EMPLOYEES_FK
    FOREIGN KEY (coordinator_id) REFERENCES EMPLOYEES (id);
ALTER TABLE AREAS      ADD CONSTRAINT AREAS_FACULTIES_FK
    FOREIGN KEY (faculty_code) REFERENCES FACULTIES (code);

ALTER TABLE SUBJECTS   ADD CONSTRAINT SUBJECTS_PROGRAMS_FK
    FOREIGN KEY (program_code) REFERENCES PROGRAMS (code);

ALTER TABLE CITIES     ADD CONSTRAINT CITIES_DEPARTMENTS_FK
    FOREIGN KEY (dept_code) REFERENCES DEPARTMENTS (code);
ALTER TABLE DEPARTMENTS ADD CONSTRAINT DEPARTMENTS_COUNTRIES_FK
    FOREIGN KEY (country_code) REFERENCES COUNTRIES (code);

ALTER TABLE EMPLOYEES  ADD CONSTRAINT EMPLOYEES_CONTRACT_TYPES_FK
    FOREIGN KEY (contract_type) REFERENCES CONTRACT_TYPES (name);
ALTER TABLE EMPLOYEES  ADD CONSTRAINT EMPLOYEES_CITIES_FK
    FOREIGN KEY (birth_place_code) REFERENCES CITIES (code);
ALTER TABLE EMPLOYEES  ADD CONSTRAINT EMPLOYEES_FACULTIES_FK
    FOREIGN KEY (faculty_code) REFERENCES FACULTIES (code);
ALTER TABLE EMPLOYEES  ADD CONSTRAINT EMPLOYEES_CAMPUSES_FK
    FOREIGN KEY (campus_code) REFERENCES CAMPUSES (code);
ALTER TABLE EMPLOYEES  ADD CONSTRAINT EMPLOYEES_EMPLOYEE_TYPES_FK
    FOREIGN KEY (employee_type) REFERENCES EMPLOYEE_TYPES (name);

ALTER TABLE FACULTIES  ADD CONSTRAINT FACULTIES_EMPLOYEES_FK
    FOREIGN KEY (dean_id) REFERENCES EMPLOYEES (id);

ALTER TABLE GROUPS     ADD CONSTRAINT GROUPS_SUBJECTS_FK
    FOREIGN KEY (subject_code) REFERENCES SUBJECTS (code);
ALTER TABLE GROUPS     ADD CONSTRAINT GROUPS_EMPLOYEES_FK
    FOREIGN KEY (professor_id) REFERENCES EMPLOYEES (id);

ALTER TABLE ENROLLMENTS ADD CONSTRAINT ENROLLMENTS_STUDENTS_FK
    FOREIGN KEY (student_id) REFERENCES STUDENTS (id);
ALTER TABLE ENROLLMENTS ADD CONSTRAINT ENROLLMENTS_GROUPS_FK
    FOREIGN KEY (NRC) REFERENCES GROUPS (NRC);

ALTER TABLE USERS      ADD CONSTRAINT USERS_STUDENTS_FK
    FOREIGN KEY (student_id) REFERENCES STUDENTS (id);
ALTER TABLE USERS      ADD CONSTRAINT USERS_EMPLOYEES_FK
    FOREIGN KEY (employee_id) REFERENCES EMPLOYEES (id);
ALTER TABLE USERS      ADD CONSTRAINT USERS_ONE_ROLE_CHK
    CHECK (
        (student_id IS NOT NULL AND employee_id IS NULL)
            OR (student_id IS NULL AND employee_id IS NOT NULL)
        );

-- -----------------------------
-- Tablas de ESTADÍSTICAS (requisito)
-- -----------------------------
CREATE TABLE trainer_monthly_stats (
                                       trainer_username VARCHAR(100) NOT NULL,
                                       period           CHAR(7)      NOT NULL,   -- YYYY-MM
                                       new_assignments  INTEGER      NOT NULL DEFAULT 0,
                                       followups_made   INTEGER      NOT NULL DEFAULT 0
);
ALTER TABLE trainer_monthly_stats
    ADD CONSTRAINT trainer_monthly_stats_pk PRIMARY KEY (trainer_username, period);
ALTER TABLE trainer_monthly_stats
    ADD CONSTRAINT trainer_monthly_stats_users_fk
        FOREIGN KEY (trainer_username) REFERENCES USERS (username);

-- Usuario por mes
CREATE TABLE user_monthly_stats (
                                    user_username   VARCHAR(100) NOT NULL,
                                    period          CHAR(7)      NOT NULL,   -- YYYY-MM
                                    routines_started INTEGER     NOT NULL DEFAULT 0,
                                    followups_made   INTEGER     NOT NULL DEFAULT 0
);
ALTER TABLE user_monthly_stats
    ADD CONSTRAINT user_monthly_stats_pk PRIMARY KEY (user_username, period);
ALTER TABLE user_monthly_stats
    ADD CONSTRAINT user_monthly_stats_users_fk
        FOREIGN KEY (user_username) REFERENCES USERS (username);
