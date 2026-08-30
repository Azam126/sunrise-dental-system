package lk.zaa.sunrise.api.integration;

import lk.zaa.sunrise.api.entity.Dentist;
import lk.zaa.sunrise.api.entity.TreatmentType;
import lk.zaa.sunrise.api.repository.DentistRepository;
import lk.zaa.sunrise.api.repository.TreatmentTypeRepository;
import lk.zaa.sunrise.common.dto.AppointmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack "does the whole clinic workflow actually work" test: register
 * an appointment, look it up, bill it, and confirm double-booking is
 * refused — all through real HTTP requests, real security, real service
 * logic, and a real (H2) database. This is the system-level counterpart to
 * the unit tests in AppointmentServiceTest / BillServiceTest, which check
 * the same rules in isolation with mocks.
 */
class AppointmentFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired private DentistRepository dentistRepository;
    @Autowired private TreatmentTypeRepository treatmentTypeRepository;

    private Long dentistId;
    private Long treatmentId;
    private String receptionistToken;

    @BeforeEach
    void seedReferenceDataAndLogin() throws Exception {
        Dentist dentist = dentistRepository.save(new Dentist("Dr. Ruwan Fernando", "General Dentistry"));
        TreatmentType treatment = treatmentTypeRepository.save(
                new TreatmentType("General Checkup", new BigDecimal("2500.00")));
        dentistId = dentist.getDentistId();
        treatmentId = treatment.getTreatmentId();
        receptionistToken = loginAndGetToken(RECEPTIONIST_USERNAME, RECEPTIONIST_PASSWORD);
    }

    @Test
    @DisplayName("Register -> Search -> Generate Bill works end-to-end and the bill total matches the treatment fee")
    void fullAppointmentAndBillingFlow() throws Exception {
        AppointmentRequest request = appointmentRequest("Nimal Perera", LocalDate.of(2026, 9, 1), LocalTime.of(10, 0));

        String registerResponse = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + receptionistToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentNumber").exists())
                .andExpect(jsonPath("$.patientName").value("Nimal Perera"))
                .andReturn().getResponse().getContentAsString();

        String appointmentNumber = objectMapper.readTree(registerResponse).get("appointmentNumber").asText();

        mockMvc.perform(get("/api/appointments/" + appointmentNumber)
                        .header("Authorization", "Bearer " + receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dentistName").value("Dr. Ruwan Fernando"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        mockMvc.perform(get("/api/bills/" + appointmentNumber)
                        .header("Authorization", "Bearer " + receptionistToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(2500.00))
                .andExpect(jsonPath("$.appointmentNumber").value(appointmentNumber));
    }

    @Test
    @DisplayName("A second appointment for the same dentist at the same date/time is rejected with 409")
    void doubleBookingIsRejectedEndToEnd() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 2);
        LocalTime time = LocalTime.of(14, 0);

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + receptionistToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(appointmentRequest("Anusha Silva", date, time))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + receptionistToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(appointmentRequest("Kamal Jayasuriya", date, time))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already has an appointment")));
    }

    @Test
    @DisplayName("Registering with a missing patient name is rejected with 400 and a field-level message")
    void registrationValidatesRequiredFields() throws Exception {
        AppointmentRequest request = appointmentRequest("", LocalDate.of(2026, 9, 3), LocalTime.of(9, 0));

        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", "Bearer " + receptionistToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", org.hamcrest.Matchers.hasItem("Patient name is required")));
    }

    @Test
    @DisplayName("Searching for a bill/appointment that was never registered returns 404, not a server error")
    void unknownAppointmentReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/appointments/APT-DOES-NOT-EXIST")
                        .header("Authorization", "Bearer " + receptionistToken))
                .andExpect(status().isNotFound());
    }

    private AppointmentRequest appointmentRequest(String patientName, LocalDate date, LocalTime time) {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientName(patientName);
        request.setAddress("12 Galle Road, Colombo");
        request.setContactNumber("0771234567");
        request.setDentistId(dentistId);
        request.setTreatmentId(treatmentId);
        request.setAppointmentDate(date);
        request.setAppointmentTime(time);
        return request;
    }
}
