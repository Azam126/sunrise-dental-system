package lk.zaa.sunrise.client.controller;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.service.ApiClient;
import lk.zaa.sunrise.client.service.ApiException;
import lk.zaa.sunrise.common.dto.BillResponse;

/**
 * Implements the Generate and Print Bill sequence diagram from Task A, Figure 5.
 * Named BillScreenController (rather than just BillController) so it reads
 * unambiguously next to lk.zaa.sunrise.api.controller.BillController, the
 * REST endpoint this screen calls — the two live in different modules/JVMs
 * and would have compiled fine either way, but distinct names make the
 * client/API boundary easier to follow when reading the codebase.
 */
public class BillScreenController {

    @FXML private TextField appointmentNumberField;
    @FXML private Label messageLabel;
    @FXML private VBox billPane;
    @FXML private Label billNumberLabel;
    @FXML private Label billPatientLabel;
    @FXML private Label billTreatmentLabel;
    @FXML private Label billFeeLabel;
    @FXML private Label billTotalLabel;
    @FXML private Label billDateLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void handleGenerate() {
        String number = appointmentNumberField.getText().trim();
        if (number.isEmpty()) {
            messageLabel.setText("Enter an appointment number first.");
            billPane.setVisible(false);
            return;
        }

        try {
            BillResponse bill = apiClient.generateBill(number);
            messageLabel.setText("");
            billNumberLabel.setText("Appointment: " + bill.getAppointmentNumber());
            billPatientLabel.setText("Patient: " + bill.getPatientName());
            billTreatmentLabel.setText("Treatment: " + bill.getTreatmentName());
            billFeeLabel.setText("Consultation Fee: Rs. " + bill.getConsultationFee());
            billTotalLabel.setText("Total: Rs. " + bill.getTotalAmount());
            billDateLabel.setText("Issue Date: " + bill.getIssueDate());
            billPane.setVisible(true);
        } catch (ApiException e) {
            messageLabel.setText(e.getMessage());
            billPane.setVisible(false);
        } catch (Exception e) {
            messageLabel.setText("Could not reach the server. Is sunrise-api running?");
            billPane.setVisible(false);
        }
    }

    @FXML
    private void handlePrint() {
        // "Print Bill / Receipt" (Task A UC9) — renders the bill panel to
        // whatever printer the receptionist's machine has configured.
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(billPane.getScene().getWindow())) {
            boolean success = job.printPage(billPane);
            if (success) {
                job.endJob();
            } else {
                new Alert(Alert.AlertType.ERROR, "Printing failed.").showAndWait();
            }
        }
    }

    @FXML
    private void handleBack() {
        SunriseClientApp.navigateTo("/fxml/MainMenu.fxml", 640, 440);
    }
}
