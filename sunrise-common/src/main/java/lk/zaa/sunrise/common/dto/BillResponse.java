package lk.zaa.sunrise.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** What the client receives back from GET /api/bills/{appointmentNumber}. */
public class BillResponse {

    private Long billId;
    private String appointmentNumber;
    private String patientName;
    private String treatmentName;
    private BigDecimal consultationFee;
    private BigDecimal totalAmount;
    private LocalDate issueDate;

    public BillResponse() {
    }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
}
