package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.repository.DentistRepository;
import lk.zaa.sunrise.api.repository.TreatmentTypeRepository;
import lk.zaa.sunrise.common.dto.DentistDto;
import lk.zaa.sunrise.common.dto.TreatmentTypeDto;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Feeds the dropdown lists on the JavaFX "Register New Appointment" screen
 * (dentist + treatment type), so the receptionist picks from real data
 * instead of retyping it.
 */
@Service
public class ReferenceDataService {

    private final DentistRepository dentistRepository;
    private final TreatmentTypeRepository treatmentTypeRepository;

    public ReferenceDataService(DentistRepository dentistRepository, TreatmentTypeRepository treatmentTypeRepository) {
        this.dentistRepository = dentistRepository;
        this.treatmentTypeRepository = treatmentTypeRepository;
    }

    public List<DentistDto> listDentists() {
        return dentistRepository.findAll().stream()
                .map(d -> new DentistDto(d.getDentistId(), d.getName(), d.getSpecialization()))
                .toList();
    }

    public List<TreatmentTypeDto> listTreatmentTypes() {
        return treatmentTypeRepository.findAll().stream()
                .map(t -> new TreatmentTypeDto(t.getTreatmentId(), t.getTreatmentName(), t.getConsultationFee()))
                .toList();
    }
}
