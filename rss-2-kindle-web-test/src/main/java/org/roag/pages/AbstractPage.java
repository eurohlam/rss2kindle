package org.roag.pages;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.roag.pages.modules.AlertPanel;
import org.roag.pages.modules.SideBar;
import org.roag.pages.modules.MenuBar;

import static com.codeborne.selenide.Selenide.$;

/**
 * Created by eurohlam on 17/08/19.
 */
abstract class AbstractPage implements Page {

    private final MenuBar menuBar = new MenuBar($("nav#mainNav"));
    private final SideBar sideBar = new SideBar($("aside#sidebar-wrapper"));
    private AlertPanel alertPanel = new AlertPanel($("#alerts_panel"));

    @Step("Get menu bar")
    public MenuBar menubar() {
        menuBar.shouldBe(Condition.visible);
        return menuBar;
    }

    @Step("Get side bar")
    public SideBar sidebar() {
        sideBar.shouldBe(Condition.visible);
        return sideBar;
    }

    @Step("Get alert panel")
    public AlertPanel alertPanel() {
        alertPanel.shouldBe(Condition.visible);
        return alertPanel;
    }
}
