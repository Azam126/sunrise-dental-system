package lk.zaa.sunrise.api.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Full access: everything a Receptionist can do, plus staff/report management. */
@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends User {

    protected Administrator() {
        super();
    }

    public Administrator(String username, String passwordHash, String fullName) {
        super(username, passwordHash, fullName);
    }
}
