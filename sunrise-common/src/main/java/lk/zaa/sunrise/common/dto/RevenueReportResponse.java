package lk.zaa.sunrise.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response for GET /api/admin/reports/revenue — revenue over a date range,
 * broken down by treatment type, so an administrator can see which
 * treatments are driving the clinic's income (decision-making support,
 * as invited by Task B(ii)).
 */
public class RevenueReportResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalRevenue;
    private List<TreatmentRevenueItem> breakdown;

    public RevenueReportResponse() {
    }

    public RevenueReportResponse(LocalDate fromDate, LocalDate toDate, BigDecimal totalRevenue,
                                  List<TreatmentRevenueItem> breakdown) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalRevenue = totalRevenue;
        this.breakdown = breakdown;
    }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public List<TreatmentRevenueItem> getBreakdown() { return breakdown; }
    public void setBreakdown(List<TreatmentRevenueItem> breakdown) { this.breakdown = breakdown; }
}
