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
@Component("SmtpAttachmentProcessor")
public class SmtpAttachmentProcessor implements org.apache.camel.Processor {

    private final Logger logger = LoggerFactory.getLogger(SmtpAttachmentProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String attachment = exchange.getIn().getHeader("attachment").toString();
        String feed = exchange.getIn().getHeader("mobiFileName").toString();
        logger.debug("SMTP is trying to send attachment {} with name {}", attachment, feed);
        DataHandler att = new DataHandler(new FileDataSource(attachment));
        exchange.getIn().addAttachment(feed, att);
    }
}
