package lk.zaa.sunrise.common.dto;

public class DentistDto {
    private Long dentistId;
    private String name;
    private String specialization;

    public DentistDto() {
    }

    public DentistDto(Long dentistId, String name, String specialization) {
        this.dentistId = dentistId;
        this.name = name;
        this.specialization = specialization;
    }

    public Long getDentistId() { return dentistId; }
    public void setDentistId(Long dentistId) { this.dentistId = dentistId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
