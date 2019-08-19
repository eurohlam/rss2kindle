package org.roag.pages.modules;

import com.codeborne.selenide.SelenideElement;

import java.util.function.Supplier;

/**
 * Created by eurohlam on 19/08/2019.
 */
public class SideBar extends AbstractPageModule {

    public SideBar(SelenideElement selector) {
        super(selector);
    }

    public SideBar(Supplier<SelenideElement> selector) {
        super(selector);
    }
}
