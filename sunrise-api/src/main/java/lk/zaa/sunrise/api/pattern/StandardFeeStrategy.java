package lk.zaa.sunrise.api.pattern;

import lk.zaa.sunrise.api.entity.Appointment;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/** Default strategy: the bill total is simply the treatment's consultation fee. */
@Component
public class StandardFeeStrategy implements FeeCalculationStrategy {

    @Override
    public BigDecimal calculate(Appointment appointment) {
        return appointment.getTreatmentType().getConsultationFee();
    }
}
