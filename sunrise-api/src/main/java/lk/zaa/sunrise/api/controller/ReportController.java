package lk.zaa.sunrise.api.controller;

import lk.zaa.sunrise.api.service.ReportService;
import lk.zaa.sunrise.common.dto.DailyReportResponse;
import lk.zaa.sunrise.common.dto.RevenueReportResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

/** Restricted to ROLE_ADMINISTRATOR by SecurityConfig ("/api/admin/**"). */
@RestController
@RequestMapping("/api/admin/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyReportResponse> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.dailyReport(date));
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportResponse> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(reportService.revenueReport(from, to));
    }
}
