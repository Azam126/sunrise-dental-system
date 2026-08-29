package lk.zaa.sunrise.api.pattern;

import org.springframework.stereotype.Component;
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
 */
@Component
public class AppointmentNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AtomicInteger dailyCounter = new AtomicInteger(0);
    private volatile LocalDate lastResetDate = LocalDate.now();

    public synchronized String generate() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastResetDate)) {
            dailyCounter.set(0);
            lastResetDate = today;
        }
        int next = dailyCounter.incrementAndGet();
        return "APT-" + today.format(DATE_FORMAT) + "-" + String.format("%04d", next);
    }
}
