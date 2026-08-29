package lk.zaa.sunrise.client.util;

import lk.zaa.sunrise.common.enums.Role;

/**
 * SINGLETON PATTERN (client-side).
 *
 * Exactly one signed-in user exists per running instance of the desktop
 * client, so the JWT token and role are held in a single, globally-reachable
 * instance rather than being threaded through every controller's
 * constructor. This is a separate, independently-justified use of Singleton
 * from AppointmentNumberGenerator on the server — the two are not the same
 * object and do not share state (they run in different JVMs entirely).
 */
public final class Session {

    private static final Session INSTANCE = new Session();

    private String token;
    private String fullName;
    private Role role;

    private Session() {
    }

    public static Session getInstance() {
        return INSTANCE;
    }

    public void login(String token, String fullName, Role role) {
        this.token = token;
        this.fullName = fullName;
        this.role = role;
    }

    public void logout() {
        this.token = null;
        this.fullName = null;
        this.role = null;
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    public boolean isAdministrator() {
        return role == Role.ADMINISTRATOR;
    }

    public String getToken() { return token; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }
}
