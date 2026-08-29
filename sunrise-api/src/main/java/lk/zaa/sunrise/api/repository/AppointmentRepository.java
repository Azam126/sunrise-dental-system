package lk.zaa.sunrise.api.repository;

import lk.zaa.sunrise.api.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
}
