package lk.zaa.sunrise.client;

/**
 * Plain (non-Application) main class. Some launchers/OSes refuse to run a jar
 * whose Main-Class extends javafx.application.Application directly when the
 * JavaFX runtime isn't already on the module path — going through this
 * indirection avoids that. Prefer `mvn javafx:run` during development.
 */
public class Launcher {
    public static void main(String[] args) {
        SunriseClientApp.main(args);
    }
}
