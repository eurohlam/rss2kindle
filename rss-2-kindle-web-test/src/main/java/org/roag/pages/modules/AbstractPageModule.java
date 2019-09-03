package org.roag.pages.modules;

import com.codeborne.selenide.SelenideElement;

import java.util.function.Supplier;

/**
 * Created by eurohlam on 19/08/2019.
 */
public abstract class AbstractPageModule implements PageModule {

    private final Supplier<SelenideElement> selector;

    public AbstractPageModule(final SelenideElement selector) {
        this(() -> selector);
    }

    public AbstractPageModule(final Supplier<SelenideElement> selector) {
        this.selector = selector;
    }

    @Override
    public Supplier<SelenideElement> selector() {
        return this.selector;
    }
}
