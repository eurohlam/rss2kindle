package org.roag.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
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
public class SMTPSender {

    final private static Logger logger = LoggerFactory.getLogger(SMTPSender.class);

    private CamelContext context;

    private String uri;
    private String from;

    @Autowired
    public SMTPSender(@Value("${smtp.uri}") String uri, @Value("${smtp.from}") String from) throws Exception{
        this.uri = uri;
        this.from = from;
        this.context = new DefaultCamelContext();
        context.start();
    }

    public void send(String to, String subject, String message) {
        try {
            logger.info("Sending new email via smtp: {}; to: {}; from: {}; subject: {}; body: {}", uri, to, from, subject, message);
            ProducerTemplate template = context.createProducerTemplate();
            Map<String, Object> headers = new HashMap<String, Object>();
            headers.put("to", to);
            headers.put("from", from);
            headers.put("subject", subject);
            headers.put("contentType", "text/plain");
            template.sendBodyAndHeaders(uri, message, headers);
            logger.info("Email has been sent successfully");

//            Thread.sleep(5000); //wait for while before stopping context
//            context.stop();
        } catch (CamelExecutionException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public CamelContext getContext() {
        return context;
    }
}
