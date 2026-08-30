package lk.zaa.sunrise.api.pattern;

import org.springframework.stereotype.Component;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SINGLETON PATTERN.
 *
 * There must be exactly one authority handing out appointment numbers for the
 * whole application, otherwise two receptionists working at the same time could
 * be issued the same number — reintroducing the double-booking problem the
 * clinic is trying to eliminate. Spring already manages this class as a
 * singleton bean (default scope), and the internal counter is additionally
 * guarded with an AtomicInteger so the guarantee holds even under concurrent
 * requests from multiple JavaFX clients hitting the REST API at once.
 *
 * Format: APT-yyyyMMdd-#### (e.g. APT-20260829-0007), which keeps numbers
 * short, human-readable on a printed bill, and naturally unique per day.
 *
 * TDD NOTE (Task C): this class originally called LocalDate.now() directly.
 * While writing AppointmentNumberGeneratorTest, the daily-counter-reset
 * behaviour turned out to be untestable that way — a test cannot make a real
 * day pass. The fix was to inject a java.time.Clock (see AppConfig) instead
 * of calling LocalDate.now() directly, so a test can supply
 * Clock.fixed(...) and control "today" precisely. This is a small but real
 * example of a test driving a design change, not just checking one.
 */
@Component
public class AppointmentNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Clock clock;
    private final AtomicInteger dailyCounter = new AtomicInteger(0);
    private volatile LocalDate lastResetDate;

    public AppointmentNumberGenerator(Clock clock) {
        this.clock = clock;
        this.lastResetDate = LocalDate.now(clock);
    }

    public synchronized String generate() {
        LocalDate today = LocalDate.now(clock);
        if (!today.equals(lastResetDate)) {
            dailyCounter.set(0);
            lastResetDate = today;
        }
        int next = dailyCounter.incrementAndGet();
        return "APT-" + today.format(DATE_FORMAT) + "-" + String.format("%04d", next);
    }
}
