package integration.browser.operations;

import static info.novatec.testit.webtester.conditions.Conditions.visibleText;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import integration.BaseIntegrationTest;

import info.novatec.testit.webtester.browser.operations.Windows;
import info.novatec.testit.webtester.browser.operations.Windows.Window;
import info.novatec.testit.webtester.pagefragments.Div;
import info.novatec.testit.webtester.pagefragments.Link;
import info.novatec.testit.webtester.pagefragments.annotations.IdentifyUsing;
import info.novatec.testit.webtester.pages.Page;


public class WindowsIntegrationTest extends BaseIntegrationTest {

    Windows cut = new Windows(browser());
    String mainWindowHandle;

    @Override
    protected String getHTMLFilePath() {
        return "html/browser/multi-window/index.html";
    }

    @Before
    public final void rememberMainWindow() {
        mainWindowHandle = browser().webDriver().getWindowHandle();
    }

    @After
    public final void closeOtherWindows() {
        WebDriver webDriver = browser().webDriver();
        webDriver.getWindowHandles()
            .stream()
            .filter(handle -> !mainWindowHandle.equals(handle))
            .forEach(handle -> webDriver.switchTo().window(handle).close());
        webDriver.switchTo().window(mainWindowHandle);
    }

    @Test
    public void windowHandlesAreRead() {

        IndexPage page = create(IndexPage.class);
        page.linkOne().click();
        page.linkTwo().click();

        Set<String> handles = cut.getHandles();
        assertThat(handles).hasSize(3);
        assertThat(handles).contains(mainWindowHandle);

    }

    @Test
    public void focusCanBeSetToOtherWindow() {

        IndexPage page = create(IndexPage.class);
        page.linkOne().click();
        page.linkTwo().click();

        cut.findFirst(window -> window.hasPageTitleContaining("#1")).setFocus();

        String message = create(MessagePage.class).message().getVisibleText();
        assertThat(message).isEqualTo("This is the first Window!");

    }

    @Test
    public void canCheckIfWindowContainsCertainElement() {

        IndexPage page = create(IndexPage.class);
        page.linkOne().click();
        page.linkTwo().click();

        Window actualWindow = cut.findFirst(window -> window.hasElement("#uniqueToTwo"));
        assertThat(actualWindow.getPageTitle()).isEqualTo("Window Page #2");

    }

    @Test
    public void canCheckIfWindowContainsCertainElementWithCondition() {

        IndexPage page = create(IndexPage.class);
        page.linkOne().click();
        page.linkTwo().click();

        Window actualWindow =
            cut.findFirst(window -> window.hasElementMatching("#msg", visibleText("This is the second Window!")));
        assertThat(actualWindow.getPageTitle()).isEqualTo("Window Page #2");

    }

    @Test
    public void canExecuteSomethingWithinTheWindow() {

        create(IndexPage.class).linkOne().click();

        StringBuilder message = new StringBuilder();
        cut.findFirst(window -> window.getPageTitle().equals("Window Page #1"))
            .executeWithin(browser -> message.append(browser.find("#msg").getVisibleText()));

        assertThat(message).hasToString("This is the first Window!");

    }

    public interface IndexPage extends Page {

        @IdentifyUsing("#one")
        Link linkOne();

        @IdentifyUsing("#two")
        Link linkTwo();

    }

    public interface MessagePage extends Page {

        @IdentifyUsing("#msg")
        Div message();
    }

}
