package lk.zaa.sunrise.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.service.ApiClient;
import lk.zaa.sunrise.client.service.ApiException;
import lk.zaa.sunrise.common.dto.AppointmentResponse;

/** Implements Use Case UC5/UC6 (Search Appointment «include» View Appointment Details) from Task A. */
public class SearchAppointmentController {

    @FXML private TextField appointmentNumberField;
    @FXML private Label messageLabel;
    @FXML private GridPane resultsPane;
    @FXML private Label patientNameLabel;
    @FXML private Label addressLabel;
    @FXML private Label contactLabel;
    @FXML private Label dentistLabel;
    @FXML private Label treatmentLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void handleSearch() {
        String number = appointmentNumberField.getText().trim();
        if (number.isEmpty()) {
            messageLabel.setText("Enter an appointment number to search.");
            resultsPane.setVisible(false);
            return;
        }

        try {
            AppointmentResponse a = apiClient.searchAppointment(number);
            messageLabel.setText("");
            patientNameLabel.setText(a.getPatientName());
            addressLabel.setText(a.getAddress());
            contactLabel.setText(a.getContactNumber());
            dentistLabel.setText(a.getDentistName());
            treatmentLabel.setText(a.getTreatmentName());
            dateTimeLabel.setText(a.getAppointmentDate() + "  " + a.getAppointmentTime());
            statusLabel.setText(a.getStatus().toString());
            resultsPane.setVisible(true);
        } catch (ApiException e) {
            messageLabel.setText(e.getMessage());
            resultsPane.setVisible(false);
        } catch (Exception e) {
            messageLabel.setText("Could not reach the server. Is sunrise-api running?");
            resultsPane.setVisible(false);
        }
    }

    @FXML
    private void handleBack() {
        SunriseClientApp.navigateTo("/fxml/MainMenu.fxml", 640, 440);
    }
}
