package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

/**
 * Created by eurohlam on 19/08/2019.
 */
public class MenuBar extends AbstractNavigationBar {

    private SelenideElement signInLink = selenideElement().$x(".//a[contains(text(),'Sign In')]");
    private SelenideElement signUpLink = selenideElement().$x(".//a[contains(text(),'Sign Up')]");
    private SelenideElement howToLink = selenideElement().$x(".//a[contains(text(),'Howto')]");
    private SelenideElement aboutLink = selenideElement().$x(".//a[contains(text(),'About')]");
    private SelenideElement contactLink = selenideElement().$x(".//a[contains(text(),'Contact')]");
    private SelenideElement logoutLink = selenideElement().$x(".//a[contains(text(),'Log out')]");

    public MenuBar(SelenideElement selector) {
        super(selector);
    }

    public MenuBar(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    @Step("Navigate to {item}")
    public void navigateTo(NavigationItem item) {
        switch (item) {
            case SIGN_IN:
                signInLink.shouldBe(Condition.visible);
                signInLink.click();
                break;
            case SIGN_UP:
                signUpLink.shouldBe(Condition.visible);
                signUpLink.click();
                break;
            case HOWTO:
                howToLink.shouldBe(Condition.visible);
                howToLink.click();
                break;
            case ABOUT:
                aboutLink.shouldBe(Condition.visible);
                aboutLink.click();
                break;
            case CONTACT:
                contactLink.shouldBe(Condition.visible);
                contactLink.click();
                break;
            case LOGOUT:
                logoutLink.shouldBe(Condition.visible);
                logoutLink.click();
                break;
            default:
                throw new IllegalArgumentException("Incorrect menu item " + item);
        }
    }

}
