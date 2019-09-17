package org.roag.web;

import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;
import org.roag.pages.ProfilePage;
import org.roag.pages.SubscribersPage;
import org.roag.pages.modules.NavigationItem;

import static org.roag.pages.PageUtils.at;

@ExtendWith(LifecycleTestExtension.class)
@DisplayName("Subscribers operations tests")
@Tag("SUBSCRIBERS")
public class SubscribersPageTest {

    private Faker faker = new Faker();

    @Test
    @DisplayName("Testing creation of new subscriber")
    void addSubscriberTest() {
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(faker.name().username())
                        .setEmail(faker.internet().emailAddress())
                        .addRss("http://" + faker.internet().url())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit()
                )
                .alertPanel()
                .shouldHave(Condition.text("Success!"));
    }

    @Test
    @DisplayName("Testing operations with subscriber: suspend, resume and remove")
    void subscriberOperationsTest() {
        String subscriber = faker.name().username();
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(subscriber)
                        .setEmail(faker.internet().emailAddress())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit())
                .editSubscriber(s -> s
                        .suspendSubscriber(subscriber)
                        .resumeSubscriber(subscriber)
                        .removeSubscriber(subscriber))
                .alertPanel()
                .shouldHave(Condition.text("Success!"));
    }


    @Test
    @DisplayName("Test updating subscriber via modal form")
    void updateSubscriberTest() {
        String subscriber = faker.name().username();
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(subscriber)
                        .setEmail(faker.internet().emailAddress())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit())
                .editSubscriber(s -> s
                        .updateSubscriber(subscriber, f -> f
                                .setName(faker.funnyName().name())
                                .addRss("http://" + faker.internet().url())
                                .addRss("http://" + faker.internet().url())
                                .addRss("https://" + faker.internet().url())
                                .addRss("https://" + faker.internet().url())
                                .addRss("http://" + faker.internet().url())
                                .addRss("http://" + faker.internet().url())
                                .addRss("https://" + faker.internet().url())
                                .addRss("http://" + faker.internet().url())
                                .submit()))
                .alertPanel()
                .shouldHave(Condition.text("Success!"));
    }
}
