package org.roag.web;

import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.roag.junit.LifecycleTestExtension;
import org.roag.pages.ProfilePage;
import org.roag.pages.ServicePage;
import org.roag.pages.SubscribersPage;
import org.roag.pages.modules.NavigationItem;

import static org.roag.pages.PageUtils.at;

@ExtendWith(LifecycleTestExtension.class)
@DisplayName("Services tests")
@Tag("SERVICES")
public class ServicePageTest {

    private Faker faker = new Faker();

    @Test
    @DisplayName("Test poll subscriptions immediately")
    void pollingTest() {
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(faker.name().username())
                        .setEmail(faker.internet().emailAddress())
                        .addRss("http://" + faker.internet().url())
                        .addRss("https://" + faker.internet().url())
                        .clickSubmit())
                .sidebar()
                .navigateTo(NavigationItem.SERVICES);
        at(ServicePage.class)
                .clickPollSubscriptionsImmediately()
                .alertPanel()
                .shouldHave(Condition.text("Success!"));

    }
}
