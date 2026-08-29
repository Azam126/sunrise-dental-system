package lk.zaa.sunrise.api.repository;

import lk.zaa.sunrise.api.entity.Bill;
import lk.zaa.sunrise.common.dto.TreatmentRevenueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByAppointment_AppointmentNumber(String appointmentNumber);

    /**
     * Calls the MySQL FUNCTION fn_daily_revenue defined in db-extras.sql —
     * the "advanced database feature" required for the Excellent band on
     * Task B. A native query (not JPQL) is required here since JPQL has no
     * syntax for invoking a user-defined SQL function.
     */
    @Query(value = "SELECT fn_daily_revenue(:targetDate)", nativeQuery = true)
    BigDecimal getDailyRevenueViaFunction(@Param("targetDate") LocalDate targetDate);

    /**
     * Revenue grouped by treatment type over a date range — a plain JPQL
     * aggregate query (no stored routine needed), shown alongside the native
     * query above as the two complementary ways the system uses "more
     * sophisticated... queries" against the database.
     */
    @Query("""
            SELECT new lk.zaa.sunrise.common.dto.TreatmentRevenueItem(t.treatmentName, COUNT(b), SUM(b.totalAmount))
            FROM Bill b
                 JOIN b.appointment a
                 JOIN a.treatmentType t
            WHERE a.appointmentDate BETWEEN :from AND :to
            GROUP BY t.treatmentName
            ORDER BY SUM(b.totalAmount) DESC
            """)
    List<TreatmentRevenueItem> revenueByTreatment(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
