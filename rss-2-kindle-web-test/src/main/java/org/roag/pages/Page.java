package org.roag.pages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Created by eurohlam on 17/08/19.
 */
public interface Page {

    String getPath();

    default String getUrl() {
        try {
            URL base = new URL(Configuration.baseUrl + getPath());
            return base.toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    default boolean isDisplayed() {
        return Objects.equals(getUrl(), WebDriverRunner.url());
    }
}
