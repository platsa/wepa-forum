package wepaForum.selenium;

import org.fluentlenium.adapter.FluentTest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationPageTest extends FluentTest {
    
    public WebDriver webDriver = new HtmlUnitDriver();

    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

    @LocalServerPort
    private Integer port;
    
    @Before
    public void setUp() {
    }
    
    @Test
    public void canGoToRegisterPage() {
        goTo("http://localhost:" + port);
        webDriver.findElement(By.linkText("Register")).click();
        assertTrue(webDriver.getTitle().contains("Register"));
    }
    
    @Test
    public void canRegister() {
        goTo("http://localhost:" + port + "/register");
        fill(find(By.name("username"))).with("test");
        fill(find(By.name("password"))).with("test");
        find(By.name("register")).submit();
        assertTrue(webDriver.getTitle().contains("Login Page"));
        fill(find(By.name("username"))).with("test");
        fill(find(By.name("password"))).with("test");
        find(By.name("submit")).submit();
        assertTrue(pageSource().contains("Logged in as"));
    }
    
    @Test
    public void cantRegisterWithEmptyOrTooLongUsername() {
        goTo("http://localhost:" + port + "/register");
        fill(find(By.name("username"))).with("testtesttesttesttesttesttesttest");
        fill(find(By.name("password"))).with("test");
        find(By.name("register")).submit();
        assertTrue(pageSource().contains("length must be"));
        fill(find(By.name("username"))).with("");
        fill(find(By.name("password"))).with("test");
        find(By.name("register")).submit();
        assertTrue(pageSource().contains("may not be empty"));
    }
    
    @Test
    public void cantRegisterWithEmptyPassword() {
        goTo("http://localhost:" + port + "/register");
        fill(find(By.name("username"))).with("test");
        fill(find(By.name("password"))).with("");
        find(By.name("register")).submit();
        assertTrue(pageSource().contains("may not be empty"));
    }
    
     @Test
    public void cantRegisterWithAlreadyUsedUsername() {
        goTo("http://localhost:" + port + "/register");
        fill(find(By.name("username"))).with("user");
        fill(find(By.name("password"))).with("user");
        find(By.name("register")).submit();
        assertTrue(pageSource().contains("username already taken"));
    }
}
