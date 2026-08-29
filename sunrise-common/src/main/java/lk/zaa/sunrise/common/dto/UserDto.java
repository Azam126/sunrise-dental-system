package lk.zaa.sunrise.common.dto;

import lk.zaa.sunrise.common.enums.Role;

/** Used by Administrator > Manage Staff Accounts. */
public class UserDto {
    private Long userId;
    private String username;
    private String fullName;
    private Role role;

    public UserDto() {
    }

    public UserDto(Long userId, String username, String fullName, Role role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
