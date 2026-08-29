package lk.zaa.sunrise.api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Composition with Appointment (Task A: Appointment "1" *-- "1" Bill): a Bill
 * has no meaning without its Appointment and is only ever created through
 * BillBuilder (Builder pattern) — see pattern/BillBuilder.java.
 */
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @OneToOne(optional = false)
    @JoinColumn(name = "appointment_id", unique = true)
    private Appointment appointment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDate issueDate;

    protected Bill() {
    }

    // Package-private: only BillBuilder is meant to construct a fully-formed Bill.
    Bill(Appointment appointment, BigDecimal totalAmount, LocalDate issueDate) {
        this.appointment = appointment;
        this.totalAmount = totalAmount;
        this.issueDate = issueDate;
    }

    public Long getBillId() { return billId; }
    public Appointment getAppointment() { return appointment; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDate getIssueDate() { return issueDate; }
}
