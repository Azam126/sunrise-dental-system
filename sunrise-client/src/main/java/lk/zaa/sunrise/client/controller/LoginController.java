package lk.zaa.sunrise.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.service.ApiClient;
import lk.zaa.sunrise.client.service.ApiException;
import lk.zaa.sunrise.client.util.Session;
import lk.zaa.sunrise.common.dto.LoginResponse;

/** Implements the Login sequence diagram from Task A, Figure 3, on the client side. */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        errorLabel.setText("");
        try {
            LoginResponse response = apiClient.login(username, password);
            Session.getInstance().login(response.getToken(), response.getFullName(), response.getRole());
            SunriseClientApp.navigateTo("/fxml/MainMenu.fxml", 640, 440);
        } catch (ApiException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Could not reach the server. Is sunrise-api running?");
        }
    }
}
