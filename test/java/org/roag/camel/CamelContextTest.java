package org.roag.camel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by eurohlam on 06.10.16.
 */
public class CamelContextTest
{
    final public static Logger logger= LoggerFactory.getLogger(ServiceLocator.class);

    @Test(groups = {"checking"})
    public void contextLoaderTest()
    {
        logger.info("Run spring context testing");
        assertTrue("Bean MongoHelper has not loaded from context", ServiceLocator.getContext().containsBeanDefinition("mongoHelper"));
    }


}
