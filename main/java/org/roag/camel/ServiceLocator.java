package org.roag.camel;

import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by eurohlam on 06.10.16.
 */
public class ServiceLocator
{
    public static final Logger logger= LoggerFactory.getLogger(ServiceLocator.class);

    public static final String CONTEXT_CONFIG= "META-INF/spring/camel-context.xml";

    public static final String CAMEL_CONTEXT_BEAN= "MainCamelContext";

    private static volatile ApplicationContext ctx = null;

    public static ApplicationContext getContext()
    {
        ApplicationContext c=ctx;
        if (c == null)
        {
            synchronized (ApplicationContext.class)
            {
                c = ctx;
                if (c==null)
                {
                    ctx = c = new ClassPathXmlApplicationContext(CONTEXT_CONFIG);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("First initialization of context: " + CONTEXT_CONFIG);
                        for (String s : ctx.getBeanDefinitionNames())
                            logger.debug(s);
                    }
                }
            }
        }
        return ctx;
    }

    public static CamelContext getCamelContext()
    {
        return (CamelContext) getContext().getBean(CAMEL_CONTEXT_BEAN);
    }

    public static <T> T getBean(Class<T> _class)
    {
        return getContext().getBean(_class);
    }

}
