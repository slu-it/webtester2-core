package info.novatec.testit.webtester.adhoc;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import junit.extensions.UnitTest;

import info.novatec.testit.webtester.internal.PageFragmentFactory;
import info.novatec.testit.webtester.pagefragments.GenericElement;
import info.novatec.testit.webtester.pagefragments.PageFragment;


@UnitTest
class ByFinderTest {

    @Mock
    PageFragmentFactory factory;
    @Mock
    SearchContext searchContext;
    @Mock
    By by;

    @InjectMocks
    ByFinder cut;

    @Test
    @DisplayName("asGeneric() creates GenericElement for found WebElement")
    void asGeneric_genericElementIsCreatedFromFoundWebElement() {

        WebElement webElement = mock(WebElement.class);
        doReturn(webElement).when(searchContext).findElement(by);

        GenericElement mockElement = mock(GenericElement.class);
        doReturn(mockElement).when(factory).pageFragment(GenericElement.class, webElement);

        GenericElement element = cut.asGeneric();
        assertThat(element).isSameAs(mockElement);

    }

    @Test
    @DisplayName("asGeneric() throws NoSuchElementException if WebElement can't be found")
    void asGeneric_noSuchElementExceptionsArePropagated() {
        doThrow(NoSuchElementException.class).when(searchContext).findElement(by);
        expectThrows(NoSuchElementException.class, () -> {
            cut.asGeneric();
        });
    }

    @Test
    @DisplayName("as(Class) creates instance of class for found WebElement")
    void as_givenFragmentTypeIsCreatedFromFoundWebElement() {

        WebElement webElement = mock(WebElement.class);
        doReturn(webElement).when(searchContext).findElement(by);

        PageFragment mockElement = mock(PageFragment.class);
        doReturn(mockElement).when(factory).pageFragment(PageFragment.class, webElement);

        PageFragment element = cut.as(PageFragment.class);
        assertThat(element).isSameAs(mockElement);

    }

    @Test
    @DisplayName("as(Class) throws NoSuchElementException if WebElement can't be found")
    void as_noSuchElementExceptionsArePropagated() {
        doThrow(NoSuchElementException.class).when(searchContext).findElement(by);
        expectThrows(NoSuchElementException.class, () -> {
            cut.as(PageFragment.class);
        });
    }

    @Test
    @DisplayName("asManyGenerics() creates stream of GenericElement for found WebElement instances")
    void asManyGenerics_genericElementIsCreatedFromFoundWebElements() {

        WebElement webElement1 = mock(WebElement.class);
        WebElement webElement2 = mock(WebElement.class);
        doReturn(asList(webElement1, webElement2)).when(searchContext).findElements(by);

        GenericElement mockElement1 = mock(GenericElement.class);
        GenericElement mockElement2 = mock(GenericElement.class);
        doReturn(mockElement1).when(factory).pageFragment(GenericElement.class, webElement1);
        doReturn(mockElement2).when(factory).pageFragment(GenericElement.class, webElement2);

        Stream<GenericElement> elements = cut.asManyGenerics();
        assertThat(elements).containsExactly(mockElement1, mockElement2);

    }

    @Test
    @DisplayName("asMany(Class) creates stream of class for found WebElement instances")
    void asMany_givenFragmentTypeIsCreatedFromFoundWebElements() {

        WebElement webElement1 = mock(WebElement.class);
        WebElement webElement2 = mock(WebElement.class);
        doReturn(asList(webElement1, webElement2)).when(searchContext).findElements(by);

        PageFragment mockElement1 = mock(PageFragment.class);
        PageFragment mockElement2 = mock(PageFragment.class);
        doReturn(mockElement1).when(factory).pageFragment(PageFragment.class, webElement1);
        doReturn(mockElement2).when(factory).pageFragment(PageFragment.class, webElement2);

        Stream<PageFragment> elements = cut.asMany(PageFragment.class);
        assertThat(elements).containsExactly(mockElement1, mockElement2);

    }

}
