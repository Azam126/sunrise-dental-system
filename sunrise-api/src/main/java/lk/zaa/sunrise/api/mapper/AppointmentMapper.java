package lk.zaa.sunrise.api.mapper;

import lk.zaa.sunrise.api.entity.Appointment;
import lk.zaa.sunrise.common.dto.AppointmentResponse;
import org.springframework.stereotype.Component;

/**
 * DTO / MAPPER PATTERN.
 *
 * Entities never leave the service layer directly. Converting to DTOs here
 * means: (1) the REST contract stays stable even if the entity model changes,
 * and (2) we never accidentally serialise internal detail (e.g. the
 * passwordHash on a related User) back to the JavaFX client.
 */
@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {
        AppointmentResponse dto = new AppointmentResponse();
        dto.setAppointmentNumber(appointment.getAppointmentNumber());
        dto.setPatientName(appointment.getPatient().getName());
        dto.setAddress(appointment.getPatient().getAddress());
        dto.setContactNumber(appointment.getPatient().getContactNumber());
        dto.setDentistName(appointment.getDentist().getName());
        dto.setTreatmentName(appointment.getTreatmentType().getTreatmentName());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus());
        return dto;
    }
}
