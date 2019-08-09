package org.roag.camel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by eurohlam on 23/05/18.
 */
@Service
public class SmtpSender {

    private final Logger logger = LoggerFactory.getLogger(SmtpSender.class);

    private String protocol;
    private String host;
    private String port;
    private String from;
    private String username;
    private String password;

    @Autowired
    public SmtpSender(@Value("${smtp.protocol}") String protocol, @Value("${smtp.host}") String host,
                      @Value("${smtp.port}") String port, @Value("${smtp.from}") String from,
                      @Value("${smtp.username}") String username, @Value("${smtp.password}") String password) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.from = from;
        this.username = username;
        this.password = password;
    }

    public void send(String host, String port, String to, String subject,
                     String from, String fromPersonal, String message) throws Exception {

        logger.info("Sending new email via smtp: {}; to: {}; from: {}; subject: {}; body: {}", host, to, from, subject, message);
        Properties props = new Properties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from, fromPersonal));
        msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");

        // Send the message.
        try (Transport transport = session.getTransport()) {
            transport.connect(host, username, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            logger.info("Email has been sent successfully");
        } catch (Exception ex) {
            logger.error("The email was not sent.");
            logger.error(ex.getMessage(), ex);
        }
    }

    public void send(String to, String subject, String message) throws Exception {
        send(to, subject, from, message);
    }

    public void send(String to, String subject, String from, String fromPersonal, String message) throws Exception {
        send(host, port, to, subject, from, fromPersonal, message);
    }

    public void send(String to, String subject, String from, String message) throws Exception {
        send(host, port, to, subject, from, null, message);
    }
}
