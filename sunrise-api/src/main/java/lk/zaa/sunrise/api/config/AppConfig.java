package lk.zaa.sunrise.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

/**
 * Supplies the single Clock bean used across the application (see
 * AppointmentNumberGenerator and BillBuilder). Production always gets the
 * real system clock; tests substitute Clock.fixed(...) to make date-based
 * behaviour deterministic instead of depending on when the test happens to run.
 */
@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
