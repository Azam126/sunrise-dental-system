package lk.zaa.sunrise.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.util.Session;

/** Implements Use Case UC10 "Access Help Guide" — step-by-step instructions for new staff. */
public class HelpController {

    @FXML private TextArea helpTextArea;

    @FXML
    private void initialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("SUNRISE DENTAL CLINIC — STAFF QUICK GUIDE\n");
        sb.append("==========================================\n\n");
        sb.append("1. LOGGING IN\n");
        sb.append("   Enter the username and password given to you by the clinic administrator.\n\n");
        sb.append("2. REGISTERING A NEW APPOINTMENT\n");
        sb.append("   From the Main Menu, select 'Register New Appointment'. Fill in the patient's\n");
        sb.append("   name, address and contact number, choose a dentist and treatment type, and\n");
        sb.append("   pick a date and time. The system will show you a unique appointment number\n");
        sb.append("   when done — write it on the patient's file, as it is needed for billing.\n\n");
        sb.append("3. SEARCHING FOR AN APPOINTMENT\n");
        sb.append("   Select 'Search / View Appointment' and type in the appointment number.\n\n");
        sb.append("4. GENERATING AND PRINTING A BILL\n");
        sb.append("   Select 'Generate & Print Bill', enter the appointment number, then click\n");
        sb.append("   'Generate Bill'. Once the bill appears, click 'Print' to print a receipt.\n\n");
        sb.append("5. MANAGING STAFF ACCOUNTS (Administrators only)\n");
        sb.append("   Administrators can add or remove staff logins from 'Manage Staff Accounts'.\n\n");
        sb.append("6. EXITING\n");
        sb.append("   Select 'Exit' from the Main Menu to safely close the application.\n");

        if (Session.getInstance().isAdministrator()) {
            sb.append("\nYou are signed in as an Administrator, so the staff management screen\n");
            sb.append("is available to you from the Main Menu.\n");
        }

        helpTextArea.setText(sb.toString());
    }

    @FXML
    private void handleBack() {
        SunriseClientApp.navigateTo("/fxml/MainMenu.fxml", 640, 440);
    }
}
