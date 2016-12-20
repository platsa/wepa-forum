package wepaForum.selenium;

import java.util.Calendar;
import org.fluentlenium.adapter.FluentTest;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import wepaForum.domain.Account;
import wepaForum.domain.Forum;
import wepaForum.domain.ForumCategory;
import wepaForum.domain.Message;
import wepaForum.domain.SubForum;
import wepaForum.domain.Topic;
import wepaForum.repository.AccountRepository;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("seletest")
public class ForumPageTest extends FluentTest {
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ForumCategoryRepository forumCategoryRepository;
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    
    public WebDriver webDriver = new HtmlUnitDriver();

    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

    @LocalServerPort
    private Integer port;
    
    @Before
    public void setUp() {
        deleteDb();
        initDb();
    }
    
    @Test
    public void canLogin() {
        goTo("http://localhost:" + port);
        webDriver.findElement(By.linkText("Login")).click();
        assertTrue(pageSource().contains("Login with Username and Password"));
        enterDetailsAndSubmit("user", "user");
        assertTrue(pageSource().contains("Logged in as"));
        assertTrue(pageSource().contains("Logout"));
    }
    
    @Test
    public void noAdminLinksToUser() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("user", "user");
        assertTrue(!pageSource().contains("Delete"));
        assertTrue(!pageSource().contains("Add a new"));
    }
    
    @Test
    public void noAdminLinksToModerator() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("moderator", "moderator");
        assertTrue(!pageSource().contains("Delete"));
        assertTrue(!pageSource().contains("Add a new"));
    }
    
    @Test
    public void adminCanDeleteSubForum() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("admin", "admin");
        assertTrue(pageSource().contains("Testataan etusivua"));
        find(By.name("delete_subforum")).submit();
        assertTrue(!pageSource().contains("Testataan etusivua"));
    }
    
    @Test
    public void adminCanDeleteCategory() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("admin", "admin");
        assertTrue(pageSource().contains("Testausta"));
        find(By.name("delete_category")).submit();
        assertTrue(!pageSource().contains("Testausta"));
    }
    
    @Test
    public void adminCanAddSubforum() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("admin", "admin");
        assertTrue(!pageSource().contains("TestSubForum"));
        fill(find(By.name("subject"))).with("TestSubForum");
        find(By.name("add_subforum")).submit();
        assertTrue(pageSource().contains("TestSubForum"));        
    }
    
    @Test
    public void adminCanAddCategory() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("admin", "admin");
        assertTrue(!pageSource().contains("TestCategory"));
        fill(find(By.name("category"))).with("TestCategory");
        find(By.name("add_category")).submit();
        assertTrue(pageSource().contains("TestCategory"));        
    }
    
    @Test
    public void adminCantAddEmptyOrTooLongSubforum() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("admin", "admin");
        fill(find(By.name("subject"))).with("");
        find(By.name("add_subforum")).submit();
        assertTrue(pageSource().contains("may not be empty"));
        fill(find(By.name("subject"))).with(longString());
        find(By.name("add_subforum")).submit();
        assertTrue(pageSource().contains("length must be"));
    }
    
    @Test
    public void adminCantAddEmptyOrTooLongCategory() {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit("admin", "admin");
        fill(find(By.name("category"))).with("");
        find(By.name("add_category")).submit();
        assertTrue(pageSource().contains("may not be empty"));
        fill(find(By.name("category"))).with(longString());
        find(By.name("add_category")).submit();
        assertTrue(pageSource().contains("length must be"));
    }
    
    private String longString() {
        String s = "k";
        for (int i = 0; i < 100; i++) {
            s += "k";
        }
        return s;
    }
    
    private void enterDetailsAndSubmit(String username, String password) {
        fill(find(By.name("username"))).with(username);
        fill(find(By.name("password"))).with(password);
        find(By.name("submit")).submit();
    }
    
    private void initDb() {
        Account user = new Account("user", passwordEncoder.encode("user"), "USER");
        accountRepository.save(user);
            
        Account admin = new Account("admin", passwordEncoder.encode("admin"), "ADMIN");
        accountRepository.save(admin);
            
        Account moderator = new Account("moderator", passwordEncoder.encode("moderator"), "MODERATOR");
        accountRepository.save(moderator);
            
        Forum forum = new Forum("wepa-Forum");
        forumRepository.save(forum);
        
        ForumCategory category = new ForumCategory("Testausta");
        forumCategoryRepository.save(category);
        forum.addForumCategory(category);
        
        SubForum subForum = new SubForum("Testataan etusivua");
        subForumRepository.save(subForum);
        category.addSubForum(subForum);
        
        Topic topic = new Topic("Ensimmäinen aihe");
        topicRepository.save(topic);
        admin.getTopics().add(topic);
        topic.addAccount(admin);
        subForum.addTopic(topic);
        
        Message message = new Message("Hello World!", "admin");
        message.setDate(Calendar.getInstance().getTime());
        messageRepository.save(message);
        topic.addMessage(message);

        accountRepository.save(admin);
        forumRepository.save(forum);
        forumCategoryRepository.save(category);
        subForumRepository.save(subForum);
        topicRepository.save(topic);
    }
    
    //Poistetaan kaikki kahteen kertaan, muuten jää joku kummittelemaan
    private void deleteDb() {
        accountRepository.deleteAll();
        forumRepository.deleteAll();
        forumCategoryRepository.deleteAll();
        subForumRepository.deleteAll();
        topicRepository.deleteAll();
        messageRepository.deleteAll();
        accountRepository.deleteAll();
        forumRepository.deleteAll();
        forumCategoryRepository.deleteAll();
        subForumRepository.deleteAll();
        topicRepository.deleteAll();
        messageRepository.deleteAll();
    }
}
