package org.roag.camel.polling;


/**
 * Created by eurohlam on 9/08/2019.
 */
public class PollingTaskResult {
    private TaskStatus status;
    private String fileName;
    private String rss;

    PollingTaskResult(String rss) {
        this.rss = rss;
        this.status = TaskStatus.NOT_STARTED;
    }

    TaskStatus getStatus() {
        return status;
    }

    void setStatus(TaskStatus status) {
        this.status = status;
    }

    String getFileName() {
        return fileName;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    String getRss() {
        return rss;
    }

    void setRss(String rss) {
        this.rss = rss;
    }
}
