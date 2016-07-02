package info.novatec.testit.webtester.browser.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import info.novatec.testit.webtester.browser.Browser;
import info.novatec.testit.webtester.browser.operations.Windows.Window;
import info.novatec.testit.webtester.internal.PageFragmentFactory;


@RunWith(Enclosed.class)
public class WindowsTest {

    @RunWith(MockitoJUnitRunner.class)
    public static abstract class AbstractWindowsTest {

        @Mock(answer = Answers.RETURNS_DEEP_STUBS)
        Browser browser;
        @Mock
        PageFragmentFactory factory;
        @InjectMocks
        Windows cut;

        void defineOrderedWindowHandles(String... handles) {
            when(browser.webDriver().getWindowHandles()).thenReturn(Sets.newLinkedHashSet(handles));
        }

    }

    public static class GetHandles extends AbstractWindowsTest {

        @Test
        public void handlesAreReturnedUnchangedFromWebDriver() {

            defineOrderedWindowHandles("handle1", "handle2");

            Set<String> actualHandles = cut.getHandles();
            assertThat(actualHandles).containsOnly("handle1", "handle2");

        }

    }

    public static class FindFirst extends AbstractWindowsTest {

        @Test
        public void existingWindowIsFound() {

            defineOrderedWindowHandles("handle: foo", "handle: bar");

            Window actualWindow = cut.findFirst(window -> window.getHandle().contains("bar"));
            assertThat(actualWindow.getHandle()).isEqualTo("handle: bar");
            assertThat(actualWindow.getBrowser()).isSameAs(browser);
            assertThat(actualWindow.getFactory()).isSameAs(factory);

        }

        @Test(expected = Windows.NoMatchingWindowFoundException.class)
        public void exceptionIsThrownIfNoWindowMatchesFilter() {
            defineOrderedWindowHandles("handle: foo", "handle: bar");
            cut.findFirst(window -> window.getHandle().contains("unknown"));
        }

        @Test
        public void theFirstFoundWindowIsReturnedIfMultipleCandidatesMatch() {

            defineOrderedWindowHandles("handle: bar #1", "handle: bar #2");

            Window actualWindow = cut.findFirst(window -> window.getHandle().contains("bar"));
            assertThat(actualWindow.getHandle()).isEqualTo("handle: bar #1");

        }

    }

}
