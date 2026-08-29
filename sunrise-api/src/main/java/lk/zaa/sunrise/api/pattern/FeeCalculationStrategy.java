package lk.zaa.sunrise.api.pattern;

import lk.zaa.sunrise.api.entity.Appointment;
import java.math.BigDecimal;

/**
 * STRATEGY PATTERN.
 *
 * How the total on a bill is worked out is kept behind this interface rather
 * than hard-coded inside BillBuilder. Today there is a single
 * StandardFeeStrategy (treatment's consultation fee, unchanged), but the
 * clinic could later ask for a loyalty discount or an insurance-adjusted
 * strategy without BillBuilder, Bill, or any controller needing to change —
 * only a new implementation of this interface would be added.
 */
public interface FeeCalculationStrategy {
    BigDecimal calculate(Appointment appointment);
}
