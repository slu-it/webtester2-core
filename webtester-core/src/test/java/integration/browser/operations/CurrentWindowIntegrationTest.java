package integration.browser.operations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import integration.BaseIntegrationTest;
import junit.extensions.IntegrationTest;

import info.novatec.testit.webtester.pagefragments.Button;
import info.novatec.testit.webtester.pagefragments.annotations.IdentifyUsing;
import info.novatec.testit.webtester.pages.Page;


@IntegrationTest
public class CurrentWindowIntegrationTest extends BaseIntegrationTest {

    @Override
    protected String getHTMLFilePath() {
        return "html/browser/window.html";
    }

    @Test
    public void scrollIntoView() {
        Button outOfView = create(TestPage.class).outOfView();
        assertThat(elementIsInView()).isFalse();
        browser().currentWindow().scrollTo(outOfView);
        assertThat(elementIsInView()).isTrue();
    }

    Boolean elementIsInView() {
        return browser().javaScript().executeWithReturn("return isScrolledIntoView()");
    }

    private interface TestPage extends Page {

        @IdentifyUsing("#outOfView")
        Button outOfView();

    }

}
