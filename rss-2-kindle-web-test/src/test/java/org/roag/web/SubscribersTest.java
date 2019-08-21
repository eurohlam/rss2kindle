package org.roag.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;
import org.roag.pages.ProfilePage;
import org.roag.pages.SubscribersPage;
import org.roag.pages.modules.AbstractNavigationBar;

import static org.roag.pages.PageUtils.at;

@ExtendWith(LifecycleTestExtension.class)
public class SubscribersTest {

    @Test
    void addSubscriberTest() {
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(AbstractNavigationBar.MenuItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                                .setName("kindle")
                                .setEmail("test@kindle.com")
                                .addRss("http://test.org/feed")
                                .addRss("http://johnwick.alive/feed")
                                .clickSubmit()
                );
    }
}
