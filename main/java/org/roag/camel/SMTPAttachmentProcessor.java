package org.roag.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Producer;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * Created by eurohlam on 04.11.16.
 */
@Component("SMTPAttachmentProcessor")
public class SMTPAttachmentProcessor implements org.apache.camel.Processor
{

    @Override
    public void process(Exchange exchange) throws Exception
    {
        String attachment = exchange.getIn().getHeader("attachment").toString();
        DataHandler att = new DataHandler(new FileDataSource(attachment));
        exchange.getIn().addAttachment(attachment, att);

    }
}
