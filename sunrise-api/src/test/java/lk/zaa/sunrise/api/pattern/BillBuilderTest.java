package lk.zaa.sunrise.api.pattern;

import lk.zaa.sunrise.api.entity.Appointment;
import lk.zaa.sunrise.api.entity.Bill;
import lk.zaa.sunrise.api.entity.Dentist;
import lk.zaa.sunrise.api.entity.Patient;
import lk.zaa.sunrise.api.entity.TreatmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class BillBuilderTest {

    @Test
    @DisplayName("Builds a Bill whose total is the strategy's result and whose issue date is today (per the injected Clock)")
    void buildsABillWithTheStrategysCalculatedTotalAndTodaysDate() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-29T12:00:00Z"), ZoneId.of("UTC"));
        TreatmentType scaling = new TreatmentType("Scaling & Polishing", new BigDecimal("4500.00"));
        Patient patient = new Patient("Anusha Silva", "45 Kandy Road, Kandy", "0777654321");
        Dentist dentist = new Dentist("Dr. Ishara Gunawardena", "Orthodontics");
        Appointment appointment = new Appointment("APT-20260829-0002", patient, dentist, scaling,
                LocalDate.of(2026, 8, 29), LocalTime.of(14, 30));

        BillBuilder builder = new BillBuilder(new StandardFeeStrategy(), fixedClock);
        Bill bill = builder.build(appointment);

        assertThat(bill.getAppointment()).isSameAs(appointment);
        assertThat(bill.getTotalAmount()).isEqualByComparingTo("4500.00");
        assertThat(bill.getIssueDate()).isEqualTo(LocalDate.of(2026, 8, 29));
    }

    @Test
    @DisplayName("BillBuilder uses whichever FeeCalculationStrategy it is given, not the treatment's own fee")
    void usesWhateverFeeStrategyItIsGiven() {
        // Demonstrates the STRATEGY pattern's whole point: BillBuilder does not
        // know or care how the fee is calculated — swapping in a different
        // strategy changes the bill total without touching BillBuilder at all.
        FeeCalculationStrategy alwaysFiveThousand = appointment -> new BigDecimal("5000.00");
        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-29T12:00:00Z"), ZoneId.of("UTC"));

        TreatmentType extraction = new TreatmentType("Tooth Extraction", new BigDecimal("6000.00"));
        Patient patient = new Patient("Kamal Jayasuriya", "9 Main Street, Galle", "0712223344");
        Dentist dentist = new Dentist("Dr. Ruwan Fernando", "General Dentistry");
        Appointment appointment = new Appointment("APT-20260829-0003", patient, dentist, extraction,
                LocalDate.of(2026, 8, 29), LocalTime.of(9, 0));

        Bill bill = new BillBuilder(alwaysFiveThousand, fixedClock).build(appointment);

        // Note: the strategy's flat 5000 is used, NOT the treatment's own 6000 fee.
        assertThat(bill.getTotalAmount()).isEqualByComparingTo("5000.00");
    }
}
