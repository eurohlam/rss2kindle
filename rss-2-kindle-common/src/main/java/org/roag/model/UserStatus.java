package org.roag.model;

/**
 * Created by eurohlam on 28/09/2017.
 */
public enum UserStatus {

    ACTIVE("active"),
    LOCKED("locked");

    private final String value;

    UserStatus(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    public static UserStatus fromValue(String value)
    {
        for (UserStatus s: UserStatus.values()) {
            if (s.toString().equals(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Illegal value of argument: " + value);
    }
}
