package org.roag.model;

/**
 * Created by eurohlam on 08.12.16.
 */
public enum SubscriberStatus
{
    ACTIVE("active"),
    SUSPENDED("suspended"),
    TERMINATED("terminated");

    private final String value;

    SubscriberStatus(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

    public static SubscriberStatus fromValue(String value)
    {
        for (SubscriberStatus s: SubscriberStatus.values()) {
            if (s.toString().equals(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Illegal value of argument: " + value);
    }

}
