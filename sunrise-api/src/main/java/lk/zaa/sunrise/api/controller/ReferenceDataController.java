package lk.zaa.sunrise.api.controller;

import lk.zaa.sunrise.api.service.ReferenceDataService;
import lk.zaa.sunrise.common.dto.DentistDto;
import lk.zaa.sunrise.common.dto.TreatmentTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    public ReferenceDataController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/dentists")
    public ResponseEntity<List<DentistDto>> dentists() {
        return ResponseEntity.ok(referenceDataService.listDentists());
    }

    @GetMapping("/treatments")
    public ResponseEntity<List<TreatmentTypeDto>> treatments() {
        return ResponseEntity.ok(referenceDataService.listTreatmentTypes());
    }
}
