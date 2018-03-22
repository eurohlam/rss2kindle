
package org.roag.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Rss
{
    @SerializedName("rss")
    private String mRss;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("lastPollingDate")
    private String lastPollingDate;
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("retryCount")
    private short retryCount = 0;

    public String getRss() {
        return mRss;
    }

    public void setRss(String rss) {
        mRss = rss;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getLastPollingDate()
    {
        return lastPollingDate;
    }

    public void setLastPollingDate(String lastPollingDate)
    {
        this.lastPollingDate = lastPollingDate;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public short getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount(short retryCount)
    {
        this.retryCount = retryCount;
    }
}
