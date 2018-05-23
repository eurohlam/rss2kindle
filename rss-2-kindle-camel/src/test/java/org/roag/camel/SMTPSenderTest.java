package org.roag.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

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
                from("pop3://localhost:25?consumer.initialDelay=100&consumer.delay=100").convertBodyTo(String.class).to("mock:smtp");
            } };
    }

    @Test(groups = {"CamelTesting"})
    public void sendTest() throws Exception
    {
        getMockEndpoint("mock:smtp").expectedBodiesReceived("Test email");
//        getMockEndpoint("smtp://localhost:25").expectedHeaderReceived("to", "test@test.com");
        sender.send("test@test.com", "test subject", "Test email");

        assertMockEndpointsSatisfied();
//        assertNotNull(context.hasEndpoint("seda:inputMessage"));
    }
}
