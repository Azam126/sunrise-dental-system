package lk.zaa.sunrise.api.service;

import jakarta.transaction.Transactional;
import lk.zaa.sunrise.api.entity.Appointment;
import lk.zaa.sunrise.api.entity.Bill;
import lk.zaa.sunrise.api.exception.ResourceNotFoundException;
import lk.zaa.sunrise.api.mapper.BillMapper;
import lk.zaa.sunrise.api.entity.BillBuilder;
import lk.zaa.sunrise.api.repository.AppointmentRepository;
import lk.zaa.sunrise.api.repository.BillRepository;
import lk.zaa.sunrise.common.dto.BillResponse;
import org.springframework.stereotype.Service;

/** Implements the Generate and Print Bill sequence diagram from Task A, Figure 5. */
@Service
public class BillService {

    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final BillBuilder billBuilder;
    private final BillMapper mapper;

    public BillService(AppointmentRepository appointmentRepository, BillRepository billRepository,
                        BillBuilder billBuilder, BillMapper mapper) {
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
        this.billBuilder = billBuilder;
        this.mapper = mapper;
    }

    @Transactional
    public BillResponse generateBill(String appointmentNumber) {
        // Idempotent: if a bill already exists for this appointment, return it
        // rather than creating a duplicate (composition means at most one Bill
        // per Appointment).
        return billRepository.findByAppointment_AppointmentNumber(appointmentNumber)
                .map(mapper::toResponse)
                .orElseGet(() -> {
                    Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "No appointment found with number " + appointmentNumber));
                    Bill bill = billBuilder.build(appointment);
                    billRepository.save(bill);
                    return mapper.toResponse(bill);
                });
    }
}
