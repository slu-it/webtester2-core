package info.novatec.testit.webtester.browser.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import junit.extensions.UnitTest;
import utils.MockFactory;

import info.novatec.testit.webtester.browser.Browser;
import info.novatec.testit.webtester.events.EventSystem;
import info.novatec.testit.webtester.events.browser.SwitchedToDefaultContentEvent;
import info.novatec.testit.webtester.events.browser.SwitchedToFrameEvent;
import info.novatec.testit.webtester.events.browser.SwitchedToWindowEvent;
import info.novatec.testit.webtester.pagefragments.PageFragment;


@UnitTest
class FocusSetterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WebDriver webDriver;
    @Mock
    EventSystem events;
    @Mock
    Browser browser;

    @InjectMocks
    FocusSetter cut;

    @BeforeEach
    void init() throws IOException {
        doReturn(webDriver).when(browser).webDriver();
        doReturn(events).when(browser).events();
        doReturn(true).when(events).isEnabled();
    }

    @Test
    @DisplayName("onFrame(int) switches to frame by given index")
    void focusOnFrameByIndex_invokesCorrectLocatorMethod() {
        cut.onFrame(42);
        verify(webDriver.switchTo()).frame(42);
    }

    @Test
    @DisplayName("onFrame(int) fires event")
    void focusOnFrameByIndex_firesEvent(ArgumentCaptor<SwitchedToFrameEvent> eventCaptor) {
        cut.onFrame(42);
        verify(events).fireEvent(eventCaptor.capture());
        SwitchedToFrameEvent event = eventCaptor.getValue();
        assertThat(event.getTarget()).isEqualTo("42");
    }

    @Test
    @DisplayName("onFrame(int) unknown index throws exception")
    void focusOnFrameByIndex_unknownIndexThrowsException() {

        NoSuchFrameException exception = mock(NoSuchFrameException.class);
        when(webDriver.switchTo().frame(42)).thenThrow(exception);

        expectThrows(NoSuchFrameException.class, () -> {
            cut.onFrame(42);
        });

        verify(events).fireExceptionEvent(exception);
        verifyNoMoreInteractions(events);

    }

    @Test
    @DisplayName("onFrame(String) switches to frame by given name or ID")
    void focusOnFrameByNameOrId_invokesCorrectLocatorMethod() {
        cut.onFrame("fooBar");
        verify(webDriver.switchTo()).frame("fooBar");
    }

    @Test
    @DisplayName("onFrame(String) fires event")
    void focusOnFrameByNameOrId_firesEvent(ArgumentCaptor<SwitchedToFrameEvent> eventCaptor) {
        cut.onFrame("fooBar");
        verify(events).fireEvent(eventCaptor.capture());
        SwitchedToFrameEvent event = eventCaptor.getValue();
        assertThat(event.getTarget()).isEqualTo("fooBar");
    }

    @Test
    @DisplayName("onFrame(String) unknown name or ID throws exception")
    void focusOnFrameByNameOrId_unknownNameOrIdThrowsException() {

        NoSuchFrameException exception = mock(NoSuchFrameException.class);
        when(webDriver.switchTo().frame("fooBar")).thenThrow(exception);

        expectThrows(NoSuchFrameException.class, () -> {
            cut.onFrame("fooBar");
        });

        verify(events).fireExceptionEvent(exception);
        verifyNoMoreInteractions(events);

    }

    @Test
    @DisplayName("onFrame(PageFragment) switches to the given frame")
    void focusOnFrameByPageFragment_invokesCorrectLocatorMethod() {
        PageFragment fragment = MockFactory.fragment().build();
        cut.onFrame(fragment);
        verify(webDriver.switchTo()).frame(fragment.webElement());
    }

    @Test
    @DisplayName("onFrame(PageFragment) fires event")
    void focusOnFrameByPageFragment_firesEvent(ArgumentCaptor<SwitchedToFrameEvent> eventCaptor) {
        PageFragment fragment = MockFactory.fragment().withName("The Name").build();
        cut.onFrame(fragment);
        verify(events).fireEvent(eventCaptor.capture());
        SwitchedToFrameEvent event = eventCaptor.getValue();
        assertThat(event.getTarget()).isEqualTo("The Name");
    }

    @Test
    @DisplayName("onFrame(PageFragment) non frame throws exception")
    void focusOnFrameByPageFragment_nonFrameFragmentThrowsException() {

        PageFragment fragment = MockFactory.fragment().withName("The Name").build();
        NoSuchFrameException exception = mock(NoSuchFrameException.class);
        when(webDriver.switchTo().frame(fragment.webElement())).thenThrow(exception);

        expectThrows(NoSuchFrameException.class, () -> {
            cut.onFrame(fragment);
        });

        verify(events).fireExceptionEvent(exception);
        verifyNoMoreInteractions(events);

    }

    @Test
    @DisplayName("onWindow(String) switches to the window with given handle")
    void focusOnWindowByNameOrHandle_invokesCorrectLocatorMethod() {
        cut.onWindow("fooBar");
        verify(webDriver.switchTo()).window("fooBar");
    }

    @Test
    @DisplayName("onWindow(String) fires event")
    void focusOnWindowByNameOrHandle_firesEvent(ArgumentCaptor<SwitchedToWindowEvent> eventCaptor) {
        cut.onWindow("fooBar");
        verify(events).fireEvent(eventCaptor.capture());
        SwitchedToWindowEvent event = eventCaptor.getValue();
        assertThat(event.getNameOrHandle()).isEqualTo("fooBar");
    }

    @Test
    @DisplayName("onWindow(String) unknown window handle throws exception")
    void focusOnWindowByNameOrHandle_unknownNameOrHandleThrowsException() {

        NoSuchWindowException exception = mock(NoSuchWindowException.class);
        when(webDriver.switchTo().window("fooBar")).thenThrow(exception);

        expectThrows(NoSuchWindowException.class, () -> {
            cut.onWindow("fooBar");
        });

        verify(events).fireExceptionEvent(exception);
        verifyNoMoreInteractions(events);

    }

    @Test
    @DisplayName("onDefaultContent() switches to default content")
    void focusOnDefaultContent_invokesCorrectLocatorMethod() {
        cut.onDefaultContent();
        verify(webDriver.switchTo()).defaultContent();
    }

    @Test
    @DisplayName("onDefaultContent() fires event")
    void focusOnDefaultContent_firesEvent(ArgumentCaptor<SwitchedToDefaultContentEvent> eventCaptor) {
        cut.onDefaultContent();
        verify(events).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();
    }

}
