package org.roag.camel.polling;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.dataformat.rss.RssConverter;
import org.roag.camel.FeedDataException;
import org.roag.camel.PollingException;
import org.roag.model.Rss;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Created by eurohlam on 9/08/2019.
 */
public class PollingTask implements Callable<PollingTaskResult> {

    private final Logger logger = LoggerFactory.getLogger(PollingTask.class);

    private ConsumerTemplate consumerTemplate;
    private final DateTimeFormatter rssFileNameFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final Rss rss;
    private final String rssUri;
    private final String path;
    private final String fileName;
    private final int lastUpdateCount;
    private final String lastUpdateTimeunit;

    public PollingTask(ConsumerTemplate consumerTemplate, Rss rss, String path, int lastUpdateCount, String lastUpdateTimeunit) {
        this.consumerTemplate = consumerTemplate;
        this.rss = rss;
        this.rssUri = getCamelHttp4Uri(rss.getRss());
        this.path = path;
        this.fileName = rssFileNameFormat.format(LocalDateTime.now());
        this.lastUpdateCount = lastUpdateCount;
        this.lastUpdateTimeunit = lastUpdateTimeunit;
    }

    private String getCamelHttp4Uri(String rss) {
        String rssUri = "http4://" + rss.replaceAll("https?://", "") + "?httpClientConfigurer=httpConfigurer";
        logger.debug("Got URI for polling {}", rssUri);
        return rssUri;
    }

    @Override
    public PollingTaskResult call() throws PollingException, FeedDataException {
        PollingTaskResult result = new PollingTaskResult(rssUri);
        logger.debug("Started polling rss: {} into file: {}", rssUri, path);
        SyndFeed feed;
        try {
            InputStream in = consumerTemplate.receiveBody(rssUri, 60000, InputStream.class);
            SyndFeedInput feedInput = new SyndFeedInput();
            SyndFeed fullFeed = feedInput.build(new XmlReader(in));
            feed = filterEntriesByDate(fullFeed);
        } catch (Exception e) {
            logger.error("Polling rss {} failed due to error: {}, {}", rss.getRss(), e.getMessage(), e);
            throw new PollingException("Polling RSS " + rss.getRss() + " failed due to error: " + e.getMessage(), e);
        }

        if (feed.getEntries().isEmpty()) {
            logger.error("There are no updates for feed {}", rss.getRss());
            throw new FeedDataException("There are no updates for feed " + rss.getRss());
        }

        logger.debug("Finished polling {}.\nTitle: {}.\nDescription: {}", rssUri, feed.getTitle(), feed.getDescription());

        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new PollingException("System error: can not create local folder for data files");
        }

        if (folder.exists() && folder.isDirectory()) {
            String file = folder.getPath() + "/" + fileName;
            try (OutputStream out = new FileOutputStream(file)) {
                out.write(RssConverter.feedToXml(feed).getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new PollingException(e.getMessage(), e);
            }
            logger.debug("Feed {} marshaled into file {}", rss.getRss(), file);
            result.setFileName(file);
            result.setStatus(TaskStatus.COMPLETED);
        }
        return result;
    }

    private SyndFeed filterEntriesByDate(SyndFeed feed) {
        LocalDate lastUpdateDate = LocalDate.now().minus(lastUpdateCount, ChronoUnit.valueOf(lastUpdateTimeunit));
        List<SyndEntry> entries = (List) feed.getEntries()
                .stream()
                .filter(entry -> ((SyndEntry) entry)
                        .getPublishedDate()
                        .toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate().isAfter(lastUpdateDate))
                .collect(Collectors.toList());
        feed.setEntries(entries);
        return feed;
    }

}
