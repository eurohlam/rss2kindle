package org.roag.exceptions;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ErrorMessages;
import org.roag.pages.Page;

/**
 * Created by eurohlam on 18/08/19.
 */
public class UnexpectedPageError extends AssertionError {
    private static final long serialVersionUID = 1L;
    private final String detail;

    public UnexpectedPageError(final String expectedPageName, final Page expectedPage) {
        super("Unexpected Page: current page should be " + expectedPageName);
        this.detail = ErrorMessages.screenshot(WebDriverRunner.driver())
                + System.lineSeparator()
                + "Page url: " + expectedPage.getUrl() + System.lineSeparator()
                + "Page path: " + expectedPage.getPath();
    }

    @Override
    public String toString() {
        return this.getLocalizedMessage() + this.detail;
    }
}
