package lk.zaa.sunrise.api.testutil;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A Clock whose instant can be moved forward mid-test. java.time.Clock has
 * no built-in way to do this (Clock.fixed() is immutable by design), but
 * testing "does the SAME AppointmentNumberGenerator instance reset its
 * counter when the day changes under it" genuinely needs one — otherwise
 * the test can only prove that two different instances behave differently,
 * which is a weaker claim than what the production code actually promises.
 */
public class MutableClock extends Clock {

    private Instant instant;
    private final ZoneId zone;

    public MutableClock(Instant instant, ZoneId zone) {
        this.instant = instant;
        this.zone = zone;
    }

    public void advanceTo(Instant newInstant) {
        this.instant = newInstant;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(instant, zone);
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
