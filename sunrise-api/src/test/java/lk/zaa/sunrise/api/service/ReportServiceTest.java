package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.exception.ReportUnavailableException;
import lk.zaa.sunrise.api.repository.AppointmentRepository;
import lk.zaa.sunrise.api.repository.BillRepository;
import lk.zaa.sunrise.common.dto.DailyReportResponse;
import lk.zaa.sunrise.common.dto.RevenueReportResponse;
import lk.zaa.sunrise.common.dto.TreatmentRevenueItem;
import lk.zaa.sunrise.common.enums.AppointmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private BillRepository billRepository;
    @InjectMocks private ReportService reportService;

    @Test
    @DisplayName("Daily report combines appointment counts with the revenue function's result")
    void dailyReportAggregatesCorrectly() {
        LocalDate date = LocalDate.of(2026, 8, 29);
        when(appointmentRepository.countByAppointmentDate(date)).thenReturn(5L);
        when(appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.COMPLETED)).thenReturn(3L);
        when(appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.CANCELLED)).thenReturn(1L);
        when(billRepository.getDailyRevenueViaFunction(date)).thenReturn(new BigDecimal("12500.00"));

        DailyReportResponse report = reportService.dailyReport(date);

        assertThat(report.getTotalAppointments()).isEqualTo(5L);
        assertThat(report.getCompletedAppointments()).isEqualTo(3L);
        assertThat(report.getCancelledAppointments()).isEqualTo(1L);
        assertThat(report.getTotalRevenue()).isEqualByComparingTo("12500.00");
    }

    @Test
    @DisplayName("If the SQL function has not been created yet, the daily report fails clearly rather than crashing")
    void dailyReportFailsClearlyWhenFunctionMissing() {
        LocalDate date = LocalDate.of(2026, 8, 29);
        when(appointmentRepository.countByAppointmentDate(date)).thenReturn(0L);
        when(appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.COMPLETED)).thenReturn(0L);
        when(appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.CANCELLED)).thenReturn(0L);
        when(billRepository.getDailyRevenueViaFunction(date))
                .thenThrow(new InvalidDataAccessResourceUsageException("FUNCTION fn_daily_revenue does not exist"));

        assertThatThrownBy(() -> reportService.dailyReport(date))
                .isInstanceOf(ReportUnavailableException.class)
                .hasMessageContaining("db-extras.sql");
    }

    @Test
    @DisplayName("Revenue report sums the per-treatment breakdown into a correct total")
    void revenueReportSumsBreakdownCorrectly() {
        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 31);
        List<TreatmentRevenueItem> breakdown = List.of(
                new TreatmentRevenueItem("Root Canal Treatment", 2L, new BigDecimal("30000.00")),
                new TreatmentRevenueItem("Scaling & Polishing", 5L, new BigDecimal("22500.00")),
                new TreatmentRevenueItem("General Checkup", 8L, new BigDecimal("20000.00"))
        );
        when(billRepository.revenueByTreatment(from, to)).thenReturn(breakdown);

        RevenueReportResponse report = reportService.revenueReport(from, to);

        assertThat(report.getBreakdown()).hasSize(3);
        assertThat(report.getTotalRevenue()).isEqualByComparingTo("72500.00");
    }

    @Test
    @DisplayName("Revenue report with no bills in range returns a zero total, not an error")
    void revenueReportHandlesEmptyRangeGracefully() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 1, 31);
        when(billRepository.revenueByTreatment(from, to)).thenReturn(List.of());

        RevenueReportResponse report = reportService.revenueReport(from, to);

        assertThat(report.getBreakdown()).isEmpty();
        assertThat(report.getTotalRevenue()).isEqualByComparingTo("0.00");
    }
}
