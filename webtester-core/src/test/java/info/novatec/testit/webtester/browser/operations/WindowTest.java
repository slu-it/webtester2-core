package info.novatec.testit.webtester.browser.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import info.novatec.testit.webtester.browser.Browser;
import info.novatec.testit.webtester.browser.operations.Windows.Window;
import info.novatec.testit.webtester.internal.PageFragmentFactory;
import info.novatec.testit.webtester.pagefragments.GenericElement;


@RunWith(Enclosed.class)
public class WindowTest {

    @RunWith(MockitoJUnitRunner.class)
    public static abstract class AbstractWindowTest {

        @Mock(answer = Answers.RETURNS_DEEP_STUBS)
        WebDriver webDriver;
        @Mock
        Browser browser;
        @Mock
        PageFragmentFactory factory;

        Window cut;

        @Before
        public void init() {
            cut = new Window(browser, factory, "window-handle");
            doReturn("main-handle").when(webDriver).getWindowHandle();
            when(browser.webDriver()).thenReturn(webDriver);
            when(webDriver.switchTo().window(anyString())).thenReturn(webDriver);
        }

    }

    public static class SetFocus extends AbstractWindowTest {

        @Test
        public void webDriverFocusIsSwitchedToWindow() {
            cut.setFocus();
            verify(webDriver.switchTo()).window("window-handle");
        }

    }

    public static class GetUrl extends AbstractWindowTest {

        @Test
        public void urlIsReturnedFromWindow() {
            when(webDriver.getCurrentUrl()).thenReturn("http://www.example.com/window.html");
            String url = cut.getUrl();
            assertThat(url).isEqualTo("http://www.example.com/window.html");
        }

        @Test
        public void windowsAreSwitchedToRetrieveUrl() {

            cut.getUrl();

            InOrder inOrder = inOrder(webDriver, webDriver.switchTo());
            inOrder.verify(webDriver.switchTo()).window("window-handle");
            inOrder.verify(webDriver).getCurrentUrl();
            inOrder.verify(webDriver.switchTo()).window("main-handle");

        }

        @SuppressWarnings("unchecked")
        @Test(expected = RuntimeException.class)
        public void inCaseOfExceptionWhileSwitchingTheFocusIsSetBackToMainWindow() {
            when(webDriver.switchTo().window("window-handle")).thenThrow(RuntimeException.class);
            try {
                cut.getUrl();
            } finally {
                verify(webDriver.switchTo()).window("main-handle");
            }
        }

    }

    public static class HasUrl extends AbstractWindowTest {

        @Test
        public void matchingUrlReturnsTrue() {
            when(webDriver.getCurrentUrl()).thenReturn("http://www.example.com/window.html");
            assertThat(cut.hasUrl("http://www.example.com/window.html")).isTrue();
        }

        @Test
        public void mismatchingUrlReturnsFalse() {
            when(webDriver.getCurrentUrl()).thenReturn("http://www.example.com/other.html");
            assertThat(cut.hasUrl("http://www.example.com/window.html")).isFalse();
        }

    }

    public static class HasUrlContaining extends AbstractWindowTest {

        @Test
        public void matchingPartialUrlReturnsTrue() {
            when(webDriver.getCurrentUrl()).thenReturn("http://www.example.com/window.html");
            assertThat(cut.hasUrlContaining("/window.html")).isTrue();
        }

        @Test
        public void mismatchingPartialUrlReturnsFalse() {
            when(webDriver.getCurrentUrl()).thenReturn("http://www.example.com/other.html");
            assertThat(cut.hasUrlContaining("/window.html")).isFalse();
        }

    }

    public static class GetPageTitle extends AbstractWindowTest {

        @Test
        public void titleIsReturnedFromWindow() {
            when(webDriver.getTitle()).thenReturn("Test Page");
            String title = cut.getPageTitle();
            assertThat(title).isEqualTo("Test Page");
        }

        @Test
        public void windowsAreSwitchedToRetrieveTitle() {

            cut.getPageTitle();

            InOrder inOrder = inOrder(webDriver, webDriver.switchTo());
            inOrder.verify(webDriver.switchTo()).window("window-handle");
            inOrder.verify(webDriver).getTitle();
            inOrder.verify(webDriver.switchTo()).window("main-handle");

        }

        @SuppressWarnings("unchecked")
        @Test(expected = RuntimeException.class)
        public void inCaseOfExceptionWhileSwitchingTheFocusIsSetBackToMainWindow() {
            when(webDriver.switchTo().window("window-handle")).thenThrow(RuntimeException.class);
            try {
                cut.getPageTitle();
            } finally {
                verify(webDriver.switchTo()).window("main-handle");
            }
        }

    }

    public static class HasPageTitle extends AbstractWindowTest {

        @Test
        public void matchingUrlReturnsTrue() {
            when(webDriver.getTitle()).thenReturn("Test Page");
            assertThat(cut.hasPageTitle("Test Page")).isTrue();
        }

        @Test
        public void mismatchingUrlReturnsFalse() {
            when(webDriver.getTitle()).thenReturn("Other Page");
            assertThat(cut.hasPageTitle("Test Page")).isFalse();
        }

    }

    public static class HasPageTitleContaining extends AbstractWindowTest {

        @Test
        public void matchingPartialUrlReturnsTrue() {
            when(webDriver.getTitle()).thenReturn("Test Page");
            assertThat(cut.hasPageTitleContaining("Test")).isTrue();
        }

        @Test
        public void mismatchingPartialUrlReturnsFalse() {
            when(webDriver.getTitle()).thenReturn("Other Page");
            assertThat(cut.hasPageTitleContaining("Test")).isFalse();
        }

    }

    public static class HasElement extends AbstractWindowTest {

        @Test
        public void findingElementReturnsTrue() {
            doReturn(mock(WebElement.class)).when(webDriver).findElement(By.cssSelector("#someId"));
            assertThat(cut.hasElement("#someId")).isTrue();
        }

        @Test
        public void notFindingElementReturnsFalse() {
            doThrow(NoSuchElementException.class).when(webDriver).findElement(By.cssSelector("#someOtherId"));
            assertThat(cut.hasElement("#someOtherId")).isFalse();
        }

    }

    public static class HasElementMatching extends AbstractWindowTest {

        @Mock
        WebElement webElement;
        @Mock
        GenericElement genericElement;

        Predicate<GenericElement> predicate = element -> "Some Text".equals(element.getVisibleText());

        @Test
        public void findingMatchingElementReturnsTrue() {

            doReturn(webElement).when(webDriver).findElement(By.cssSelector("#someId"));
            doReturn(genericElement).when(factory).pageFragment(GenericElement.class, webElement);
            doReturn("Some Text").when(genericElement).getVisibleText();

            assertThat(cut.hasElementMatching("#someId", predicate)).isTrue();

        }

        @Test
        public void notFindingElementReturnsFalse() {
            doThrow(NoSuchElementException.class).when(webDriver).findElement(By.cssSelector("#someOtherId"));
            assertThat(cut.hasElementMatching("#someId", predicate)).isFalse();
        }

        @Test
        public void notFindingMatchingElementReturnsFalse() {

            doReturn(webElement).when(webDriver).findElement(By.cssSelector("#someId"));
            doReturn(genericElement).when(factory).pageFragment(GenericElement.class, webElement);
            doReturn("Some Other Text").when(genericElement).getVisibleText();

            assertThat(cut.hasElementMatching("#someId", predicate)).isFalse();

        }

    }

    public static class ExecuteWithin extends AbstractWindowTest {

        @Mock
        Consumer<Browser> operation;

        @Test
        public void windowsAreSwitchedToExecuteOperation() {

            cut.executeWithin(operation);

            InOrder inOrder = inOrder(webDriver, webDriver.switchTo(), operation);
            inOrder.verify(webDriver.switchTo()).window("window-handle");
            inOrder.verify(operation).accept(browser);
            inOrder.verify(webDriver.switchTo()).window("main-handle");

        }

        @SuppressWarnings("unchecked")
        @Test(expected = RuntimeException.class)
        public void inCaseOfExceptionWhileSwitchingTheFocusIsSetBackToMainWindow() {
            when(webDriver.switchTo().window("window-handle")).thenThrow(RuntimeException.class);
            try {
                cut.executeWithin(operation);
            } finally {
                verify(webDriver.switchTo()).window("main-handle");
            }
        }

    }

}
