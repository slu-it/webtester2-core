package info.novatec.testit.webtester.adhoc;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import junit.extensions.UnitTest;

import info.novatec.testit.webtester.internal.PageFragmentFactory;
import info.novatec.testit.webtester.pagefragments.PageFragment;
import info.novatec.testit.webtester.pagefragments.identification.ByProducers;


@UnitTest
class TypeFinderTest {

    @Mock
    PageFragmentFactory factory;
    @Mock
    SearchContext searchContext;

    TypeFinder<PageFragment> cut;

    @BeforeEach
    void init() {
        cut = new TypeFinder<>(factory, searchContext, PageFragment.class);
    }

    @Test
    @DisplayName("by(String) creates instance of configured type for found WebElement")
    void byForString_fragmentIsCreatedForGivenBy() {

        WebElement webElement = mock(WebElement.class);
        PageFragment mockElement = mock(PageFragment.class);

        doReturn(webElement).when(searchContext).findElement(any(By.class));
        doReturn(mockElement).when(factory).pageFragment(PageFragment.class, webElement);

        PageFragment element = cut.by("#someId");
        assertThat(element).isSameAs(mockElement);

    }

    @Test
    @DisplayName("by(String) given string is interpreted as a CSS selector")
    void byForString_stringIsInterpretedAsCssSelector(ArgumentCaptor<By> byCaptor) {

        cut.by("#someId");

        verify(searchContext).findElement(byCaptor.capture());

        By by = byCaptor.getValue();
        assertThat(by).isInstanceOf(By.ByCssSelector.class);
        assertThat(by).hasToString("By.cssSelector: #someId");

    }

    @Test
    @DisplayName("by(String) throws NoSuchElementException if WebElement can't be found")
    void byForString_noSuchElementExceptionsArePropagated() {
        doThrow(NoSuchElementException.class).when(searchContext).findElement(any(By.class));
        expectThrows(NoSuchElementException.class, () -> {
            cut.by("#someId");
        });
    }

    @Test
    @DisplayName("by(By) creates instance of configured type for found WebElement")
    void byForBy_fragmentIsCreatedForGivenBy() {

        WebElement webElement = mock(WebElement.class);
        PageFragment mockElement = mock(PageFragment.class);

        doReturn(webElement).when(searchContext).findElement(any(By.ById.class));
        doReturn(mockElement).when(factory).pageFragment(PageFragment.class, webElement);

        PageFragment element = cut.by(ByProducers.id("someId"));
        assertThat(element).isSameAs(mockElement);

    }

    @Test
    @DisplayName("by(By) throws NoSuchElementException if WebElement can't be found")
    void byForBy_noSuchElementExceptionsArePropagated() {
        doThrow(NoSuchElementException.class).when(searchContext).findElement(any(By.class));
        expectThrows(NoSuchElementException.class, () -> {
            cut.by(ByProducers.id("someId"));
        });
    }

    @Test
    @DisplayName("manyBy(String) creates stream of configured type for found WebElement instances")
    void manyByForString_fragmentIsCreatedForGivenBy() {

        WebElement webElement1 = mock(WebElement.class);
        WebElement webElement2 = mock(WebElement.class);
        doReturn(asList(webElement1, webElement2)).when(searchContext).findElements(any(By.class));

        PageFragment mockElement1 = mock(PageFragment.class);
        PageFragment mockElement2 = mock(PageFragment.class);
        doReturn(mockElement1).when(factory).pageFragment(PageFragment.class, webElement1);
        doReturn(mockElement2).when(factory).pageFragment(PageFragment.class, webElement2);

        Stream<PageFragment> elements = cut.manyBy(".someClass");
        assertThat(elements).containsExactly(mockElement1, mockElement2);

    }

    @Test
    @DisplayName("manyBy(String) given string is interpreted as a CSS selector")
    void manyByForString_stringIsInterpretedAsCssSelector(ArgumentCaptor<By> byCaptor) {

        cut.manyBy(".someClass");

        verify(searchContext).findElements(byCaptor.capture());

        By by = byCaptor.getValue();
        assertThat(by).isInstanceOf(By.ByCssSelector.class);
        assertThat(by).hasToString("By.cssSelector: .someClass");

    }

    @Test
    @DisplayName("manyBy(By) creates stream of configured type for found WebElement instances")
    void manyByForBy_fragmentIsCreatedForGivenBy() {

        WebElement webElement1 = mock(WebElement.class);
        WebElement webElement2 = mock(WebElement.class);
        doReturn(asList(webElement1, webElement2)).when(searchContext).findElements(any(By.ByClassName.class));

        PageFragment mockElement1 = mock(PageFragment.class);
        PageFragment mockElement2 = mock(PageFragment.class);
        doReturn(mockElement1).when(factory).pageFragment(PageFragment.class, webElement1);
        doReturn(mockElement2).when(factory).pageFragment(PageFragment.class, webElement2);

        Stream<PageFragment> elements = cut.manyBy(ByProducers.className("someClass"));
        assertThat(elements).containsExactly(mockElement1, mockElement2);

    }

}
