package org.roag.pages.modules;

import com.codeborne.selenide.SelenideElement;

import java.util.function.Supplier;

/**
 * Created by eurohlam on 20/08/2019.
 */
public abstract class AbstractNavigationBar extends AbstractPageModule {

    public enum MenuItem {
        SIGN_IN,
        SIGN_UP,
        HOWTO,
        ABOUT,
        CONTACT,
        PROFILE,
        SUBSCRIBERS,
        SERVICES,
        LOGOUT
    }

    AbstractNavigationBar(SelenideElement selector) {
        super(selector);
    }

    AbstractNavigationBar(Supplier<SelenideElement> selector) {
        super(selector);
    }

    @Override
    public boolean isDisplayed() {
        return selenideElement().isDisplayed();
    }

    public abstract void navigateTo(MenuItem item);
}
