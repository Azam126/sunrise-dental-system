package lk.zaa.sunrise.api.entity;

import lk.zaa.sunrise.api.pattern.FeeCalculationStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

/**
 * BUILDER PATTERN.
 *
 * Building a Bill involves several steps that must happen in a fixed order
 * (fetch the appointment's treatment fee, apply the fee strategy, stamp the
 * issue date) before a valid, immutable Bill can exist. Bill's all-args
 * constructor is package-private specifically so this builder is the only
 * supported way to create one — callers cannot accidentally construct a Bill
 * with a missing or miscalculated total.
 *
 * BUG FIX: this class originally lived in lk.zaa.sunrise.api.pattern, which
 * meant Bill's package-private constructor was actually unreachable from it —
 * Java's package-private access is scoped to "same package", not "this one
 * approved class", so the two being in different packages was a compile
 * error (caught by a real `mvn test` run in GitHub Actions CI, which this
 * sandbox has never had the network access to run). Moving BillBuilder here,
 * into entity alongside Bill, is what actually makes the intended
 * encapsulation compile-enforced rather than just a comment.
 *
 * Clock is injected for the same reason as AppointmentNumberGenerator (see
 * its Javadoc): BillBuilderTest can then assert the exact issue date stamped
 * on a Bill, using Clock.fixed(...), instead of a test that could flake if
 * it happens to run right around midnight.
 */
@Component
public class BillBuilder {

    private final FeeCalculationStrategy feeStrategy;
    private final Clock clock;

    public BillBuilder(FeeCalculationStrategy feeStrategy, Clock clock) {
        this.feeStrategy = feeStrategy;
        this.clock = clock;
    }

    public Bill build(Appointment appointment) {
        BigDecimal total = feeStrategy.calculate(appointment);
        return new Bill(appointment, total, LocalDate.now(clock));
    }
}
