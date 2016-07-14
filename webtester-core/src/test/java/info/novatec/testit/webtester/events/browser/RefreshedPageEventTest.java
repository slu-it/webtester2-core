package info.novatec.testit.webtester.events.browser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import junit.extensions.UnitTest;


@UnitTest
public class RefreshedPageEventTest {

    @Test
    public void descriptionIsGeneratedCorrectly() {
        RefreshedPageEvent event = new RefreshedPageEvent();
        assertThat(event.describe()).isEqualTo("refreshed the page");
    }

}
