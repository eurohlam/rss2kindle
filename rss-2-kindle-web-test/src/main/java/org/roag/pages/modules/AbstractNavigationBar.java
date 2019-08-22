package org.roag.pages.modules;

import com.codeborne.selenide.SelenideElement;

import java.util.function.Supplier;

/**
 * Created by eurohlam on 20/08/2019.
 */
public abstract class AbstractNavigationBar extends AbstractPageModule {


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

    public abstract void navigateTo(NavigationItem item);
}
