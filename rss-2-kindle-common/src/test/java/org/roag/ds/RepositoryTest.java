package org.roag.ds;

import org.roag.ds.impl.MemorySubscriberRepository;
import org.roag.ds.impl.MemoryUserRepository;
import org.roag.model.Roles;
import org.roag.model.Subscriber;
import org.roag.model.User;
import org.roag.model.UserStatus;
import org.roag.service.SubscriberFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by eurohlam on 08.12.16.
 * Test for checking in memory subscriberRepository
 */
public class RepositoryTest
{

    private UserRepository userRepository;
    private SubscriberRepository subscriberRepository;
    private SubscriberFactory factory;
    private static String TEST_USER = "test_user";
    private static String TEST_EMAIL = "test@test.com";
    private static String TEST_NAME = "test";
    private static String TEST_RSS = "http://testrss.com/feed";

    public RepositoryTest()
    {
        userRepository = MemoryUserRepository.getInstance();
        subscriberRepository = MemorySubscriberRepository.getInstance(userRepository);
        factory = new SubscriberFactory();
    }

    @Test(groups = {"Functionality Check"})
    public void crudSubscriberTest() throws Exception
    {
        User user=factory.newUser(TEST_USER, TEST_EMAIL,"123");
        userRepository.addUser(user);
        assertNotNull(userRepository.getUser(TEST_USER));
        assertEquals(userRepository.getUser(TEST_USER).getRoles().iterator().next(), Roles.ROLE_USER, "User has unexpected role.");

        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        OperationResult r = subscriberRepository.addSubscriber(TEST_USER,subscriber);
        assertEquals(r, OperationResult.SUCCESS);

        assertNotNull(subscriberRepository.getSubscriber(TEST_USER, TEST_EMAIL));
        assertEquals(subscriberRepository.suspendSubscriber(TEST_USER, TEST_EMAIL), OperationResult.SUCCESS);

        assertTrue(subscriberRepository.findAllSubscribersByUser(TEST_USER).size() > 0);
        assertEquals(subscriberRepository.removeSubscriber(TEST_USER, subscriber), OperationResult.SUCCESS);

        assertEquals(userRepository.lockUser(TEST_USER), OperationResult.SUCCESS);
        assertEquals(userRepository.removeUser(TEST_USER), OperationResult.SUCCESS);

    }

    @Test(groups = {"Functionality Check"})
    public void subscriberFactoryTest() throws Exception
    {
        User user=factory.newUser(TEST_USER, TEST_EMAIL, "123");
        assertEquals(user.getStatus(), UserStatus.ACTIVE.toString());

        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        assertNotNull(subscriber.getRsslist().get(0).getRss());

        String r = factory.convertPojo2Json(subscriber);
        assertTrue(r.startsWith("{"));

        Subscriber ns = factory.convertJson2Pojo(Subscriber.class, r);
        assertEquals(ns.getEmail(), subscriber.getEmail());

        user.getSubscribers().add(ns);
        user.setLastLogin("01-01-2018");
        String u = factory.convertPojo2Json(user);
        assertTrue(u.startsWith("{"));
        User u1 = factory.convertJson2Pojo(User.class, u);
        u1.setLastLogin("02-02-2018");
        assertEquals(u1.getPreviousLogin(), "01-01-2018", "Incorrect parsing of lastLogin");
    }

    @Test(groups = {"Functionality Check"}, expectedExceptions = {IllegalArgumentException.class})
    public void duplicatedSubscriberTest() throws Exception
    {
        userRepository.addUser(factory.newUser(TEST_USER, TEST_EMAIL, "123"));
        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        subscriberRepository.addSubscriber(TEST_USER, subscriber);
        subscriberRepository.addSubscriber(TEST_USER, subscriber);
    }

}
