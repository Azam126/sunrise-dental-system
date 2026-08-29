package lk.zaa.sunrise.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.util.Session;

/**
 * Menu-driven hub required by the brief. Admin-only actions are hidden
 * (not merely disabled) for a Receptionist, reflecting the access
 * permissions set out in Task A Assumption 1.
 */
public class MainMenuController {

    @FXML private Label welcomeLabel;
    @FXML private Button manageStaffButton;

    @FXML
    private void initialize() {
        Session session = Session.getInstance();
        welcomeLabel.setText("Signed in as " + session.getFullName() + " (" + session.getRole() + ")");

        boolean isAdmin = session.isAdministrator();
        manageStaffButton.setVisible(isAdmin);
        manageStaffButton.setManaged(isAdmin);
    }

    @FXML
    private void openRegisterAppointment() {
        SunriseClientApp.navigateTo("/fxml/RegisterAppointment.fxml", 520, 560);
    }

    @FXML
    private void openSearchAppointment() {
        SunriseClientApp.navigateTo("/fxml/SearchAppointment.fxml", 520, 420);
    }

    @FXML
    private void openBill() {
        SunriseClientApp.navigateTo("/fxml/Bill.fxml", 520, 420);
    }

    @FXML
    private void openManageStaff() {
        SunriseClientApp.navigateTo("/fxml/ManageStaff.fxml", 560, 480);
    }

    @FXML
    private void openHelp() {
        SunriseClientApp.navigateTo("/fxml/Help.fxml", 560, 480);
    }

    @FXML
    private void handleExit() {
        Session.getInstance().logout();
        Platform.exit();
    }
}
