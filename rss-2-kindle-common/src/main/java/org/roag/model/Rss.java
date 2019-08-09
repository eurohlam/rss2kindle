
package org.roag.model;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Rss implements Serializable {
    @SerializedName("rss")
    private String rss;
    @SerializedName("status")
    private String status;
    @SerializedName("lastPollingDate")
    private String lastPollingDate;
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("retryCount")
    private short retryCount = 0;

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastPollingDate() {
        return lastPollingDate;
    }

    public void setLastPollingDate(String lastPollingDate) {
        this.lastPollingDate = lastPollingDate;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public short getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(short retryCount) {
        this.retryCount = retryCount;
    }
}
