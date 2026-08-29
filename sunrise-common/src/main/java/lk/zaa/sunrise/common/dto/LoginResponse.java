package lk.zaa.sunrise.common.dto;

import lk.zaa.sunrise.common.enums.Role;

/** Response body for a successful login: the bearer token plus basic user info. */
public class LoginResponse {

    private String token;
    private String fullName;
    private Role role;

    public LoginResponse() {
    }

    public LoginResponse(String token, String fullName, Role role) {
        this.token = token;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
