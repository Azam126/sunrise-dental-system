package lk.zaa.sunrise.api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Reference data holding the consultation fee for each treatment
 * (Task A Assumption 4) so bills are calculated automatically, not typed in.
 */
@Entity
@Table(name = "treatment_types")
public class TreatmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long treatmentId;

    @Column(nullable = false, length = 100)
    private String treatmentName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    protected TreatmentType() {
    }

    public TreatmentType(String treatmentName, BigDecimal consultationFee) {
        this.treatmentName = treatmentName;
        this.consultationFee = consultationFee;
    }

    public Long getTreatmentId() { return treatmentId; }
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
}
