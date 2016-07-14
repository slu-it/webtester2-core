package info.novatec.testit.webtester.adhoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import junit.extensions.UnitTest;

import info.novatec.testit.webtester.browser.Browser;
import info.novatec.testit.webtester.internal.PageFragmentFactory;
import info.novatec.testit.webtester.pagefragments.GenericElement;
import info.novatec.testit.webtester.pagefragments.PageFragment;
import info.novatec.testit.webtester.pagefragments.identification.ByProducers;


@UnitTest
class AdHocFinderTest {

    @Mock
    PageFragmentFactory factory;
    @Mock
    SearchContext searchContext;

    @InjectMocks
    AdHocFinder cut;

    @Test
    @DisplayName("creating instance for Browser uses WebDriver as search context")
    void creatingForBrowserUsesWebDriverAsSearchContext() {

        Browser browser = mock(Browser.class);
        WebDriver webDriver = mock(WebDriver.class);
        doReturn(webDriver).when(browser).webDriver();

        AdHocFinder cut = new AdHocFinder(browser);

        assertThat(cut.getSearchContext()).isSameAs(webDriver);
        assertThat(cut.getFactory()).isNotNull();

    }

    @Test
    @DisplayName("creating instance for PageFragment uses WebElement as search context")
    void creatingForPageFragmentUsesWebElementAsSearchContext() {

        PageFragment pageFragment = mock(PageFragment.class);
        WebElement webElement = mock(WebElement.class);
        doReturn(webElement).when(pageFragment).webElement();

        AdHocFinder cut = new AdHocFinder(pageFragment);

        assertThat(cut.getSearchContext()).isSameAs(webElement);
        assertThat(cut.getFactory()).isNotNull();

    }

    @Test
    @DisplayName("find(String) initializes ByFinder correctly")
    void findString_byFinderIsCreatedCorrectly() {

        ByFinder finder = cut.find("#someId");
        assertThat(finder.getFactory()).isSameAs(factory);
        assertThat(finder.getSearchContext()).isSameAs(searchContext);

        By by = finder.getBy();
        assertThat(by).isInstanceOf(By.ByCssSelector.class);
        assertThat(by).hasToString("By.cssSelector: #someId");

    }

    @Test
    @DisplayName("findBy(By) initializes ByFinder correctly")
    void findBy_byFinderIsCreatedCorrectly() {

        ByFinder finder = cut.findBy(ByProducers.id("someId"));
        assertThat(finder.getFactory()).isSameAs(factory);
        assertThat(finder.getSearchContext()).isSameAs(searchContext);

        By by = finder.getBy();
        assertThat(by).isInstanceOf(By.ById.class);
        assertThat(by).hasToString("By.id: someId");

    }

    @Test
    @DisplayName("findGeneric() initializes TypeFinder correctly")
    void findGeneric_typeFinderIsCreatedCorrectly() {

        TypeFinder<GenericElement> finder = cut.findGeneric();
        assertThat(finder.getFactory()).isSameAs(factory);
        assertThat(finder.getSearchContext()).isSameAs(searchContext);
        assertThat(finder.getFragmentClass()).isEqualTo(GenericElement.class);

    }

    @Test
    @DisplayName("find(Class) initializes TypeFinder correctly")
    void findClass_typeFinderIsCreatedCorrectly() {

        TypeFinder<TestFragment> finder = cut.find(TestFragment.class);
        assertThat(finder.getFactory()).isSameAs(factory);
        assertThat(finder.getSearchContext()).isSameAs(searchContext);
        assertThat(finder.getFragmentClass()).isEqualTo(TestFragment.class);

    }

    public interface TestFragment extends PageFragment {
    }

}
