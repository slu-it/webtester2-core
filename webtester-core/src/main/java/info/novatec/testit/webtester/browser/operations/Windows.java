package info.novatec.testit.webtester.browser.operations;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

import lombok.AccessLevel;
import lombok.Getter;

import info.novatec.testit.webtester.WebTesterException;
import info.novatec.testit.webtester.browser.Browser;
import info.novatec.testit.webtester.internal.PageFragmentFactory;
import info.novatec.testit.webtester.pagefragments.GenericElement;
import info.novatec.testit.webtester.pagefragments.PageFragment;


/**
 * This browser operation offers methods related to the handling of multiple browser windows.
 *
 * @see #getHandles()
 * @see #findFirst(Predicate)
 * @since 2.1
 */
public class Windows extends BaseBrowserOperation {

    private final PageFragmentFactory factory;

    /**
     * Creates a new {@link Windows} for the given {@link Browser}.
     *
     * @param browser the browser to use
     * @since 2.1
     */
    public Windows(Browser browser) {
        this(browser, new PageFragmentFactory(browser));
    }

    /**
     * Creates a new {@link Windows} for the given {@link Browser} and {@link PageFragmentFactory}.
     *
     * @param browser the browser to use
     * @param factory the factory to use
     * @since 2.1
     */
    public Windows(Browser browser, PageFragmentFactory factory) {
        super(browser);
        this.factory = factory;
    }

    /**
     * Returns a set of all window handles of this {@link Browser}.
     * <p>
     * These handles can be used to switch focus between these windows using {@link Browser#focus()}.
     *
     * @return all handles as an unordered set
     * @see Browser#focus()
     * @see FocusSetter#onWindow(String)
     * @see WebDriver#getWindowHandles()
     * @since 2.1
     */
    public Set<String> getHandles() {
        return webDriver().getWindowHandles();
    }

    /**
     * Iterates over all {@link Window windows} of this {@link Browser} to find the first window matching the given
     * predicate and returns it. In case there is no matching window an exception is thrown.
     * <p>
     * <b>Example:</b> {@code windows.findFirst(window -> window.getPageTitle().equals("Some Title"))}
     *
     * @param filter the window predicate used for matching windows
     * @return the found window
     * @throws NoMatchingWindowFoundException in case no window matching the predicate was found
     * @since 2.1
     */
    public Window findFirst(Predicate<? super Window> filter) {
        return webDriver().getWindowHandles()
            .stream()
            .map(handle -> new Window(browser(), factory, handle))
            .filter(filter)
            .findFirst()
            .orElseThrow(
                () -> new NoMatchingWindowFoundException("could not find any window matching the given filter: " + filter));
    }

    /**
     * Allows for accessing certain information about a window of the {@link Browser}.
     * Commonly used to switch focus to a window using {@link Windows#findFirst(Predicate)}.
     *
     * @since 2.1
     */
    @Getter(AccessLevel.PACKAGE)
    public static class Window {

        private final Browser browser;
        private final PageFragmentFactory factory;
        @Getter(AccessLevel.PUBLIC)
        private final String handle;

        /**
         * Creates a new {@link Window} instance with the given browser and unique handle.
         * <p>
         * This constructor is package private because windows should only be initialized from within the {@link Windows}
         * class or tests.
         *
         * @param browser the browser backing this window
         * @param handle the unique handle of the window
         * @since 2.1
         */
        Window(Browser browser, PageFragmentFactory factory, String handle) {
            this.browser = browser;
            this.handle = handle;
            this.factory = factory;
        }

        /**
         * Changes the {@link Browser browser's} focus to this window.
         * <p>
         * This will change the search scope for {@link PageFragment page fragments} to this window as well!
         *
         * @see TargetLocator#window(String)
         * @since 2.1
         */
        public void setFocus() {
            browser.webDriver().switchTo().window(handle);
        }

        /**
         * Returns the URL of this {@link Window}.
         *
         * @return the URL of the window
         * @see WebDriver#getCurrentUrl()
         * @since 2.1
         */
        public String getUrl() {
            return getDataFromWindow(WebDriver::getCurrentUrl);
        }

        /**
         * Returns whether or not this {@link Window} has the specified URL.
         *
         * @param url the URL to check
         * @return true if URL of window is an exact match - otherwise false
         * @since 2.1
         */
        public boolean hasUrl(String url) {
            return getUrl().equals(url);
        }

        /**
         * Returns whether or not the given partial url is contained within this {@link Window window's} URL.
         *
         * @param partialUrl the URL fragment which should be part of the window's URL
         * @return true if URL of window contains the partial URL - otherwise false
         * @since 2.1
         */
        public boolean hasUrlContaining(String partialUrl) {
            return getUrl().contains(partialUrl);
        }

        /**
         * Returns the page title of this {@link Window}.
         *
         * @return the page title of the window
         * @see WebDriver#getTitle()
         * @since 2.1
         */
        public String getPageTitle() {
            return getDataFromWindow(WebDriver::getTitle);
        }

        /**
         * Returns whether or not this {@link Window} has the specified page title.
         *
         * @param title the page title to check
         * @return true if page title of window is an exact match - otherwise false
         * @since 2.1
         */
        public boolean hasPageTitle(String title) {
            return getPageTitle().equals(title);
        }

        /**
         * Returns whether or not the given partial page title is contained within this {@link Window window's} title.
         *
         * @param partialTitle the title fragment which should be part of the window's title
         * @return true if title of window contains the partial title - otherwise false
         * @since 2.1
         */
        public boolean hasPageTitleContaining(String partialTitle) {
            return getPageTitle().contains(partialTitle);
        }

        /**
         * Returns whether or not an element with the given CSS selector can be found within this {@link Window}.
         *
         * @param cssSelector the CSS selector to use
         * @return true if element was found - otherwise false
         * @since 2.1
         */
        public boolean hasElement(String cssSelector) {
            return getDataFromWindow(webDriver -> findWebElement(webDriver, cssSelector).isPresent());
        }

        /**
         * Returns whether or not an element with the given CSS selector and matching the given {@link Predicate} can be
         * found within this {@link Window}.
         * <p>
         * <b>Example:</b> {@code window.hasElementMatching("#someId", element -> element.isVisible())}
         *
         * @param cssSelector the CSS selector to use
         * @param condition the condition to check
         * @return true if element was found and matches condition - otherwise false (might be element was not found, or did
         * not match condition)
         * @since 2.1
         */
        public boolean hasElementMatching(String cssSelector, Predicate<? super GenericElement> condition) {
            return getDataFromWindow(webDriver -> findWebElement(webDriver, cssSelector).map(this::wrapAsGenericElement)
                .map(condition::test)
                .orElse(false));
        }

        private Optional<WebElement> findWebElement(WebDriver webDriver, String cssSelector) {
            WebElement element = null;
            try {
                element = webDriver.findElement(By.cssSelector(cssSelector));
            } catch (NoSuchElementException e) {
                // no such element >> return false;
            }
            return Optional.ofNullable(element);
        }

        private GenericElement wrapAsGenericElement(WebElement webElement) {
            return factory.pageFragment(GenericElement.class, webElement);
        }

        private <T> T getDataFromWindow(Function<WebDriver, T> function) {
            WebDriver webDriver = browser.webDriver();
            String currentHandle = webDriver.getWindowHandle();
            T data;
            try {
                data = function.apply(webDriver.switchTo().window(handle));
            } finally {
                webDriver.switchTo().window(currentHandle);
            }
            return data;
        }

        /**
         * Executes the given {@link Consumer consumer} on this {@link Window window's} content.
         * <p>
         * For this the focus of the browser is temporarly set on this window. After the execution the previously window is
         * set back into focus.
         * <p>
         * This is useful in cases where e.g. a small operation is executed on the content of a popup.
         *
         * @param operation the operation to execute on the window's content
         * @since 2.1
         */
        public void executeWithin(Consumer<Browser> operation) {
            WebDriver webDriver = browser.webDriver();
            String currentHandle = webDriver.getWindowHandle();
            try {
                webDriver.switchTo().window(handle);
                operation.accept(browser);
            } finally {
                webDriver.switchTo().window(currentHandle);
            }
        }

    }

    /**
     * This exception is throw in case the {@link Window#findFirst(Predicate)} method did not find any matching {@link
     * Window}.
     *
     * @since 2.1
     */
    public static class NoMatchingWindowFoundException extends WebTesterException {

        /**
         * Creates a new {@link NoMatchingWindowFoundException} with the given message.
         *
         * @param message the exception's message
         * @since 2.1
         */
        NoMatchingWindowFoundException(String message) {
            super(message);
        }

    }

}
