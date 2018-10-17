package org.roag.model;

/**
 * Created by eurohlam on 1/11/2017.
 */
public enum Roles {
    ROLE_USER ("ROLE_USER"),
    ROLE_ADMIN ("ROLE_ADMIN");

    private final String value;

    Roles(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
