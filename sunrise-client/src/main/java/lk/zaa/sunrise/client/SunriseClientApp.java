package lk.zaa.sunrise.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SunriseClientApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Sunrise Dental Clinic - Appointment & Patient Management System");
        primaryStage.setResizable(true);
        navigateTo("/fxml/Login.fxml", 480, 360);
        primaryStage.show();
    }

    /** Simple central-navigation helper used by every controller to switch screens. */
    public static void navigateTo(String fxmlPath, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(SunriseClientApp.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(SunriseClientApp.class.getResource("/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load screen: " + fxmlPath, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
