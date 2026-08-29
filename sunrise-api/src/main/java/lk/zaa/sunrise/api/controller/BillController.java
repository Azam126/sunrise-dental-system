package lk.zaa.sunrise.api.controller;

import lk.zaa.sunrise.api.service.BillService;
import lk.zaa.sunrise.common.dto.BillResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/{appointmentNumber}")
    public ResponseEntity<BillResponse> generate(@PathVariable String appointmentNumber) {
        return ResponseEntity.ok(billService.generateBill(appointmentNumber));
    }
}
