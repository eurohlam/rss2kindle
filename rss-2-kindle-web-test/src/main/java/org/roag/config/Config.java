package org.roag.config;

/**
 * Created by eurohlam on 17/08/19.
 */
public final class Config {

    public static Credentials credentials() {
        return new Credentials(System.getProperty("r2k.username"),
                System.getProperty("r2k.password"),
                System.getProperty("r2k.email"));
    }

}
