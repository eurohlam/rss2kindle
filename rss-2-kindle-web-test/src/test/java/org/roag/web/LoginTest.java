package org.roag.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.roag.config.Config;
import org.roag.pages.LoginPage;
import org.roag.pages.ProfilePage;
import org.roag.pages.SignUpPage;

import static org.roag.pages.PageUtils.to;

/**
 * Created by eurohlam on 17/08/19.
 */
public class LoginTest {

    @Test
    @DisplayName("Login Test")
    void loginTest() {
        to(SignUpPage.class).signUpWith(Config.credentials());

        System.out.println("Trying to login");
        ProfilePage profilePage = to(LoginPage.class).loginWith(Config.credentials());
        Assertions.assertTrue(profilePage.isDisplayed());
    }
}
