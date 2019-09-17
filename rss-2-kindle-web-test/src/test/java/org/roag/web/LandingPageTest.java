package org.roag.web;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.roag.pages.LandingPage;
import org.roag.pages.modules.NavigationItem;

import static org.roag.pages.PageUtils.at;
import static org.roag.pages.PageUtils.to;

@DisplayName("Landing Page tests")
@Tag("LANDING")
public class LandingPageTest {

    private Faker faker = new Faker();

    @Test
    @DisplayName("Test Contact Me form")
    void contactMeTest() {
        to(LandingPage.class)
                .menubar()
                .navigateTo(NavigationItem.CONTACT);
        at(LandingPage.class)
                .sendMessage(
                        faker.funnyName().name(),
                        faker.internet().emailAddress(),
                        faker.phoneNumber().cellPhone(),
                        faker.chuckNorris().fact());
    }

    @Test
    @DisplayName("Test HOWTO section")
    void howtoTest() {
        to(LandingPage.class)
                .menubar()
                .navigateTo(NavigationItem.HOWTO);
        at(LandingPage.class)
                .clickHow()
                .closeModalForm()
                .clickPrivacyAndSecurity()
                .closeModalForm()
                .clickWhy()
                .closeModalForm();
    }
}
