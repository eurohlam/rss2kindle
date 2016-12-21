package org.roag.ds;

import org.roag.ds.impl.MemorySubscriberRepository;
import org.roag.model.Subscriber;
import org.roag.service.SubscriberFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by eurohlam on 08.12.16.
 * Test for checking in memory repository
 */
public class RepositoryTest
{

    private SubscriberRepository repository = MemorySubscriberRepository.getInstance();
    private SubscriberFactory factory = new SubscriberFactory();
    private String TEST_EMAIL = "test@test.com";
    private String TEST_NAME = "test";
    private String TEST_RSS = "http://testrss.com/feed";

    @Test(groups = {"Functionality Check"})
    public void crudSubscriberTest() throws Exception
    {
        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        OperationResult r = repository.addSubscriber(subscriber);
        assertEquals(r, OperationResult.SUCCESS);

        assertNotNull(repository.getSubscriber(TEST_EMAIL));
        assertEquals(repository.suspendSubscriber(TEST_EMAIL), OperationResult.SUCCESS);

        assertTrue(repository.findAll().size() > 0);
        assertEquals(repository.removeSubscriber(subscriber), OperationResult.SUCCESS);
    }

    @Test(groups = {"Functionality Check"})
    public void subscriberFactoryTest() throws Exception
    {
        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        assertNotNull(subscriber.getRsslist().get(0).getRss());

        String r = factory.convertPojo2Json(subscriber);
        assertTrue(r.startsWith("{"));

        Subscriber ns = factory.convertJson2Pojo(Subscriber.class, r);
        assertEquals(ns.getEmail(), subscriber.getEmail());
    }

    @Test(groups = {"Functionality Check"}, expectedExceptions = {IllegalArgumentException.class})
    public void duplicatedSubscriberTest() throws Exception
    {
        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        repository.addSubscriber(subscriber);
        repository.addSubscriber(subscriber);
    }

}
