package org.roag.web;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;
import org.roag.pages.ProfilePage;
import org.roag.pages.SubscribersPage;
import org.roag.pages.modules.AbstractNavigationBar;
import org.roag.pages.modules.NavigationItem;

import static org.roag.pages.PageUtils.at;

@ExtendWith(LifecycleTestExtension.class)
public class SubscribersTest {

    @Test
    void addSubscriberTest() {
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        SubscribersPage page = at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName("kindle1")
                        .setEmail("test1@kindle.com")
                        .addRss("http://test.org/feed")
                        .addRss("http://johnwick.alive/feed")
                        .clickSubmit()
                );
        Assertions.assertTrue(page.alertPanel().getText().contains("Success!"));
    }

    @Test
    void editSubscriberTest() {
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        SubscribersPage page = at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName("suspendUser")
                        .setEmail("suspend@test.com")
                        .addRss("http://suspend.com/feed")
                        .clickSubmit())
                .editSubscriber(s -> s
                        .suspendSubscriber("suspendUser")
                        .resumeSubscriber("suspendUser")
                        .removeSubscriber("suspendUser"));
        Assertions.assertTrue(page.alertPanel().getText().contains("Success!"));
    }
}
