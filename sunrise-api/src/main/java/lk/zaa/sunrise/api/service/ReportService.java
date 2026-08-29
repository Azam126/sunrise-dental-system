package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.exception.ReportUnavailableException;
import lk.zaa.sunrise.api.repository.AppointmentRepository;
import lk.zaa.sunrise.api.repository.BillRepository;
import lk.zaa.sunrise.common.dto.DailyReportResponse;
import lk.zaa.sunrise.common.dto.RevenueReportResponse;
import lk.zaa.sunrise.common.dto.TreatmentRevenueItem;
import lk.zaa.sunrise.common.enums.AppointmentStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Backs the Administrator-only "View Clinic Reports" use case (Task A, UC13). */
@Service
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public ReportService(AppointmentRepository appointmentRepository, BillRepository billRepository) {
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    public DailyReportResponse dailyReport(LocalDate date) {
        long total = appointmentRepository.countByAppointmentDate(date);
        long completed = appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.COMPLETED);
        long cancelled = appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.CANCELLED);

        BigDecimal revenue;
        try {
            revenue = billRepository.getDailyRevenueViaFunction(date);
        } catch (DataAccessException e) {
            throw new ReportUnavailableException(
                    "Daily revenue could not be calculated. This report depends on the fn_daily_revenue "
                            + "SQL function in db-extras.sql — has that script been applied to the database? See README.md.");
        }

        return new DailyReportResponse(date, total, completed, cancelled,
                revenue == null ? BigDecimal.ZERO : revenue);
    }

    public RevenueReportResponse revenueReport(LocalDate from, LocalDate to) {
        List<TreatmentRevenueItem> breakdown = billRepository.revenueByTreatment(from, to);
        BigDecimal total = breakdown.stream()
                .map(TreatmentRevenueItem::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new RevenueReportResponse(from, to, total, breakdown);
    }
}
