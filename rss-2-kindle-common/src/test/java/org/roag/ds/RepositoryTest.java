package org.roag.ds;

import org.roag.ds.impl.MemorySubscriberRepository;
import org.roag.model.Subscriber;
import org.roag.model.User;
import org.roag.model.UserStatus;
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
    private String TEST_USER = "test_user";
    private String TEST_EMAIL = "test@test.com";
    private String TEST_NAME = "test";
    private String TEST_RSS = "http://testrss.com/feed";

    @Test(groups = {"Functionality Check"})
    public void crudSubscriberTest() throws Exception
    {
        User user=factory.newUser(TEST_USER, "123");
        repository.addUser(user);
        assertNotNull(repository.getUser(TEST_USER));

        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        OperationResult r = repository.addSubscriber(TEST_USER,subscriber);
        assertEquals(r, OperationResult.SUCCESS);

        assertNotNull(repository.getSubscriber(TEST_USER, TEST_EMAIL));
        assertEquals(repository.suspendSubscriber(TEST_USER, TEST_EMAIL), OperationResult.SUCCESS);

        assertTrue(repository.findAll(TEST_USER).size() > 0);
        assertEquals(repository.removeSubscriber(TEST_USER, subscriber), OperationResult.SUCCESS);

        assertEquals(repository.lockUser(TEST_USER), OperationResult.SUCCESS);
        assertEquals(repository.removeUser(TEST_USER), OperationResult.SUCCESS);

    }

    @Test(groups = {"Functionality Check"})
    public void subscriberFactoryTest() throws Exception
    {
        User user=factory.newUser(TEST_USER, "123");
        assertEquals(user.getStatus(), UserStatus.ACTIVE.toString());

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
        repository.addUser(factory.newUser(TEST_USER, "123"));
        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        repository.addSubscriber(TEST_USER, subscriber);
        repository.addSubscriber(TEST_USER, subscriber);
    }

}
