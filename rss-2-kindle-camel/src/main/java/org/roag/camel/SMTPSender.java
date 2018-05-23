package org.roag.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eurohlam on 23/05/18.
 */
@Service
public class SMTPSender
{

    private String uri;
    private String from;

    @Autowired
    public SMTPSender(@Value("${smtp.uri}") String uri, @Value("${smtp.from}") String from) {
        this.uri = uri;
        this.from = from;
    }

    public void send(String to, String subject, String message) {
        try {
           CamelContext context = new DefaultCamelContext();
 /*           context.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("seda:inputMessage").to(uri);
                }
            });*/
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            Map<String, Object> headers = new HashMap<String, Object>();
            headers.put("to", to);
            headers.put("from", from);
            headers.put("subject", subject);
            headers.put("contentType", "text/plain");
            template.sendBodyAndHeaders(uri, message, headers);
            Thread.sleep(10000);

            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
