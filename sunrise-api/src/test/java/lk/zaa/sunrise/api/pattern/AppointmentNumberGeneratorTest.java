package lk.zaa.sunrise.api.pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TDD NOTE: this test class was written before AppointmentNumberGenerator was
 * refactored to accept a Clock. The very first version of these tests could
 * not exercise "does the counter reset on a new day?" at all — the class
 * called LocalDate.now() directly, so there was no way to control what day
 * the test ran on (red: cannot even write a meaningful assertion). Injecting
 * a Clock (see AppConfig / the class Javadoc) made testDailyCounterResets...
 * below possible (green), and every other test in this class continued to
 * pass unchanged afterwards (refactor confirmed safe).
 */
class AppointmentNumberGeneratorTest {

    @Test
    @DisplayName("Generated number follows the APT-yyyyMMdd-#### format")
    void generatesCorrectFormat() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-29T09:00:00Z"), ZoneId.of("UTC"));
        AppointmentNumberGenerator generator = new AppointmentNumberGenerator(fixedClock);

        String number = generator.generate();

        assertThat(number).matches("APT-20260829-\\d{4}");
        assertThat(number).isEqualTo("APT-20260829-0001");
    }

    @Test
    @DisplayName("Consecutive calls on the same day produce unique, incrementing numbers")
    void incrementsWithinSameDay() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-29T09:00:00Z"), ZoneId.of("UTC"));
        AppointmentNumberGenerator generator = new AppointmentNumberGenerator(fixedClock);

        String first = generator.generate();
        String second = generator.generate();
        String third = generator.generate();

        assertThat(first).isEqualTo("APT-20260829-0001");
        assertThat(second).isEqualTo("APT-20260829-0002");
        assertThat(third).isEqualTo("APT-20260829-0003");
    }

    @Test
    @DisplayName("Counter resets to 0001 when the SAME generator's clock moves to a new day")
    void dailyCounterResetsOnNewDay() {
        lk.zaa.sunrise.api.testutil.MutableClock clock =
                new lk.zaa.sunrise.api.testutil.MutableClock(Instant.parse("2026-08-29T23:59:00Z"), ZoneId.of("UTC"));
        AppointmentNumberGenerator generator = new AppointmentNumberGenerator(clock);

        assertThat(generator.generate()).isEqualTo("APT-20260829-0001");
        assertThat(generator.generate()).isEqualTo("APT-20260829-0002");

        // Move the same clock instance past midnight and generate again on
        // the very same generator — this is what actually happens to the
        // real Spring singleton bean overnight.
        clock.advanceTo(Instant.parse("2026-08-30T00:05:00Z"));

        assertThat(generator.generate()).isEqualTo("APT-20260830-0001");
    }

    @Test
    @DisplayName("Numbers are unique across many rapid calls (thread-safety smoke test)")
    void generatesUniqueNumbersUnderConcurrentAccess() throws InterruptedException {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-08-29T09:00:00Z"), ZoneId.of("UTC"));
        AppointmentNumberGenerator generator = new AppointmentNumberGenerator(fixedClock);

        int threadCount = 20;
        int callsPerThread = 25;
        java.util.Set<String> results = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < callsPerThread; j++) {
                    results.add(generator.generate());
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assertThat(results).hasSize(threadCount * callsPerThread);
    }
}
