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
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by eurohlam on 29.06.17.
 */
public class RssPollingTask implements Callable<Map<String, String>>
{
    final private static Logger logger = LoggerFactory.getLogger(RssPollingTask.class);

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
    public Map<String, String> call() throws Exception
    {
        Map<String, String> map = new Hashtable<>(1);
        logger.debug("Started polling {}", rssURI);
        SyndFeed feed = consumer.receiveBody(rssURI, SyndFeedImpl.class);
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
            map.put(rssURI, file);
        }
        return map;
    }
}
