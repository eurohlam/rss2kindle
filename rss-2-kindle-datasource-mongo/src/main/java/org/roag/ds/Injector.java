package org.roag.ds;

import org.apache.camel.CamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Deprecated
public final class Injector implements ApplicationContextAware
{
    private static final String MODULE_XML_CONFIG_FILE = "META-INF/spring/mongo-context.xml";
    private static final ReadWriteLock REENTRANT_LOCK = new ReentrantReadWriteLock();

    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz)
    {
        return getContext().getBean(clazz);
    }

    public static <T> T getBean(Class<T> clazz, String beanId)
    {
        return getContext().getBean(beanId, clazz);
    }

    public static CamelContext getCamelContext()
    {
        return getBean(CamelContext.class);
    }

    public static ApplicationContext getContext()
    {
        Lock readLock = REENTRANT_LOCK.readLock();
        readLock.lock();
        try
        {
            if (applicationContext == null)
            {
                applicationContext = new ClassPathXmlApplicationContext(MODULE_XML_CONFIG_FILE);
            }
            return applicationContext;
        }
        finally
        {
            readLock.unlock();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        if (applicationContext == null)
        {
            throw new NullPointerException("context is null, it is not allowed");
        }

        Lock writeLock = REENTRANT_LOCK.writeLock();
        writeLock.lock();
        try
        {
            Injector.applicationContext = applicationContext;
        }
        finally
        {
            writeLock.unlock();
        }
    }

    private Injector()
    {
    }
}