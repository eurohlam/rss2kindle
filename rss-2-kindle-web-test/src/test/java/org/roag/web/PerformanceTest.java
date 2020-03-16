package org.roag.web;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.roag.pages.*;
import org.roag.pages.modules.NavigationItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.roag.pages.PageUtils.at;
import static org.roag.pages.PageUtils.to;

@DisplayName("Performance tests")
@Tag("PERFORMANCE")
public class PerformanceTest {

    private Faker faker = new Faker();

    @DisplayName("create a user and poll subscriptions for it")
    @ParameterizedTest(name = "{index} ==> username={0}, password={1}, email={2}")
    @MethodSource("userProvider")
    void createUserAndPoll(String user, String password, String email) {
        to(SignUpPage.class)
                .signUpWith(user, password, email);
        to(LoginPage.class).loginWith(user, password);
        at(ProfilePage.class)
                .sidebar()
                .navigateTo(NavigationItem.SUBSCRIBERS);
        at(SubscribersPage.class)
                .addNewSubscriber(s -> s
                        .setName(faker.name().username())
                        .setEmail(faker.internet().emailAddress())
                        .addRss("https://wordpress.org/news/feed")
                        .addRss("https://meduza.io/rss/all")
                        .addRss("https://eurohlam.ru/feed")
                        .clickSubmit()
                )
                .sidebar()
                .navigateTo(NavigationItem.SERVICES);
        at(ServicePage.class).clickPollSubscriptionsImmediately();
    }

    static Stream<Arguments> userProvider() {
        List<Arguments> userList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            userList.add(arguments("testuser" + i, "testuser" + i, "testuser" + i + "@test.org"));
        }
        return userList.stream();
    }
}
