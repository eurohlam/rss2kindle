package org.roag.web;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.roag.pages.LoginPage;
import org.roag.pages.ProfilePage;

/**
 * Created by eurohlam on 17/08/19.
 */
public class LoginTest {

    @Test
    @DisplayName("Login Test")
    void loginTest() {
        System.out.println("Trying to login");
        LoginPage loginPage = new LoginPage();
        ProfilePage profilePage = loginPage.loginWith("testuser", "testuser");
        Assertions.assertTrue(profilePage.isDisplayed());
    }
}
