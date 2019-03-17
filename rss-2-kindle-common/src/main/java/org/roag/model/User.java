package org.roag.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;


/**
 * Created by eurohlam on 28/09/2017.
 */
@SuppressWarnings("unused")
public class User {

    @SerializedName("_id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("dateCreated")
    private String dateCreated;
    @SerializedName("dateModified")
    private String dateModified;
    @SerializedName("lastLogin")
    private String lastLogin;
    @SerializedName("previousLogin")
    private String previousLogin;
    @SerializedName("status")
    private String status;
    @SerializedName("subscribers")
    private List<Subscriber> subscribers;
    @SerializedName("roles")
    private Set<Roles> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        if (this.lastLogin != null) {
            this.previousLogin = this.lastLogin;
        }
        this.lastLogin = lastLogin;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public String getPreviousLogin() {
        return previousLogin;
    }

    public void setPreviousLogin(String previousLogin) {
        this.previousLogin = previousLogin;
    }
}
