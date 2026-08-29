package lk.zaa.sunrise.api.entity;

import jakarta.persistence.*;

/** Reference data — not a system login (Task A Assumption 6). */
@Entity
@Table(name = "dentists")
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dentistId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String specialization;

    protected Dentist() {
    }

    public Dentist(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }

    public Long getDentistId() { return dentistId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
