package info.novatec.testit.webtester.browser.proxy;

import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.selenium.Proxy;

import junit.extensions.UnitTest;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NoProxyConfigurationTest {

    @Mock
    Proxy proxy;
    @InjectMocks
    NoProxyConfiguration cut;

    @Test
    public void proxyIsNotTouchedInAnyWay() {
        cut.configureProxy(proxy);
        verifyZeroInteractions(proxy);
    }

}
