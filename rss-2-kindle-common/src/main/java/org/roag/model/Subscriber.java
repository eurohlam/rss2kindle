
package org.roag.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Subscriber implements Serializable {

    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("rsslist")
    private List<Rss> rssList;
    @SerializedName("settings")
    private Settings settings;
    @SerializedName("status")
    private String status;
    @SerializedName("dateCreated")
    private String dateCreated;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Rss> getRsslist() {
        return rssList;
    }

    public void setRsslist(List<Rss> rsslist) {
        this.rssList = rsslist;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
