package lk.zaa.sunrise.api.repository;

import lk.zaa.sunrise.api.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
