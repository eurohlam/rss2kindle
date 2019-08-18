package org.roag.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.roag.exceptions.UnexpectedPageError;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by eurohlam on 18/08/19.
 */
public final class PageUtils {

    private PageUtils() {
        // static methods only
    }

    @Step("Close WebDriver")
    public static void closeWebDriver() {
        Selenide.close();
    }


    @Step("Navigate To {pageName}")
    private static <T extends Page> T navigateToPage(String pageName, Class<T> pageClass) {
        T page = instantiatePage(pageName, pageClass);
        browseTo(page.getUrl());
        return page;
    }

    @Step("Open URL {url}")
    public static void browseTo(String url) {
        Selenide.open(url);
    }

    public static <T extends Page> T to(Class<T> pageClazz) {
        String pageName = extractPageName(pageClazz);

        if (pageClazz == LoginPage.class) {
            return navigateToPage(pageName, pageClazz);
        } else {
            // navigate to requested page
            navigateToPage(pageName, pageClazz);

            // then make sure we actually arrived by checking with 'at'
            return at(pageClazz);
        }
    }

    public static <T extends Page> T at(Class<T> pageClazz) {
        return verifyAtPage(extractPageName(pageClazz), pageClazz);
    }

    private static <T extends Page> String extractPageName(Class<T> pageClazz) {
        String name = pageClazz.getSimpleName();
        String[] nameParts = StringUtils.splitByCharacterTypeCamelCase(name);
        return StringUtils.join(nameParts, " ");
    }

    @Step("Verify That {pageName} Is Displayed")
    private static <T extends Page> T verifyAtPage(String pageName, Class<T> pageClass) {
        T page = instantiatePage(pageName, pageClass);
        if (!isPageDisplayed(pageName, page)) {
            attachAll();
            throw new UnexpectedPageError(pageName, page);
        }
        return page;
    }

    @Step("Create {pageName} Object")
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private static <T extends Page> T instantiatePage(String pageName, Class<T> pageClass) {
        try {
            return pageClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Evaluate {pageName} isDisplayed Method")
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private static boolean isPageDisplayed(String pageName, Page page) {
        for (int i = 0; i < 10; i++) {
            boolean isDisplayed = page.isDisplayed();

            if (isDisplayed) {
                return true;
            }

            sleep(1000);
        }

        return false;
    }

    public static void attachAll() {
        attachScreenshot();
        attachUrl();
        attachPageSource();
    }

    public static void attachScreenshot() {
        InputStream screenShot = new ByteArrayInputStream(
                ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES));
        Allure.addAttachment("Screenshot", "image/png", screenShot, ".png");
    }

    public static void attachPageSource() {
        String pageSource = WebDriverRunner.getWebDriver().getPageSource();
        Allure.addAttachment("Page source", "text/html", pageSource, ".html");
    }

    public static void attachUrl() {
        Allure.addAttachment("Current url", "text/plain", WebDriverRunner.url());
    }

}
