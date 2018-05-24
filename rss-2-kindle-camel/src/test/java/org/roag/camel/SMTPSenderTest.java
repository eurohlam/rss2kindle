package org.roag.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eurohlam on 23/05/18.
 */
public class SMTPSenderTest extends CamelSpringTestSupport
{
    private SMTPSender sender;

    @Override
    protected AbstractApplicationContext createApplicationContext()
    {
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("META-INF/spring/test-spring-context.xml");
        sender = context.getBean(SMTPSender.class);
        return context;
    }

    @Override
    public boolean isCreateCamelContextPerClass()
    {
        return true;
    }

    @Override
    public boolean isUseAdviceWith()
    {
        return false;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception
    {
        CamelContext context = super.createCamelContext();
//        context.addComponent("smtp", context.getComponent("seda"));
        return context;
    }

    public void startCamelContext() throws Exception
    {
        context.start();
    }


    public void stopCamelContext() throws Exception
    {
        context.stop();
    }


    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception
    {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("pop3://localhost?consumer.initialDelay=100&consumer.delay=100").convertBodyTo(String.class).to("mock:result");
            } };
    }

    @Test(groups = {"CamelTesting"})
    public void sendTest() throws Exception
    {
        Mailbox.clearAll();
        MockEndpoint mock = getMockEndpoint("mock:result");
//        mock.expectedHeaderReceived("to", "test@test.com");
//        mock.expectedMessageCount(1);
//        sender.send("test@test.com", "test subject", "Test email");
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("to", "test@test.com");
        headers.put("from", "sender@test.com");
        headers.put("subject", "test subject");
        headers.put("contentType", "text/plain");
        sendBody("smtp://localhost", "Test email", headers);

        assertMailboxReceivedMessages("localhost");
        mock.assertIsSatisfied();
        System.out.println(mock.getReceivedExchanges());
//        assertTrue(mock.getReceivedExchanges().get(0).getIn().getBody(String.class).contains("Test email"));
    }

    protected void assertMailboxReceivedMessages(String name) throws IOException, MessagingException {
        Mailbox mailbox = Mailbox.get(name);
        assertEquals(mailbox.size(), 1, name + " should have received 1 mail");

        Message message = mailbox.get(0);
        assertNotNull(name + " should have received at least one mail!", message.getContent().toString());
        assertEquals("hello world!", message.getContent());
        assertEquals("camel@localhost", message.getFrom()[0].toString());
        boolean found = false;
        for (Address adr : message.getRecipients(Message.RecipientType.TO)) {
            if (name.equals(adr.toString())) {
                found = true;
            }
        }
        assertTrue(found, "Should have found the recpient to in the mail: " + name);
    }
}
