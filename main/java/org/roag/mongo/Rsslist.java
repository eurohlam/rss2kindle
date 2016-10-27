
package org.roag.mongo;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Rsslist {

    @SerializedName("rss")
    private String mRss;
    @SerializedName("status")
    private String mStatus;

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

}
