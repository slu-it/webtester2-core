package info.novatec.testit.webtester.browser.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import junit.extensions.UnitTest;
import utils.MockFactory;

import info.novatec.testit.webtester.browser.Browser;
import info.novatec.testit.webtester.events.EventSystem;
import info.novatec.testit.webtester.events.browser.ClosedWindowEvent;
import info.novatec.testit.webtester.events.browser.MaximizedWindowEvent;
import info.novatec.testit.webtester.events.browser.RefreshedPageEvent;
import info.novatec.testit.webtester.events.browser.SetWindowPositionEvent;
import info.novatec.testit.webtester.events.browser.SetWindowSizeEvent;
import info.novatec.testit.webtester.pagefragments.PageFragment;


@UnitTest
class CurrentWindowTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WebDriver webDriver;
    @Mock
    EventSystem events;
    @Mock
    Browser browser;

    @InjectMocks
    CurrentWindow cut;

    @BeforeEach
    void initializeMockedBrowser() throws IOException {
        doReturn(webDriver).when(browser).webDriver();
        doReturn(events).when(browser).events();
        doReturn(true).when(events).isEnabled();
    }

    @Test
    @DisplayName("getHandle() returns handle of current window")
    void getHandle_handleOfWindowCanBeReturned() {
        doReturn("fooBar").when(webDriver).getWindowHandle();
        String handle = cut.getHandle();
        assertThat(handle).isEqualTo("fooBar");
    }

    @Test
    @DisplayName("refresh() refreshes current window")
    void refresh_refreshingWindowDelegatesToCorrectWebDriverMethod() {
        cut.refresh();
        verify(webDriver.navigate()).refresh();
    }

    @Test
    @DisplayName("refresh() fires event")
    void refresh_refreshingWindowFiresEvent(ArgumentCaptor<RefreshedPageEvent> eventCaptor) {
        cut.refresh();
        verify(events).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("maximize() maximizes current window")
    void maximize_maximizingWindowDelegatesToCorrectWebDriverMethod() {
        cut.maximize();
        verify(webDriver.manage().window()).maximize();
    }

    @Test
    @DisplayName("maximize() fires event")
    void maximize_maximizingWindowFiresEvent(ArgumentCaptor<MaximizedWindowEvent> eventCaptor) {
        cut.maximize();
        verify(events).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();
    }

    @Test
    @DisplayName("toggleFullScreen() presses F11 to toggle fullscreen")
    void toggleFullScreen_togglingFullScreenSendsF11KeyToCurrentPage() {
        WebElement element = mock(WebElement.class);
        doReturn(element).when(webDriver).findElement(By.tagName("html"));
        cut.toggleFullScreen();
        verify(element).sendKeys(Keys.F11);
    }

    @Test
    @DisplayName("setPosition(int, int) moves window to point")
    void setPosition_settingWindowPositionDelegatesToCorrectWebDriverMethod() {
        cut.setPosition(42, 84);
        verify(webDriver.manage().window()).setPosition(new Point(42, 84));
    }

    @Test
    @DisplayName("setPosition(int, int) fires event")
    void setPosition_settingWindowPositionFiresEvent(ArgumentCaptor<SetWindowPositionEvent> eventCaptor) {
        cut.setPosition(42, 84);
        verify(events).fireEvent(eventCaptor.capture());
        SetWindowPositionEvent event = eventCaptor.getValue();
        assertThat(event.getX()).isEqualTo(42);
        assertThat(event.getY()).isEqualTo(84);
    }

    @Test
    @DisplayName("setSize(int, int) resizes window to dimensions")
    void setSize_settingWindowSizeDelegatesToCorrectWebDriverMethod() {
        cut.setSize(1024, 768);
        verify(webDriver.manage().window()).setSize(new Dimension(1024, 768));
    }

    @Test
    @DisplayName("setSize(int, int) fires event")
    void setSize_settingWindowSizeFiresEvent(ArgumentCaptor<SetWindowSizeEvent> eventCaptor) {
        cut.setSize(1024, 768);
        verify(events).fireEvent(eventCaptor.capture());
        SetWindowSizeEvent event = eventCaptor.getValue();
        assertThat(event.getWidth()).isEqualTo(1024);
        assertThat(event.getHeight()).isEqualTo(768);
    }

    @Test
    @DisplayName("scrollTo(PageFragment) executes scrolling with javascript")
    void scrollTo_correctJavaScriptIsExecuted() {
        JavaScriptExecutor javaScript = mock(JavaScriptExecutor.class);
        doReturn(javaScript).when(browser).javaScript();
        PageFragment fragment = MockFactory.fragment().build();
        cut.scrollTo(fragment);
        verify(javaScript).execute("arguments[0].scrollIntoView(true)", fragment);
    }

    @Test
    @DisplayName("close() closes current window")
    void close_closingWindowDelegatesToCorrectWebDriverMethods() {
        cut.close();
        verify(webDriver).close();
    }

    @Test
    @DisplayName("close() fires event")
    void close_closingWindowFiresEvent(ArgumentCaptor<ClosedWindowEvent> eventCaptor) {
        cut.close();
        verify(events).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();
    }

}
