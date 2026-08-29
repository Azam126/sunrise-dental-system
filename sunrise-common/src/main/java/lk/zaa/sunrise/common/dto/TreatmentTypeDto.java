package lk.zaa.sunrise.common.dto;

import java.math.BigDecimal;

public class TreatmentTypeDto {
    private Long treatmentId;
    private String treatmentName;
    private BigDecimal consultationFee;

    public TreatmentTypeDto() {
    }

    public TreatmentTypeDto(Long treatmentId, String treatmentName, BigDecimal consultationFee) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.consultationFee = consultationFee;
    }

    public Long getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Long treatmentId) { this.treatmentId = treatmentId; }
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
}
