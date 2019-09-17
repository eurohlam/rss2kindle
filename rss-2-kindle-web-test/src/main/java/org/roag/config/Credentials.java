package org.roag.config;

/**
 * Created by eurohlam on 18/08/19.
 */
public final class Credentials {

    private final String username;
    private final String password;
    private final String email;

    public Credentials(final String username, final String password, final String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String email() {
        return email;
    }

    @Override
    public String toString() {
        return "username: " + username + " email: " + email;
    }
}
