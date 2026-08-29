package lk.zaa.sunrise.common.dto;

import java.math.BigDecimal;

/** One row of the revenue-by-treatment breakdown (see RevenueReportResponse). */
public class TreatmentRevenueItem {
    private String treatmentName;
    private Long appointmentCount;
    private BigDecimal revenue;

    public TreatmentRevenueItem() {
    }

    public TreatmentRevenueItem(String treatmentName, Long appointmentCount, BigDecimal revenue) {
        this.treatmentName = treatmentName;
        this.appointmentCount = appointmentCount;
        this.revenue = revenue;
    }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public Long getAppointmentCount() { return appointmentCount; }
    public void setAppointmentCount(Long appointmentCount) { this.appointmentCount = appointmentCount; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
