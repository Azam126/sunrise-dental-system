package lk.zaa.sunrise.api.repository;

import lk.zaa.sunrise.api.entity.Appointment;
import lk.zaa.sunrise.api.entity.Dentist;
import lk.zaa.sunrise.api.entity.Patient;
import lk.zaa.sunrise.api.entity.TreatmentType;
import lk.zaa.sunrise.common.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test: exercises the real Spring Data JPA query derivation and
 * an in-memory H2 database, rather than mocking the repository the way the
 * *ServiceTest classes do. This is what actually proves the queries
 * AppointmentService relies on are correct, not just that the service calls
 * them with the right arguments.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
class AppointmentRepositoryTest {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DentistRepository dentistRepository;
    @Autowired private TreatmentTypeRepository treatmentTypeRepository;

    private Dentist dentist;
    private TreatmentType treatment;

    @BeforeEach
    void setUp() {
        dentist = dentistRepository.save(new Dentist("Dr. Ruwan Fernando", "General Dentistry"));
        treatment = treatmentTypeRepository.save(new TreatmentType("General Checkup", new BigDecimal("2500.00")));
    }

    @Test
    @DisplayName("An appointment can be found again by its appointment number")
    void findsByAppointmentNumber() {
        Patient patient = patientRepository.save(new Patient("Nimal Perera", "12 Galle Road", "0771234567"));
        appointmentRepository.save(new Appointment("APT-20260829-0001", patient, dentist, treatment,
                LocalDate.of(2026, 8, 29), LocalTime.of(10, 0)));

        var found = appointmentRepository.findByAppointmentNumber("APT-20260829-0001");

        assertThat(found).isPresent();
        assertThat(found.get().getPatient().getName()).isEqualTo("Nimal Perera");
    }

    @Test
    @DisplayName("Searching for a number that was never saved returns empty, not an exception")
    void returnsEmptyForUnknownNumber() {
        assertThat(appointmentRepository.findByAppointmentNumber("APT-DOES-NOT-EXIST")).isEmpty();
    }

    @Test
    @DisplayName("existsBy... correctly detects a same dentist/date/time clash")
    void detectsDoubleBookingClash() {
        Patient patientA = patientRepository.save(new Patient("Nimal Perera", "12 Galle Road", "0771234567"));
        appointmentRepository.save(new Appointment("APT-20260829-0001", patientA, dentist, treatment,
                LocalDate.of(2026, 8, 29), LocalTime.of(10, 0)));

        boolean clash = appointmentRepository
                .existsByDentist_DentistIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        dentist.getDentistId(), LocalDate.of(2026, 8, 29), LocalTime.of(10, 0),
                        AppointmentStatus.CANCELLED);

        assertThat(clash).isTrue();
    }

    @Test
    @DisplayName("existsBy... ignores a clash if the existing appointment at that slot was cancelled")
    void ignoresCancelledAppointmentsWhenCheckingForClashes() {
        Patient patientA = patientRepository.save(new Patient("Nimal Perera", "12 Galle Road", "0771234567"));
        Appointment cancelled = appointmentRepository.save(new Appointment("APT-20260829-0001", patientA, dentist,
                treatment, LocalDate.of(2026, 8, 29), LocalTime.of(10, 0)));
        cancelled.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(cancelled);

        boolean clash = appointmentRepository
                .existsByDentist_DentistIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        dentist.getDentistId(), LocalDate.of(2026, 8, 29), LocalTime.of(10, 0),
                        AppointmentStatus.CANCELLED);

        assertThat(clash).isFalse();
    }

    @Test
    @DisplayName("existsBy... does not clash for a different time on the same day")
    void noClashForDifferentTime() {
        Patient patientA = patientRepository.save(new Patient("Nimal Perera", "12 Galle Road", "0771234567"));
        appointmentRepository.save(new Appointment("APT-20260829-0001", patientA, dentist, treatment,
                LocalDate.of(2026, 8, 29), LocalTime.of(10, 0)));

        boolean clash = appointmentRepository
                .existsByDentist_DentistIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        dentist.getDentistId(), LocalDate.of(2026, 8, 29), LocalTime.of(11, 0),
                        AppointmentStatus.CANCELLED);

        assertThat(clash).isFalse();
    }

    @Test
    @DisplayName("The database itself rejects two appointments with the same appointment number")
    void enforcesUniqueAppointmentNumberAtTheDatabaseLevel() {
        Patient patientA = patientRepository.save(new Patient("Nimal Perera", "12 Galle Road", "0771234567"));
        Patient patientB = patientRepository.save(new Patient("Anusha Silva", "45 Kandy Road", "0777654321"));

        appointmentRepository.saveAndFlush(new Appointment("APT-20260829-0001", patientA, dentist, treatment,
                LocalDate.of(2026, 8, 29), LocalTime.of(10, 0)));

        assertThatThrownBy(() -> appointmentRepository.saveAndFlush(
                new Appointment("APT-20260829-0001", patientB, dentist, treatment,
                        LocalDate.of(2026, 8, 29), LocalTime.of(15, 0))))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
