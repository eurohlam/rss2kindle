package org.roag.model;

/**
 * Created by eurohlam on 08.12.16.
 */
public enum RssStatus {
    ACTIVE("active"),
    OFFLINE("offline"),
    DEAD("dead");

    private final String value;

    RssStatus(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return value;
    }

    public static RssStatus fromValue(String value) {
        for (RssStatus s : RssStatus.values()) {
            if (s.toString().equals(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Illegal value of argument: " + value);
    }

}
