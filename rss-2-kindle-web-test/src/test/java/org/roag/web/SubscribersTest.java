package org.roag.web;

import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
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

    private Faker faker = new Faker();

    @Test
    void addSubscriberTest() {
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        SubscribersPage page = at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(faker.name().username())
                        .setEmail(faker.internet().emailAddress())
                        .addRss("http://" + faker.internet().url())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit()
                );
        Assertions.assertTrue(page.alertPanel().getText().contains("Success!"));
    }

    @Test
    void editSubscriberTest() {
        String subscriber = faker.name().username();
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        SubscribersPage page = at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(subscriber)
                        .setEmail(faker.internet().emailAddress())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit())
                .editSubscriber(s -> s
                        .suspendSubscriber(subscriber)
                        .resumeSubscriber(subscriber)
                        .removeSubscriber(subscriber));
        Assertions.assertTrue(page.alertPanel().getText().contains("Success!"));
    }
}
