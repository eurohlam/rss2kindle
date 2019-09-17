package org.roag.pages.modules;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.util.function.Supplier;

public interface PageModule {

    boolean isDisplayed();

    Supplier<SelenideElement> selector();

    default SelenideElement selenideElement() {
        return selector().get();
    }

    default void should(Condition... conditions) {
        selenideElement().should(conditions);
    }

    default void shouldBe(Condition... conditions) {
        selenideElement().shouldBe(conditions);
    }

    default void shouldNotBe(Condition... conditions) {
        selenideElement().shouldNotBe(conditions);
    }

    default void shouldHave(Condition... conditions) {
        selenideElement().shouldHave(conditions);
    }

    default void shouldNotHave(Condition... conditions) {
        selenideElement().shouldNotHave(conditions);
    }
}
