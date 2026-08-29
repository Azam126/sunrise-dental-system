-- Run this file separately through the MySQL client / Workbench AFTER the
-- application has started once (so the tables above already exist) — e.g.:
--
--   mysql -u sunrise_app -p sunrise_dental < triggers.sql
--
-- It is kept out of schema.sql because Spring Boot's built-in SQL script
-- runner splits statements on ";" and does not understand the MySQL-CLI-only
-- "DELIMITER" directive that a multi-statement trigger body needs.
--
-- Advanced database feature: a second, DB-level line of defence against the
-- exact problem named in the brief — a dentist being double-booked for the
-- same date and time slot — on top of the application layer generating
-- unique appointment numbers via AppointmentNumberGenerator (Task B).

DROP TRIGGER IF EXISTS trg_prevent_double_booking;

DELIMITER //

CREATE TRIGGER trg_prevent_double_booking
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    DECLARE existing_count INT;

    SELECT COUNT(*) INTO existing_count
    FROM appointments
    WHERE dentist_id = NEW.dentist_id
      AND appointment_date = NEW.appointment_date
      AND appointment_time = NEW.appointment_time
      AND status <> 'CANCELLED';

    IF existing_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This dentist already has an appointment at that date and time.';
    END IF;
END//

DELIMITER ;
