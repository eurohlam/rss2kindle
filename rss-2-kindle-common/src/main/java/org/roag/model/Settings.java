
package org.roag.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Settings implements Serializable {

    @SerializedName("starttime")
    private String startTime;
    @SerializedName("timeout")
    private String timeout;

    public String getStarttime() {
        return startTime;
    }

    public void setStarttime(String starttime) {
        this.startTime = starttime;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

}
