package lk.zaa.sunrise.api.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Day-to-day clinic staff: register appointments, search, generate bills. */
@Entity
@DiscriminatorValue("RECEPTIONIST")
public class Receptionist extends User {

    protected Receptionist() {
        super();
    }

    public Receptionist(String username, String passwordHash, String fullName) {
        super(username, passwordHash, fullName);
    }
}
