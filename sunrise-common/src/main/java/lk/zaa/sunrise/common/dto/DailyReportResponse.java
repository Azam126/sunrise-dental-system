package lk.zaa.sunrise.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response for GET /api/admin/reports/daily — gives an administrator a
 * same-day snapshot: how busy the clinic was and how much was billed.
 */
public class DailyReportResponse {
    private LocalDate date;
    private long totalAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private BigDecimal totalRevenue;

    public DailyReportResponse() {
    }

    public DailyReportResponse(LocalDate date, long totalAppointments, long completedAppointments,
                                long cancelledAppointments, BigDecimal totalRevenue) {
        this.date = date;
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.totalRevenue = totalRevenue;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }
    public long getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; }
    public long getCancelledAppointments() { return cancelledAppointments; }
    public void setCancelledAppointments(long cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
