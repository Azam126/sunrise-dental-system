package lk.zaa.sunrise.api.repository;

import lk.zaa.sunrise.api.entity.Appointment;
import lk.zaa.sunrise.common.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    /**
     * Application-level double-booking check. This exists alongside the
     * database trigger in db-extras.sql (defence in depth): the trigger only
     * takes effect once that script is manually applied (see README), so the
     * service layer cannot rely on it alone for a rule this important.
     */
    boolean existsByDentist_DentistIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long dentistId, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus excludedStatus);

    long countByAppointmentDate(LocalDate appointmentDate);

    long countByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status);
}
