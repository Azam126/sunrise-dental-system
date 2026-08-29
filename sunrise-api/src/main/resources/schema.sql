-- Sunrise Dental Clinic — Appointment and Patient Management System
-- MySQL schema. Applied automatically by Spring Boot on startup (spring.sql.init).
-- Matches the entities in lk.zaa.sunrise.api.entity, which mirror the Task A class diagram.
--
-- NOTE: the double-booking trigger and revenue function are NOT in this
-- file — see db-extras.sql and the README for why, and how to apply them.

CREATE TABLE IF NOT EXISTS users (
    user_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    full_name      VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL   -- discriminator: ADMINISTRATOR | RECEPTIONIST
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS patients (
    patient_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    address         VARCHAR(255) NOT NULL,
    contact_number  VARCHAR(20)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS dentists (
    dentist_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    specialization  VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS treatment_types (
    treatment_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    treatment_name     VARCHAR(100)   NOT NULL,
    consultation_fee   DECIMAL(10,2)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS appointments (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_number   VARCHAR(20)  NOT NULL UNIQUE,
    patient_id           BIGINT       NOT NULL,
    dentist_id           BIGINT       NOT NULL,
    treatment_id         BIGINT       NOT NULL,
    appointment_date     DATE         NOT NULL,
    appointment_time     TIME         NOT NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_appt_patient   FOREIGN KEY (patient_id)   REFERENCES patients(patient_id),
    CONSTRAINT fk_appt_dentist   FOREIGN KEY (dentist_id)   REFERENCES dentists(dentist_id),
    CONSTRAINT fk_appt_treatment FOREIGN KEY (treatment_id) REFERENCES treatment_types(treatment_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bills (
    bill_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id  BIGINT        NOT NULL UNIQUE,
    total_amount    DECIMAL(10,2) NOT NULL,
    issue_date      DATE          NOT NULL,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id)
) ENGINE=InnoDB;
