package lk.zaa.sunrise.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.service.ApiClient;
import lk.zaa.sunrise.client.service.ApiException;
import lk.zaa.sunrise.common.dto.AppointmentRequest;
import lk.zaa.sunrise.common.dto.AppointmentResponse;
import lk.zaa.sunrise.common.dto.DentistDto;
import lk.zaa.sunrise.common.dto.TreatmentTypeDto;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Implements the Register New Appointment sequence diagram from Task A, Figure 4. */
public class RegisterAppointmentController {

    @FXML private TextField patientNameField;
    @FXML private TextField addressField;
    @FXML private TextField contactNumberField;
    @FXML private ComboBox<DentistDto> dentistComboBox;
    @FXML private ComboBox<TreatmentTypeDto> treatmentComboBox;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private TextField appointmentTimeField;
    @FXML private Label messageLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void initialize() {
        try {
            List<DentistDto> dentists = apiClient.listDentists();
            dentistComboBox.getItems().addAll(dentists);
            dentistComboBox.setConverter(displayNameConverter(DentistDto::getName));

            List<TreatmentTypeDto> treatments = apiClient.listTreatments();
            treatmentComboBox.getItems().addAll(treatments);
            treatmentComboBox.setConverter(displayNameConverter(t -> t.getTreatmentName()
                    + " (Rs. " + t.getConsultationFee() + ")"));
        } catch (Exception e) {
            messageLabel.setText("Could not load dentists/treatments. Is sunrise-api running?");
        }
    }

    @FXML
    private void handleRegister() {
        // Client-side validation before anything is sent to the API — this is
        // the "validateInput()" step in the Task A sequence diagram.
        if (patientNameField.getText().isBlank() || addressField.getText().isBlank()
                || contactNumberField.getText().isBlank() || dentistComboBox.getValue() == null
                || treatmentComboBox.getValue() == null || appointmentDatePicker.getValue() == null
                || appointmentTimeField.getText().isBlank()) {
            messageLabel.setText("Please fill in every field before submitting.");
            return;
        }

        LocalTime time;
        try {
            time = LocalTime.parse(appointmentTimeField.getText().trim());
        } catch (DateTimeParseException e) {
            messageLabel.setText("Time must be in HH:mm format, e.g. 14:30.");
            return;
        }

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientName(patientNameField.getText().trim());
        request.setAddress(addressField.getText().trim());
        request.setContactNumber(contactNumberField.getText().trim());
        request.setDentistId(dentistComboBox.getValue().getDentistId());
        request.setTreatmentId(treatmentComboBox.getValue().getTreatmentId());
        request.setAppointmentDate(appointmentDatePicker.getValue());
        request.setAppointmentTime(time);

        try {
            AppointmentResponse response = apiClient.registerAppointment(request);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Registered");
            alert.setHeaderText("Appointment number: " + response.getAppointmentNumber());
            alert.setContentText("Please note this number down — it is needed to search for "
                    + "the appointment or generate a bill.");
            alert.showAndWait();
            handleBack();
        } catch (ApiException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            messageLabel.setText("Could not reach the server. Is sunrise-api running?");
        }
    }

    @FXML
    private void handleBack() {
        SunriseClientApp.navigateTo("/fxml/MainMenu.fxml", 640, 440);
    }

    private <T> StringConverter<T> displayNameConverter(java.util.function.Function<T, String> nameFn) {
        return new StringConverter<>() {
            @Override public String toString(T item) { return item == null ? "" : nameFn.apply(item); }
            @Override public T fromString(String string) { return null; }
        };
    }
}
