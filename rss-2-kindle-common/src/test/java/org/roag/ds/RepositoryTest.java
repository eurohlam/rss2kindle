package org.roag.ds;

import org.roag.ds.impl.MemorySubscriberRepository;
import org.roag.ds.impl.MemoryUserRepository;
import org.roag.model.*;
import org.roag.service.ModelFactory;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * Created by eurohlam on 08.12.16.
 * Test for checking in memory subscriberRepository
 */
public class RepositoryTest {

    private UserRepository userRepository;
    private SubscriberRepository subscriberRepository;
    private ModelFactory factory;
    private static String TEST_USER = "test_user";
    private static String TEST_EMAIL = "test@test.com";
    private static String TEST_NAME = "test";
    private static String TEST_RSS = "http://testrss.com/feed";

    public RepositoryTest() {
        userRepository = MemoryUserRepository.getInstance();
        subscriberRepository = MemorySubscriberRepository.getInstance(userRepository);
        factory = new ModelFactory();
    }

    @Test(groups = {"Functionality Check"})
    public void crudSubscriberTest() throws Exception {
        User user = factory.newUser(TEST_USER, TEST_EMAIL, "123");
        userRepository.addUser(user);
        assertNotNull(userRepository.getUser(TEST_USER));
        assertEquals(userRepository.getUser(TEST_USER).getRoles().iterator().next(), Roles.ROLE_USER, "User has unexpected role.");

        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        OperationResult r = subscriberRepository.addSubscriber(TEST_USER, subscriber);
        assertEquals(r, OperationResult.SUCCESS);

        assertNotNull(subscriberRepository.getSubscriber(TEST_USER, TEST_EMAIL));
        assertEquals(subscriberRepository.suspendSubscriber(TEST_USER, TEST_EMAIL), OperationResult.SUCCESS);
        assertTrue(SubscriberStatus.fromValue(subscriberRepository.getSubscriber(TEST_USER, TEST_EMAIL).getStatus()) == SubscriberStatus.SUSPENDED);

        assertEquals(subscriberRepository.resumeSubscriber(TEST_USER, TEST_EMAIL), OperationResult.SUCCESS);
        assertTrue(SubscriberStatus.fromValue(subscriberRepository.getSubscriber(TEST_USER, TEST_EMAIL).getStatus()) == SubscriberStatus.ACTIVE);

        Rss rss = new Rss();
        rss.setRss("http://newrss.com");
        rss.setStatus(RssStatus.ACTIVE.toString());
        subscriber.getRsslist().add(rss);
        subscriberRepository.updateSubscriber(TEST_USER, subscriber);
        assertEquals(subscriberRepository.getSubscriber(TEST_USER, TEST_EMAIL).getRsslist().size(), 2);

        assertTrue(subscriberRepository.findAllSubscribersByUser(TEST_USER).size() > 0);
        assertEquals(subscriberRepository.removeSubscriber(TEST_USER, subscriber), OperationResult.SUCCESS);

        assertEquals(userRepository.lockUser(TEST_USER), OperationResult.SUCCESS);
        assertEquals(userRepository.removeUser(TEST_USER), OperationResult.SUCCESS);

    }

    @Test(groups = {"Functionality Check"})
    public void subscriberFactoryTest() throws Exception {
        User user = factory.newUser(TEST_USER, TEST_EMAIL, "123");
        assertEquals(user.getStatus(), UserStatus.ACTIVE.toString());

        Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
        assertNotNull(subscriber.getRsslist().get(0).getRss());

        String r = factory.pojo2Json(subscriber);
        assertTrue(r.startsWith("{"));

        Subscriber ns = factory.json2Pojo(Subscriber.class, r);
        assertEquals(ns.getEmail(), subscriber.getEmail());

        user.getSubscribers().add(ns);
        user.setLastLogin("01-01-2018");
        String u = factory.pojo2Json(user);
        assertTrue(u.startsWith("{"));
        User u1 = factory.json2Pojo(User.class, u);
        u1.setLastLogin("02-02-2018");
        assertEquals(u1.getPreviousLogin(), "01-01-2018", "Incorrect parsing of lastLogin");

        User newUser=factory.newUser(nu -> {nu.setEmail("newuser@test.test"); nu.setUsername("newUser"); nu.setPassword("test");});
        assertEquals(newUser.getUsername(), "newUser", "Incorrect user created via lambda");
        assertEquals(newUser.getPassword(), "test", "Incorrect user created via lambda");
        Subscriber subscr = factory.newSubscriber(sub -> {sub.setEmail("sub@sub.com"); sub.setName("sub"); sub.setStatus(SubscriberStatus.ACTIVE.toString());});
        assertEquals(subscr.getName(), "sub", "Incorrect subscriber created via lambda");
        assertEquals(subscr.getEmail(), "sub@sub.com", "Incorrect subscriber created via lambda");
    }

    @Test(groups = {"Functionality Check"}, expectedExceptions = {IllegalArgumentException.class})
    public void duplicatedSubscriberTest() throws Exception {
        userRepository.addUser(factory.newUser(TEST_USER, TEST_EMAIL, "123"));
        try {
            Subscriber subscriber = factory.newSubscriber(TEST_EMAIL, TEST_NAME, TEST_RSS);
            subscriberRepository.addSubscriber(TEST_USER, subscriber);
            subscriberRepository.addSubscriber(TEST_USER, subscriber);
        } finally {
            userRepository.removeUser(TEST_USER);
        }
    }

    @Test(groups = {"Functionality Check"})
    public void duplicatedUserRoleTest() throws Exception {
        userRepository.addUser(factory.newUser(TEST_USER, TEST_EMAIL, "123"));
        userRepository.assignRole(TEST_USER, Roles.ROLE_ADMIN);
        assertTrue(userRepository.getUser(TEST_USER).getRoles().contains(Roles.ROLE_ADMIN));
        assertTrue(userRepository.assignRole(TEST_USER, Roles.ROLE_ADMIN) == OperationResult.DUPLICATED);
        userRepository.removeUser(TEST_USER);
    }

    @Test(groups = {"Functionality Check"})
    public void findAllByConditionTest() throws Exception {
        for (int i = 0; i < 10000; i++) {
            User user = factory.newUser(TEST_USER + i, i + TEST_EMAIL, "123");
            if (i % 2 == 0) {
                user.setStatus(UserStatus.LOCKED.toString());
            }
            if (i % 10 == 0) {
                user.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_ADMIN)));
            }
            userRepository.addUser(user);
        }
        Map<String, String> conditions = new HashMap<>();
        conditions.put("status", UserStatus.LOCKED.toString());
        conditions.put("roles", "ROLE_USER");
        userRepository.findAll(conditions).forEach(user -> assertTrue(UserStatus.fromValue(user.getStatus()) == UserStatus.LOCKED));
    }


}
