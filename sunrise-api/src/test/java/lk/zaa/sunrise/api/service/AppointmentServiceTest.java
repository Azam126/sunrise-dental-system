package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.entity.Dentist;
import lk.zaa.sunrise.api.entity.TreatmentType;
import lk.zaa.sunrise.api.exception.DuplicateBookingException;
import lk.zaa.sunrise.api.exception.ResourceNotFoundException;
import lk.zaa.sunrise.api.mapper.AppointmentMapper;
import lk.zaa.sunrise.api.pattern.AppointmentNumberGenerator;
import lk.zaa.sunrise.api.repository.AppointmentRepository;
import lk.zaa.sunrise.api.repository.DentistRepository;
import lk.zaa.sunrise.api.repository.PatientRepository;
import lk.zaa.sunrise.api.repository.TreatmentTypeRepository;
import lk.zaa.sunrise.common.dto.AppointmentRequest;
import lk.zaa.sunrise.common.dto.AppointmentResponse;
import lk.zaa.sunrise.common.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TDD NOTE: registrationRejectsDoubleBooking() below was written FIRST,
 * before the check existed in AppointmentService.register() — it
 * originally failed (register() saved the clashing appointment instead of
 * throwing). Adding the existsBy... check and the DuplicateBookingException
 * throw is what turned this red test green; nothing about the test itself
 * changed afterwards. This is the concrete instance of TDD referenced in the
 * Task C report.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private DentistRepository dentistRepository;
    @Mock private TreatmentTypeRepository treatmentTypeRepository;
    @Mock private AppointmentNumberGenerator numberGenerator;
    // Constructed manually in setUp() (not @InjectMocks) so a real
    // AppointmentMapper — not a mock — does the entity-to-DTO mapping,
    // giving these tests genuine response data to assert on.
    private AppointmentService appointmentService;

    private final AppointmentMapper realMapper = new AppointmentMapper();

    private AppointmentRequest validRequest;
    private Dentist dentist;
    private TreatmentType treatment;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(appointmentRepository, patientRepository,
                dentistRepository, treatmentTypeRepository, numberGenerator, realMapper);

        dentist = new Dentist("Dr. Ruwan Fernando", "General Dentistry");
        treatment = new TreatmentType("General Checkup", new BigDecimal("2500.00"));

        validRequest = new AppointmentRequest();
        validRequest.setPatientName("Nimal Perera");
        validRequest.setAddress("12 Galle Road, Colombo");
        validRequest.setContactNumber("0771234567");
        validRequest.setDentistId(1L);
        validRequest.setTreatmentId(1L);
        validRequest.setAppointmentDate(LocalDate.of(2026, 8, 29));
        validRequest.setAppointmentTime(LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("A valid request is saved and returns an appointment number")
    void registersAppointmentSuccessfully() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentist_DentistIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                eq(1L), any(), any(), eq(AppointmentStatus.CANCELLED))).thenReturn(false);
        when(numberGenerator.generate()).thenReturn("APT-20260829-0001");

        AppointmentResponse response = appointmentService.register(validRequest);

        assertThat(response.getAppointmentNumber()).isEqualTo("APT-20260829-0001");
        assertThat(response.getPatientName()).isEqualTo("Nimal Perera");
        assertThat(response.getDentistName()).isEqualTo("Dr. Ruwan Fernando");
        verify(patientRepository).save(any());
        verify(appointmentRepository).save(any());
    }

    @Test
    @DisplayName("Registering when the dentist already has a slot at that date/time throws DuplicateBookingException")
    void registrationRejectsDoubleBooking() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.of(treatment));
        when(appointmentRepository.existsByDentist_DentistIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                eq(1L), eq(LocalDate.of(2026, 8, 29)), eq(LocalTime.of(10, 0)), eq(AppointmentStatus.CANCELLED)))
                .thenReturn(true);

        assertThatThrownBy(() -> appointmentService.register(validRequest))
                .isInstanceOf(DuplicateBookingException.class)
                .hasMessageContaining("Dr. Ruwan Fernando");

        verify(appointmentRepository, never()).save(any());
        verify(patientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registering with an unknown dentist ID throws ResourceNotFoundException")
    void registrationFailsForUnknownDentist() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.register(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Dentist not found");

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Registering with an unknown treatment ID throws ResourceNotFoundException")
    void registrationFailsForUnknownTreatment() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.register(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Treatment type not found");
    }

    @Test
    @DisplayName("Searching for a known appointment number returns its details")
    void findsAppointmentByNumber() {
        var patient = new lk.zaa.sunrise.api.entity.Patient("Anusha Silva", "45 Kandy Road", "0777654321");
        var appointment = new lk.zaa.sunrise.api.entity.Appointment(
                "APT-20260829-0002", patient, dentist, treatment,
                LocalDate.of(2026, 8, 29), LocalTime.of(14, 30));
        when(appointmentRepository.findByAppointmentNumber("APT-20260829-0002"))
                .thenReturn(Optional.of(appointment));

        AppointmentResponse response = appointmentService.findByNumber("APT-20260829-0002");

        assertThat(response.getPatientName()).isEqualTo("Anusha Silva");
        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Searching for an unknown appointment number throws ResourceNotFoundException")
    void searchFailsForUnknownAppointmentNumber() {
        when(appointmentRepository.findByAppointmentNumber("APT-NOPE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findByNumber("APT-NOPE"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
