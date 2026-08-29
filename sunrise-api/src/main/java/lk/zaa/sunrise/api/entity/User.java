package lk.zaa.sunrise.api.entity;

import jakarta.persistence.*;

/**
 * Abstract base for staff accounts. Mirrors the User superclass in the Task A
 * class diagram. Administrator and Receptionist are persisted in a single table
 * with a discriminator column ("role"), which keeps the schema simple while
 * still giving us real Java inheritance (User <|-- Administrator, Receptionist).
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Never stored in plain text — see Task A Assumption 3 (BCrypt hash). */
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String fullName;

    protected User() {
    }

    protected User(String username, String passwordHash, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
