package org.roag.web;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;
import org.roag.pages.ProfilePage;
import org.roag.pages.SubscriberDetailsPage;
import org.roag.pages.SubscribersPage;
import org.roag.pages.modules.NavigationItem;

import static org.roag.pages.PageUtils.at;

@ExtendWith(LifecycleTestExtension.class)
public class SubscriberDetailsTest {

    private Faker faker = new Faker();

    @Test
    @DisplayName("Testing operations with subscriptions: activate, deactivate, remove")
    void subscriptionOperationsTest() {
        String subscriber = faker.name().username();
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        SubscribersPage page = at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(subscriber)
                        .setEmail(faker.internet().emailAddress())
                        .addRss("http://" + faker.internet().url())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit())
                .editSubscriber(s -> s.subscriberDetails(subscriber));
        at(SubscriberDetailsPage.class)
                .deactivateFirst()
                .activateFirst()
                .removeFirst();
    }
}
