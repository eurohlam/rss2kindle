package org.roag.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by eurohlam on 28/09/2017.
 */
@SuppressWarnings("unused")
public class User {

    @SerializedName("_id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("dateCreated")
    private String dateCreated;
    @SerializedName("status")
    private String status;
    @SerializedName("subscribers")
    private List<Subscriber> subscribers;
    @SerializedName("roles")
    private List<Roles> roles;

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

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }
}
