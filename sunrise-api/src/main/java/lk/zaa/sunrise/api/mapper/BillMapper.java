package lk.zaa.sunrise.api.mapper;

import lk.zaa.sunrise.api.entity.Bill;
import lk.zaa.sunrise.common.dto.BillResponse;
import org.springframework.stereotype.Component;

@Component
public class BillMapper {

    public BillResponse toResponse(Bill bill) {
        BillResponse dto = new BillResponse();
        dto.setBillId(bill.getBillId());
        dto.setAppointmentNumber(bill.getAppointment().getAppointmentNumber());
        dto.setPatientName(bill.getAppointment().getPatient().getName());
        dto.setTreatmentName(bill.getAppointment().getTreatmentType().getTreatmentName());
        dto.setConsultationFee(bill.getAppointment().getTreatmentType().getConsultationFee());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setIssueDate(bill.getIssueDate());
        return dto;
    }
}
