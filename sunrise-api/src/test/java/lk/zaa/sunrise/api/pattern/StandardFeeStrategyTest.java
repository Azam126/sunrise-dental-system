package lk.zaa.sunrise.api.pattern;

import lk.zaa.sunrise.api.entity.Appointment;
import lk.zaa.sunrise.api.entity.Dentist;
import lk.zaa.sunrise.api.entity.Patient;
import lk.zaa.sunrise.api.entity.TreatmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class StandardFeeStrategyTest {

    @Test
    @DisplayName("The bill total equals the appointment's treatment consultation fee")
    void calculatesFeeAsTheTreatmentsConsultationFee() {
        TreatmentType rootCanal = new TreatmentType("Root Canal Treatment", new BigDecimal("15000.00"));
        Patient patient = new Patient("Nimal Perera", "12 Galle Road, Colombo", "0771234567");
        Dentist dentist = new Dentist("Dr. Ruwan Fernando", "General Dentistry");
        Appointment appointment = new Appointment("APT-20260829-0001", patient, dentist, rootCanal,
                LocalDate.of(2026, 8, 29), LocalTime.of(10, 0));

        BigDecimal fee = new StandardFeeStrategy().calculate(appointment);

        assertThat(fee).isEqualByComparingTo("15000.00");
    }
}
