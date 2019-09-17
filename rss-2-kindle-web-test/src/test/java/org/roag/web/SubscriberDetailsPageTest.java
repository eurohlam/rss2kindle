package org.roag.web;

import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;
import org.roag.pages.ProfilePage;
import org.roag.pages.SubscriberDetailsPage;
import org.roag.pages.SubscribersPage;
import org.roag.pages.modules.NavigationItem;

import static org.roag.pages.PageUtils.at;

@ExtendWith(LifecycleTestExtension.class)
@DisplayName("Subscriptions operations tests")
@Tag("SUBSCRIBERS")
@Tag("SUBSCRIPTIONS")
public class SubscriberDetailsPageTest {

    private Faker faker = new Faker();

    @Test
    @DisplayName("Test operations with subscriptions: activate, deactivate, remove")
    void subscriptionOperationsTest() {
        String subscriber = faker.name().username();
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(subscriber)
                        .setEmail(faker.internet().emailAddress())
                        .addRss("http://" + faker.internet().url())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit())
                .editSubscriber(s -> s.navigateToSubscriberDetails(subscriber));
        at(SubscriberDetailsPage.class)
                .deactivateFirst()
                .activateFirst()
                .removeFirst()
                .alertPanel()
                .shouldHave(Condition.text("Success!"));
    }

    @Test
    @DisplayName("Test add new subscriptions")
    void addNewSubscriptionsTest() {
        String subscriber = faker.name().username();
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(subscriber)
                        .setEmail(faker.internet().emailAddress())
                        .addRss("http://" + faker.internet().url())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit())
                .editSubscriber(s -> s.navigateToSubscriberDetails(subscriber));
        at(SubscriberDetailsPage.class)
                .addSubscriptions("http://" + faker.internet().url(),
                        "http://" + faker.internet().url(),
                        "http://" + faker.internet().url(),
                        "https://" + faker.internet().url(),
                        "https://" + faker.internet().url(),
                        "https://" + faker.internet().url())
                .alertPanel()
                .shouldHave(Condition.text("Success!"));
    }
}
