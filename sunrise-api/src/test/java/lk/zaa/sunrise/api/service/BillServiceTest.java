package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.entity.*;
import lk.zaa.sunrise.api.exception.ResourceNotFoundException;
import lk.zaa.sunrise.api.mapper.BillMapper;
import lk.zaa.sunrise.api.pattern.BillBuilder;
import lk.zaa.sunrise.api.pattern.StandardFeeStrategy;
import lk.zaa.sunrise.api.repository.AppointmentRepository;
import lk.zaa.sunrise.api.repository.BillRepository;
import lk.zaa.sunrise.common.dto.BillResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private BillRepository billRepository;

    private BillService billService;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-29T12:00:00Z"), ZoneId.of("UTC"));
        BillBuilder realBuilder = new BillBuilder(new StandardFeeStrategy(), fixedClock);
        billService = new BillService(appointmentRepository, billRepository, realBuilder, new BillMapper());

        TreatmentType treatment = new TreatmentType("Scaling & Polishing", new BigDecimal("4500.00"));
        Dentist dentist = new Dentist("Dr. Ishara Gunawardena", "Orthodontics");
        Patient patient = new Patient("Kamal Jayasuriya", "9 Main Street, Galle", "0712223344");
        appointment = new Appointment("APT-20260829-0003", patient, dentist, treatment,
                LocalDate.of(2026, 8, 29), LocalTime.of(9, 0));
    }

    @Test
    @DisplayName("Generating a bill for the first time builds and saves a new Bill")
    void generatesNewBillWhenNoneExists() {
        when(billRepository.findByAppointment_AppointmentNumber("APT-20260829-0003"))
                .thenReturn(Optional.empty());
        when(appointmentRepository.findByAppointmentNumber("APT-20260829-0003"))
                .thenReturn(Optional.of(appointment));

        BillResponse response = billService.generateBill("APT-20260829-0003");

        assertThat(response.getTotalAmount()).isEqualByComparingTo("4500.00");
        assertThat(response.getPatientName()).isEqualTo("Kamal Jayasuriya");
        verify(billRepository).save(any(Bill.class));
    }

    @Test
    @DisplayName("Generating a bill twice for the same appointment returns the existing bill (idempotent, no duplicate)")
    void returnsExistingBillInsteadOfDuplicating() {
        Bill existingBill = new BillBuilder(new StandardFeeStrategy(),
                Clock.fixed(Instant.parse("2026-08-29T12:00:00Z"), ZoneId.of("UTC"))).build(appointment);
        when(billRepository.findByAppointment_AppointmentNumber("APT-20260829-0003"))
                .thenReturn(Optional.of(existingBill));

        BillResponse response = billService.generateBill("APT-20260829-0003");

        assertThat(response.getTotalAmount()).isEqualByComparingTo("4500.00");
        verify(billRepository, never()).save(any());
        verify(appointmentRepository, never()).findByAppointmentNumber(any());
    }

    @Test
    @DisplayName("Generating a bill for an unknown appointment number throws ResourceNotFoundException")
    void failsForUnknownAppointment() {
        when(billRepository.findByAppointment_AppointmentNumber("APT-NOPE")).thenReturn(Optional.empty());
        when(appointmentRepository.findByAppointmentNumber("APT-NOPE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> billService.generateBill("APT-NOPE"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(billRepository, never()).save(any());
    }
}
