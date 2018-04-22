package org.roag.camel;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * Created by eurohlam on 04.11.16.
 */
@Component("SMTPAttachmentProcessor")
public class SMTPAttachmentProcessor implements org.apache.camel.Processor
{

    final private static Logger logger = LoggerFactory.getLogger(SMTPAttachmentProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception
    {
        String attachment = exchange.getIn().getHeader("attachment").toString();
        String feed = exchange.getIn().getHeader("mobiFileName").toString();
        logger.debug("SMTP is trying to send attachment {} with name {}", attachment, feed);
        assert(attachment==null);
        DataHandler att = new DataHandler(new FileDataSource(attachment));
        exchange.getIn().addAttachment(feed, att);
    }
}
