package org.roag.camel;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.dataformat.rss.RssConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * Created by eurohlam on 29.06.17.
 */
class RssPollingTask implements Callable<RssPollingTask.PollingTaskResult>
{
    final private static Logger logger = LoggerFactory.getLogger(RssPollingTask.class);

    protected enum TaskStatus {NOT_STARTED, COMPLETED};
    final private ConsumerTemplate consumer;
    final private String rssURI;
    final private String path;
    final private String fileName;

    public RssPollingTask(ConsumerTemplate consumer, String rssURI, String path, String fileName)
    {
        this.consumer = consumer;
        this.rssURI = rssURI;
        this.path = path;
        this.fileName = fileName;
    }

    @Override
    public PollingTaskResult call() throws Exception
    {
        PollingTaskResult result=new PollingTaskResult(rssURI);
        logger.debug("Started polling {}", rssURI);
        SyndFeed feed = null;
        try
        {
            feed = consumer.receiveBody(rssURI, 60000, SyndFeedImpl.class);
        } catch (RuntimeException e)
        {
            logger.error("Polling RSS {} failed due to error: {}, {}", rssURI, e.getMessage(), e);
            throw new ConnectException("Polling RSS " + rssURI +" failed due to error: " + e.getMessage());
        }

        if (feed == null){
            logger.error("Timeout error during the polling {}", rssURI);
            throw new TimeoutException("Timeout error during the polling " + rssURI);
        }
        logger.debug("Finished polling {}.\nTitle: {}.\nDescription: {}", rssURI, feed.getTitle(), feed.getDescription());

        File folder=new File(path);
        if (!folder.exists())
            folder.mkdirs();
        if (folder.exists() && folder.isDirectory())
        {
            String file=folder.getPath() + "/" + fileName;
            OutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(RssConverter.feedToXml(feed).getBytes());
            } catch (IOException e)
            {
                logger.error(e.getMessage(), e);
                throw e;
            } finally {
                if (out != null)
                    out.close();
            }
            logger.debug("Feed {} marshaled into file {}", rssURI, file);
            result.setFileName(file);
            result.setStatus(TaskStatus.COMPLETED);
        }
        return result;
    }

    protected class PollingTaskResult
    {
        private TaskStatus status;
        private String fileName;
        private String rssURI;

        public PollingTaskResult(String rssURI)
        {
            this.rssURI = rssURI;
            this.status = TaskStatus.NOT_STARTED;
        }

        public TaskStatus getStatus()
        {
            return status;
        }

        public void setStatus(TaskStatus status)
        {
            this.status = status;
        }

        public String getFileName()
        {
            return fileName;
        }

        public void setFileName(String fileName)
        {
            this.fileName = fileName;
        }

        public String getRssURI()
        {
            return rssURI;
        }

        public void setRssURI(String rssURI)
        {
            this.rssURI = rssURI;
        }
    }
}
