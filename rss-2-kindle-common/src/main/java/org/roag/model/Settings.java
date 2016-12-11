
package org.roag.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Settings {

    @SerializedName("starttime")
    private String mStarttime;
    @SerializedName("timeout")
    private String mTimeout;
//    @SerializedName("to")
//    private String mTo;

    public String getStarttime() {
        return mStarttime;
    }

    public void setStarttime(String starttime) {
        mStarttime = starttime;
    }

    public String getTimeout() {
        return mTimeout;
    }

    public void setTimeout(String timeout) {
        mTimeout = timeout;
    }

}
