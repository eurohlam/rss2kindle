package org.roag.camel;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eurohlam on 23/05/18.
 */
@Service
public class SMTPSender extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SMTPSender.class);

    private String uri;
    private String from;

    @Autowired
    public SMTPSender(@Value("${smtp.uri}") String uri, @Value("${smtp.from}") String from) {
        this.uri = uri;
        this.from = from;
    }

    @Override
    public void configure() throws Exception {
        from("direct:sendEmail").log("Sending email to ${header.to} with subject ${header.subject}").to(uri);
    }

    public void send(String host, String port, String to, String subject, String message) {
        logger.info("Sending new email via smtp: {}:{}; to: {}; from: {}; subject: {}; body: {}", host, port, to, from, subject, message);
        ProducerTemplate template = getContext().createProducerTemplate();
        Map<String, Object> headers = new HashMap<>();
        headers.put("to", to);
        headers.put("from", from);
        headers.put("subject", subject);
        headers.put("contentType", "text/plain");
        template.sendBodyAndHeaders("smtp://" + host + ":" + port, message, headers);
        logger.info("Email has been sent successfully");
    }

    public void send(String to, String subject, String message) {
        logger.info("Sending new email via smtp: {}; to: {}; from: {}; subject: {}; body: {}", uri, to, from, subject, message);
        try {
            Endpoint endpoint = getContext().getEndpoint("direct:sendEmail");
            Exchange exchange = endpoint.createExchange();
            Message in = exchange.getIn();
            in.setHeader("subject", subject);
            in.setHeader("to", to);
            in.setHeader("from", from);
            in.setHeader("contentType", "text/plain");
            in.setBody(message);
            Producer producer = endpoint.createProducer();
            producer.start();
            producer.process(exchange);
            logger.info("Email has been sent successfully");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
