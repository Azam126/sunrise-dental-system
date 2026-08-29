package lk.zaa.sunrise.api.controller;

import jakarta.validation.Valid;
import lk.zaa.sunrise.api.service.AppointmentService;
import lk.zaa.sunrise.common.dto.AppointmentRequest;
import lk.zaa.sunrise.common.dto.AppointmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> register(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{appointmentNumber}")
    public ResponseEntity<AppointmentResponse> search(@PathVariable String appointmentNumber) {
        return ResponseEntity.ok(appointmentService.findByNumber(appointmentNumber));
    }
}
