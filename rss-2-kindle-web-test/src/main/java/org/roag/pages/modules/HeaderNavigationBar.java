package org.roag.pages.modules;

import com.codeborne.selenide.SelenideElement;

import java.util.function.Supplier;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 19/08/2019.
 */
public class HeaderNavigationBar extends AbstractPageModule {

    private SelenideElement signInLink = selenideElement().$x("//a");
    private SelenideElement signUpLink = selenideElement().$("");
    private SelenideElement howToLink = selenideElement().$("");
    private SelenideElement aboutLink = selenideElement().$("");
    private SelenideElement contactLink = selenideElement().$("");
    private SelenideElement logoutLink = selenideElement().$("");

    public HeaderNavigationBar(SelenideElement selector) {
        super(selector);
    }

    public HeaderNavigationBar(Supplier<SelenideElement> selector) {
        super(selector);
    }
}
