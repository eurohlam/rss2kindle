package org.roag.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.roag.config.Config;
import org.roag.pages.LoginPage;
import org.roag.pages.ProfilePage;
import org.roag.pages.SignUpPage;
import org.roag.pages.modules.NavigationItem;

import static org.roag.pages.PageUtils.at;
import static org.roag.pages.PageUtils.to;

/**
 * Created by eurohlam on 17/08/19.
 */
@DisplayName("Login Page Tests")
public class LoginTest {

    @Test
    @DisplayName("Login Test")
    @Disabled
    void loginTest() {
        to(SignUpPage.class)
                .signUpWith(Config.credentials())
                .menubar()
                .navigateTo(NavigationItem.SIGN_IN);

        ProfilePage profilePage = at(LoginPage.class).loginWith(Config.credentials());
        Assertions.assertTrue(profilePage.isDisplayed());
    }
}
