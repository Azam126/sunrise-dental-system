package lk.zaa.sunrise.client.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.service.ApiClient;
import lk.zaa.sunrise.client.service.ApiException;
import lk.zaa.sunrise.common.dto.DailyReportResponse;
import lk.zaa.sunrise.common.dto.RevenueReportResponse;
import lk.zaa.sunrise.common.dto.TreatmentRevenueItem;

/** Implements Use Case UC13 "View Clinic Reports" (Administrator only) from Task A. */
public class ReportsController {

    @FXML private DatePicker dailyDatePicker;
    @FXML private Label dailyMessageLabel;
    @FXML private Label dailyResultLabel;

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Label revenueMessageLabel;
    @FXML private TableView<TreatmentRevenueItem> revenueTable;
    @FXML private TableColumn<TreatmentRevenueItem, String> treatmentColumn;
    @FXML private TableColumn<TreatmentRevenueItem, Long> countColumn;
    @FXML private TableColumn<TreatmentRevenueItem, java.math.BigDecimal> revenueColumn;
    @FXML private Label revenueTotalLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void initialize() {
        treatmentColumn.setCellValueFactory(new PropertyValueFactory<>("treatmentName"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentCount"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
    }

    @FXML
    private void handleDailyReport() {
        if (dailyDatePicker.getValue() == null) {
            dailyMessageLabel.setText("Choose a date first.");
            return;
        }
        try {
            DailyReportResponse r = apiClient.dailyReport(dailyDatePicker.getValue());
            dailyMessageLabel.setText("");
            dailyResultLabel.setText(String.format(
                    "%d appointment(s) — %d completed, %d cancelled. Revenue billed: Rs. %s",
                    r.getTotalAppointments(), r.getCompletedAppointments(), r.getCancelledAppointments(),
                    r.getTotalRevenue()));
        } catch (ApiException e) {
            dailyMessageLabel.setText(e.getMessage());
            dailyResultLabel.setText("");
        } catch (Exception e) {
            dailyMessageLabel.setText("Could not reach the server. Is sunrise-api running?");
            dailyResultLabel.setText("");
        }
    }

    @FXML
    private void handleRevenueReport() {
        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            revenueMessageLabel.setText("Choose both a from-date and a to-date.");
            return;
        }
        try {
            RevenueReportResponse r = apiClient.revenueReport(fromDatePicker.getValue(), toDatePicker.getValue());
            revenueMessageLabel.setText("");
            revenueTable.setItems(FXCollections.observableArrayList(r.getBreakdown()));
            revenueTotalLabel.setText("Total revenue: Rs. " + r.getTotalRevenue());
        } catch (ApiException e) {
            revenueMessageLabel.setText(e.getMessage());
        } catch (Exception e) {
            revenueMessageLabel.setText("Could not reach the server. Is sunrise-api running?");
        }
    }

    @FXML
    private void handleBack() {
        SunriseClientApp.navigateTo("/fxml/MainMenu.fxml", 640, 440);
    }
}
