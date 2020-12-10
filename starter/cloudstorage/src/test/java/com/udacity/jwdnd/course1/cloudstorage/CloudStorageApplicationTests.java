package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void getLoginPage() {
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
    }

    /**
     * Test that verifies that an unauthorized user can only access the login and signup pages.
     */
    @Test
    @Order(100)
    public void unauthorizedOnlyLoginAndSignUp() {
        driver.get("http://localhost:" + this.port + "/");
        Assertions.assertEquals("Login", driver.getTitle());

        driver.get("http://localhost:" + this.port + "/home");
        Assertions.assertEquals("Login", driver.getTitle());

        driver.get("http://localhost:" + this.port + "/result");
        Assertions.assertEquals("Login", driver.getTitle());

        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());

        driver.get("http://localhost:" + this.port + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());
    }

    /**
     * Write a test that signs up a new user, logs in, verifies that the home page is accessible, logs out, and verifies
     * that the home page is no longer accessible.
     */
    @Test
    @Order(101)
    public void simpleUserFlow() {
        //go to signup
        driver.get("http://localhost:" + this.port + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());

        //create user
        driver.findElement(By.id("inputFirstName")).sendKeys("FirstName");
        driver.findElement(By.id("inputLastName")).sendKeys("LastName");
        driver.findElement(By.id("inputUsername")).sendKeys(USERNAME);
        driver.findElement(By.id("inputPassword")).sendKeys(PASSWORD);
        driver.findElement(By.id("signupButton")).click();

        //go to login and login
        loginUser();

        //verify home
        driver.get("http://localhost:" + this.port + "/home");
        Assertions.assertEquals("Home", driver.getTitle());

        //logs out
        driver.findElement(By.id("logoutButton")).click();

        //verify no home
        driver.get("http://localhost:" + this.port + "/home");
        Assertions.assertEquals("Login", driver.getTitle());
    }

    /**
     * Write a test that creates a note, and verifies it is displayed.
     */
    @Test
    @Order(200)
    public void createNoteFlow() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        loginUser();

        WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", notesTab);
        final WebElement createNoteButton = driver.findElement(By.id("createNoteButton"));
        wait.until(ExpectedConditions.elementToBeClickable(createNoteButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("note-title")));

        driver.findElement(By.id("note-title")).sendKeys("title");
        driver.findElement(By.id("note-description")).sendKeys("description");
        driver.findElement(By.id("noteSaveButton")).click();
        Assertions.assertEquals("Result", driver.getTitle());

        //check for note
        driver.get("http://localhost:" + this.port + "/home");
        notesTab = driver.findElement(By.id("nav-notes-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", notesTab);

        WebElement notesTable = driver.findElement(By.id("noteTable")).findElement(By.tagName("tbody"));
        List<WebElement> notesList = notesTable.findElements(By.tagName("th"));
        Optional<WebElement> first = notesList.stream()
                .filter(e -> e.getAttribute("innerHTML").equals("title"))
                .findFirst();

        Assertions.assertTrue(first.isPresent());
    }

    /**
     * Write a test that edits an existing note and verifies that the changes are displayed.
     */
    @Test
    @Order(201)
    public void editNoteFlow() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        loginUser();

        WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", notesTab);

        WebElement notesTable = driver.findElement(By.id("noteTable")).findElement(By.tagName("tbody"));
        List<WebElement> notesList = notesTable.findElements(By.tagName("tr"));
        Optional<WebElement> optionalTr = notesList.stream()
                .filter(e -> e.findElement(By.tagName("th")).getAttribute("innerHTML").equals("title"))
                .findFirst();
        Assertions.assertTrue(optionalTr.isPresent());

        WebElement tr = optionalTr.get();
        wait.until(ExpectedConditions.elementToBeClickable(tr.findElement(By.tagName("button")))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("note-title")));

        driver.findElement(By.id("note-title")).sendKeys("_edited");
        driver.findElement(By.id("noteSaveButton")).click();
        Assertions.assertEquals("Result", driver.getTitle());

        //check for note
        driver.get("http://localhost:" + this.port + "/home");
        notesTab = driver.findElement(By.id("nav-notes-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", notesTab);

        notesTable = driver.findElement(By.id("noteTable")).findElement(By.tagName("tbody"));
        notesList = notesTable.findElements(By.tagName("th"));
        Optional<WebElement> first = notesList.stream()
                .filter(e -> e.getAttribute("innerHTML").equals("title_edited"))
                .findFirst();

        Assertions.assertTrue(first.isPresent());
    }

    /**
     * Write a test that deletes a note and verifies that the note is no longer displayed.
     */
    @Test
    @Order(202)
    public void deleteNoteFlow() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        loginUser();

        WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", notesTab);

        WebElement notesTable = driver.findElement(By.id("noteTable")).findElement(By.tagName("tbody"));
        List<WebElement> notesList = notesTable.findElements(By.tagName("tr"));
        Optional<WebElement> optionalTr = notesList.stream()
                .filter(e -> e.findElement(By.tagName("th")).getAttribute("innerHTML").equals("title_edited"))
                .findFirst();
        Assertions.assertTrue(optionalTr.isPresent());

        WebElement tr = optionalTr.get();
        final WebElement a = tr.findElement(By.tagName("a"));
        wait.until(ExpectedConditions.elementToBeClickable(a)).click();

        driver.get("http://localhost:" + this.port + "/home");
        notesTab = driver.findElement(By.id("nav-notes-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", notesTab);

        notesTable = driver.findElement(By.id("noteTable")).findElement(By.tagName("tbody"));
        notesList = notesTable.findElements(By.tagName("tr"));
        Assertions.assertEquals(0, notesList.size());
    }

    /**
     * Write a test that creates a set of credentials, verifies that they are displayed, and verifies that the displayed password is encrypted.
     */
    @Test
    @Order(300)
    public void createCredentialFlow() {
        WebDriverWait wait = new WebDriverWait (driver, 10);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        loginUser();

        WebElement credentialTab = driver.findElement(By.id("nav-credentials-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", credentialTab);

        final WebElement createNoteButton = driver.findElement(By.id("createCredentialButton"));
        wait.until(ExpectedConditions.elementToBeClickable(createNoteButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-url")));

        driver.findElement(By.id("credential-url")).sendKeys("url");
        driver.findElement(By.id("credential-username")).sendKeys(USERNAME);
        driver.findElement(By.id("credential-password")).sendKeys(PASSWORD);
        driver.findElement(By.id("credentialSaveButton")).click();
        Assertions.assertEquals("Result", driver.getTitle());

        //check for note
        driver.get("http://localhost:" + this.port + "/home");
        credentialTab = driver.findElement(By.id("nav-credentials-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", credentialTab);

        WebElement table = driver.findElement(By.id("credentialTable")).findElement(By.tagName("tbody"));
        List<WebElement> credentialList = table.findElements(By.tagName("tr"));
        Optional<WebElement> optionalRow = credentialList.stream()
                .filter(e -> e.findElement(By.tagName("th")).getAttribute("innerHTML").equals("url"))
                .findFirst();

        Assertions.assertTrue(optionalRow.isPresent());

        WebElement row = optionalRow.get();
        String attribute = row.findElement(By.className("credentialPassword")).getAttribute("innerHTML");
        Assertions.assertNotEquals(attribute, PASSWORD);
    }

    /**
     * Write a test that views an existing set of credentials, verifies that the viewable password is unencrypted, edits the credentials, and verifies that the changes are displayed.
     */
    @Test
    @Order(301)
    public void editCredentialFlow() {
        WebDriverWait wait = new WebDriverWait (driver, 10);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        loginUser();

        WebElement credentialTab = driver.findElement(By.id("nav-credentials-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", credentialTab);

        WebElement table = driver.findElement(By.id("credentialTable")).findElement(By.tagName("tbody"));
        List<WebElement> credentialList = table.findElements(By.tagName("tr"));
        WebElement row = credentialList.stream().findFirst().orElse(null);
        Assertions.assertNotNull(row);

        String oldEncryptedPass = row.findElement(By.className("credentialPassword")).getAttribute("innerHTML");

        wait.until(ExpectedConditions.elementToBeClickable(row.findElement(By.tagName("button")))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("credential-url")));
        driver.findElement(By.id("credential-password")).sendKeys("_edited");
        driver.findElement(By.id("credentialSaveButton")).click();
        Assertions.assertEquals("Result", driver.getTitle());


        driver.get("http://localhost:" + this.port + "/home");
        credentialTab = driver.findElement(By.id("nav-credentials-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", credentialTab);

        table = driver.findElement(By.id("credentialTable")).findElement(By.tagName("tbody"));
        credentialList = table.findElements(By.tagName("tr"));
        row = credentialList.stream().findFirst().orElse(null);
        Assertions.assertNotNull(row);
        String newEncryptedPass = row.findElement(By.className("credentialPassword")).getAttribute("innerHTML");

        Assertions.assertNotEquals(newEncryptedPass, PASSWORD);
        Assertions.assertNotEquals(newEncryptedPass, PASSWORD+"_edited");
        Assertions.assertNotEquals(oldEncryptedPass, newEncryptedPass);
    }

    /**
     * Write a test that deletes an existing set of credentials and verifies that the credentials are no longer displayed.
     */
    @Test
    @Order(302)
    public void deleteCredentialFlow() {
        WebDriverWait wait = new WebDriverWait (driver, 10);
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        loginUser();

        WebElement credentialTab = driver.findElement(By.id("nav-credentials-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", credentialTab);

        WebElement credentialTable = driver.findElement(By.id("credentialTable")).findElement(By.tagName("tbody"));
        List<WebElement> credentialList = credentialTable.findElements(By.tagName("tr"));
        WebElement row = credentialList.stream().findFirst().orElse(null);
        Assertions.assertNotNull(row);

        final WebElement a = row.findElement(By.tagName("a"));
        wait.until(ExpectedConditions.elementToBeClickable(a)).click();

        driver.get("http://localhost:" + this.port + "/home");
        credentialTab = driver.findElement(By.id("nav-credentials-tab"));
        javascriptExecutor.executeScript("arguments[0].click()", credentialTab);

        credentialTable = driver.findElement(By.id("credentialTable")).findElement(By.tagName("tbody"));
        credentialList = credentialTable.findElements(By.tagName("tr"));
        Assertions.assertEquals(0, credentialList.size());
    }

    /**
     * Goes to login and logs in, checks if redirected to home
     */
    private void loginUser() {
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
        driver.findElement(By.id("inputUsername")).sendKeys(USERNAME);
        driver.findElement(By.id("inputPassword")).sendKeys(PASSWORD);
        driver.findElement(By.id("loginButton")).click();

        Assertions.assertEquals("Home", driver.getTitle());
    }
}
