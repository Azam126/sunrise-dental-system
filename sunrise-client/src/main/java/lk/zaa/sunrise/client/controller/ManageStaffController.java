package lk.zaa.sunrise.client.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.zaa.sunrise.client.SunriseClientApp;
import lk.zaa.sunrise.client.service.ApiClient;
import lk.zaa.sunrise.client.service.ApiException;
import lk.zaa.sunrise.common.dto.NewUserRequest;
import lk.zaa.sunrise.common.dto.UserDto;
import lk.zaa.sunrise.common.enums.Role;

/**
 * Administrator-only screen (Task A UC12 "Manage Staff Accounts"). The API
 * enforces this restriction independently via ROLE_ADMINISTRATOR on
 * /api/admin/**, so even a modified client cannot bypass it.
 */
public class ManageStaffController {

    @FXML private TableView<UserDto> usersTable;
    @FXML private TableColumn<UserDto, String> usernameColumn;
    @FXML private TableColumn<UserDto, String> fullNameColumn;
    @FXML private TableColumn<UserDto, Role> roleColumn;

    @FXML private TextField newUsernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private TextField newFullNameField;
    @FXML private ComboBox<Role> newRoleComboBox;
    @FXML private Label messageLabel;

    private final ApiClient apiClient = new ApiClient();

    @FXML
    private void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        newRoleComboBox.setItems(FXCollections.observableArrayList(Role.values()));

        loadUsers();
    }

    private void loadUsers() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(apiClient.listUsers()));
        } catch (Exception e) {
            messageLabel.setText("Could not load staff list. Is sunrise-api running?");
        }
    }

    @FXML
    private void handleAddUser() {
        if (newUsernameField.getText().isBlank() || newPasswordField.getText().isBlank()
                || newFullNameField.getText().isBlank() || newRoleComboBox.getValue() == null) {
            messageLabel.setText("Please fill in every field before adding a staff account.");
            return;
        }

        NewUserRequest request = new NewUserRequest();
        request.setUsername(newUsernameField.getText().trim());
        request.setPassword(newPasswordField.getText());
        request.setFullName(newFullNameField.getText().trim());
        request.setRole(newRoleComboBox.getValue());

        try {
            apiClient.createUser(request);
            messageLabel.setText("");
            newUsernameField.clear();
            newPasswordField.clear();
            newFullNameField.clear();
            newRoleComboBox.setValue(null);
            loadUsers();
        } catch (ApiException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            messageLabel.setText("Could not reach the server. Is sunrise-api running?");
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsers();
    }

    @FXML
    private void handleBack() {
        SunriseClientApp.navigateTo("/fxml/MainMenu.fxml", 640, 440);
    }
}
