package info.novatec.testit.webtester.browser.operations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.security.Credentials;
import org.openqa.selenium.security.UserAndPassword;

import junit.extensions.UnitTest;

import info.novatec.testit.webtester.browser.Browser;
import info.novatec.testit.webtester.events.EventSystem;
import info.novatec.testit.webtester.events.browser.AcceptedAlertEvent;
import info.novatec.testit.webtester.events.browser.DeclinedAlertEvent;


@UnitTest
class AlertHandlerTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    WebDriver webDriver;
    @Mock
    EventSystem events;
    @Mock
    Browser browser;

    @InjectMocks
    AlertHandler cut;

    @BeforeEach
    void initializeMockedBrowser() {
        doReturn(webDriver).when(browser).webDriver();
        doReturn(events).when(browser).events();
        doReturn(true).when(events).isEnabled();
    }

    @Test
    @DisplayName("accept() accepts existing alert")
    void accept_alertsCanBeAccepted() {
        Alert alert = defineAlertToBePresent("hello alert!");
        cut.accept();
        verify(alert).accept();
    }

    @Test
    @DisplayName("accept() throws exception if no alert exists")
    void accept_acceptingNonExistentAlertThrowsException() {
        defineAlertNotToBePresent();
        expectThrows(NoAlertPresentException.class, () -> {
            cut.accept();
        });
    }

    @Test
    @DisplayName("accept() fires event")
    void accept_acceptingAnAlertFiresEvent(ArgumentCaptor<AcceptedAlertEvent> eventCaptor) {
        defineAlertToBePresent("hello alert!");
        cut.accept();
        verify(events).fireEvent(eventCaptor.capture());
        AcceptedAlertEvent event = eventCaptor.getValue();
        assertThat(event.getAlertMessage()).isEqualTo("hello alert!");
    }

    @Test
    @DisplayName("acceptIfPresent() accepts existing alert")
    void acceptIfPresent_alertsCanBeAcceptedIfTheyArePresent() {
        Alert alert = defineAlertToBePresent("hello alert!");
        cut.acceptIfPresent();
        verify(alert).accept();
    }

    @Test
    @DisplayName("acceptIfPresent() doesn't throw exception if no alert exists")
    void acceptIfPresent_acceptingAnAlertIfItsPresentDoesNotThrowExceptionIfNoAlertExists() {
        defineAlertNotToBePresent();
        cut.acceptIfPresent();
        // no exception
    }

    @Test
    @DisplayName("acceptIfPresent() fires event")
    void acceptIfPresent_acceptingAnAlertFiresEvent(ArgumentCaptor<AcceptedAlertEvent> eventCaptor) {
        defineAlertToBePresent("hello alert!");
        cut.acceptIfPresent();
        verify(events).fireEvent(eventCaptor.capture());
        AcceptedAlertEvent event = eventCaptor.getValue();
        assertThat(event.getAlertMessage()).isEqualTo("hello alert!");
    }

    @Test
    @DisplayName("decline() declines existing alert")
    void decline_alertsCanBeDeclined() {
        Alert alert = defineAlertToBePresent("hello alert!");
        cut.decline();
        verify(alert).dismiss();
    }

    @Test
    @DisplayName("decline() throws exception if no alert exists")
    void decline_decliningNonExistentAlertThrowsException() {
        defineAlertNotToBePresent();
        expectThrows(NoAlertPresentException.class, () -> {
            cut.decline();
        });
    }

    @Test
    @DisplayName("decline() fires event")
    void decline_decliningAnAlertFiresEvent(ArgumentCaptor<DeclinedAlertEvent> eventCaptor) {
        defineAlertToBePresent("hello alert!");
        cut.decline();
        verify(events).fireEvent(eventCaptor.capture());
        DeclinedAlertEvent event = eventCaptor.getValue();
        assertThat(event.getAlertMessage()).isEqualTo("hello alert!");
    }

    @Test
    @DisplayName("declineIfPresent() declines existing alert")
    void declineIfPresent_alertsCanBeDeclinedIfTheyArePresent() {
        Alert alert = defineAlertToBePresent("hello alert!");
        cut.declineIfPresent();
        verify(alert).dismiss();
    }

    @Test
    @DisplayName("declineIfPresent() doesn't throw exception if no alert exists")
    void declineIfPresent_decliningAnAlertIfItsPresentDoesNotThrowExceptionIfNoAlertExists() {
        defineAlertNotToBePresent();
        cut.declineIfPresent();
        // no exception
    }

    @Test
    @DisplayName("declineIfPresent() fires event")
    void declineIfPresent_decliningAnAlertFiresEvent(ArgumentCaptor<DeclinedAlertEvent> eventCaptor) {
        defineAlertToBePresent("hello alert!");
        cut.declineIfPresent();
        verify(events).fireEvent(eventCaptor.capture());
        DeclinedAlertEvent event = eventCaptor.getValue();
        assertThat(event.getAlertMessage()).isEqualTo("hello alert!");
    }

    @Test
    @DisplayName("isPresent() returns false if alert is not present")
    void isPresent_presenceOfNonExistentAlertReturnsFalse() {
        defineAlertNotToBePresent();
        assertThat(cut.isPresent()).isFalse();
    }

    @Test
    @DisplayName("isPresent() returns true if alert is present")
    void isPresent_presenceOfExistentAlertReturnsTrue() {
        defineAlertToBePresent("hello alert!");
        assertThat(cut.isPresent()).isTrue();
    }

    @Test
    @DisplayName("get() returns empty optional if alert is not present")
    void get_gettingNonExistentAlertReturnsEmptyOptional() {
        defineAlertNotToBePresent();
        assertThat(cut.get()).isEmpty();
    }

    @Test
    @DisplayName("get() returns optional containing the alert if alert is present")
    void get_gettingExistentAlertReturnsItAsAnOptional() {
        defineAlertToBePresent("hello alert!");
        assertThat(cut.get()).isPresent();
    }

    @Test
    @DisplayName("authenticateUsing(Credentials) executes authentication with credentials")
    void authenticateWith_canAuthenticateWithCredentials() {

        Alert alert = defineAlertToBePresent("please sign in");
        Credentials credentials = new UserAndPassword("foo", "bar");
        cut.authenticateWith(credentials);

        verify(alert).authenticateUsing(credentials);

    }

    @Test
    @DisplayName("authenticateUsing(String, String) executes authentication with username and password")
    void authenticateWith_canAuthenticateWithUsernameAndPassword(ArgumentCaptor<Credentials> credentialsCaptor) {

        Alert alert = defineAlertToBePresent("please sign in");
        cut.authenticateWith("foo", "bar");
        verify(alert).authenticateUsing(credentialsCaptor.capture());

        Credentials credentials = credentialsCaptor.getValue();
        assertThat(credentials).isInstanceOf(UserAndPassword.class);
        assertThat((( UserAndPassword ) credentials).getUsername()).isEqualTo("foo");
        assertThat((( UserAndPassword ) credentials).getPassword()).isEqualTo("bar");

    }

    Alert defineAlertToBePresent(String message) {
        Alert alert = mock(Alert.class);
        when(alert.getText()).thenReturn(message);
        when(webDriver.switchTo().alert()).thenReturn(alert);
        return alert;
    }

    void defineAlertNotToBePresent() {
        NoAlertPresentException exception = new NoAlertPresentException();
        when(webDriver.switchTo().alert()).thenThrow(exception);
    }

}
