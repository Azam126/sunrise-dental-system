package lk.zaa.sunrise.api.service;

import jakarta.transaction.Transactional;
import lk.zaa.sunrise.api.entity.Appointment;
import lk.zaa.sunrise.api.entity.Dentist;
import lk.zaa.sunrise.api.entity.Patient;
import lk.zaa.sunrise.api.entity.TreatmentType;
import lk.zaa.sunrise.api.exception.DuplicateBookingException;
import lk.zaa.sunrise.api.exception.ResourceNotFoundException;
import lk.zaa.sunrise.common.enums.AppointmentStatus;
import lk.zaa.sunrise.api.mapper.AppointmentMapper;
import lk.zaa.sunrise.api.pattern.AppointmentNumberGenerator;
import lk.zaa.sunrise.api.repository.AppointmentRepository;
import lk.zaa.sunrise.api.repository.DentistRepository;
import lk.zaa.sunrise.api.repository.PatientRepository;
import lk.zaa.sunrise.api.repository.TreatmentTypeRepository;
import lk.zaa.sunrise.common.dto.AppointmentRequest;
import lk.zaa.sunrise.common.dto.AppointmentResponse;
import org.springframework.stereotype.Service;

/** Implements the Register New Appointment sequence diagram from Task A, Figure 4. */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;
    private final AppointmentNumberGenerator numberGenerator;
    private final AppointmentMapper mapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               DentistRepository dentistRepository,
                               TreatmentTypeRepository treatmentTypeRepository,
                               AppointmentNumberGenerator numberGenerator,
                               AppointmentMapper mapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
        this.numberGenerator = numberGenerator;
        this.mapper = mapper;
    }

    @Transactional
    public AppointmentResponse register(AppointmentRequest request) {
        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found: " + request.getDentistId()));

        TreatmentType treatment = treatmentTypeRepository.findById(request.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment type not found: " + request.getTreatmentId()));

        boolean clash = appointmentRepository
                .existsByDentist_DentistIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        request.getDentistId(), request.getAppointmentDate(), request.getAppointmentTime(),
                        AppointmentStatus.CANCELLED);
        if (clash) {
            throw new DuplicateBookingException(
                    "Dr. " + dentist.getName() + " already has an appointment at "
                            + request.getAppointmentDate() + " " + request.getAppointmentTime());
        }

        Patient patient = new Patient(request.getPatientName(), request.getAddress(), request.getContactNumber());
        patientRepository.save(patient);

        String appointmentNumber = numberGenerator.generate();
        Appointment appointment = new Appointment(appointmentNumber, patient, dentist, treatment,
                request.getAppointmentDate(), request.getAppointmentTime());

        appointmentRepository.save(appointment);
        return mapper.toResponse(appointment);
    }

    public AppointmentResponse findByNumber(String appointmentNumber) {
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No appointment found with number " + appointmentNumber));
        return mapper.toResponse(appointment);
    }
}
