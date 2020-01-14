package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.function.Supplier;

/**
 * Created by eurohlam on 19/08/2019.
 */
public class SideBar extends AbstractNavigationBar {

    private SelenideElement profileLink = selenideElement().$x(".//a[contains(text(),'My Profile')]");
    private SelenideElement subscribersLink = selenideElement().$x(".//a[contains(text(),'Subscribers')]");
    private SelenideElement servicesLink = selenideElement().$x(".//a[contains(text(),'Services')]");

    public SideBar(SelenideElement selector) {
        super(selector);
    }

    public SideBar(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    @Step("Navigate to {item}")
    public void navigateTo(NavigationItem item) {
        switch (item) {
            case PROFILE:
                profileLink.shouldBe(Condition.visible);
                profileLink.click();
                break;
            case SUBSCRIBERS:
                subscribersLink.shouldBe(Condition.visible);
                subscribersLink.click();
                break;
            case SERVICES:
                servicesLink.shouldBe(Condition.visible);
                servicesLink.click();
                break;
            default:
                throw new IllegalArgumentException("Incorrect menu item " + item);
        }
    }
}
