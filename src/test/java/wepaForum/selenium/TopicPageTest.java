package wepaForum.selenium;

import java.util.Calendar;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
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
public class TopicPageTest extends FluentTest {
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
    public void noAddOrDeleteLinksToAnonymous() {
        goTo("http://localhost:" + port + "/topic/" + topicRepository.findAll().get(0).getId());
        assertTrue(webDriver.getTitle().contains("Ensimmäinen aihe"));
        assertTrue(!pageSource().contains("Delete"));
        assertTrue(!pageSource().contains("Add a new"));
        assertTrue(!pageSource().contains("Users who have written to this topic"));
    }
    
    @Test
    public void noDeleteLinksToUser() {
        loginAsAndGoToTopic("user", "user");
        assertTrue(!pageSource().contains("Delete"));
        assertTrue(pageSource().contains("Add a new"));
        assertTrue(!pageSource().contains("Users who have written to this topic"));
    }
    
    @Test
    public void showDeleteLinksToModerator() {
        loginAsAndGoToTopic("moderator", "moderator");
        assertTrue(pageSource().contains("Delete"));
        assertTrue(pageSource().contains("Add a new"));
        assertTrue(!pageSource().contains("Users who have written to this topic"));
    }
    
    @Test
    public void showAllLinksToAdmin() {
        loginAsAndGoToTopic("admin", "admin");
        assertTrue(pageSource().contains("Delete"));
        assertTrue(pageSource().contains("Add a new"));
        assertTrue(pageSource().contains("Users who have written to this topic"));
    }
    
    @Test
    public void canDeleteMessage() {
        loginAsAndGoToTopic("admin", "admin");
        assertTrue(pageSource().contains("Hello World"));
        find(By.name("delete_message")).submit();
        assertTrue(!pageSource().contains("Hello World"));
    }
    
    @Test
    public void canAddMessage() {
        loginAsAndGoToTopic("admin", "admin");
        assertTrue(!pageSource().contains("Toinen topic"));
        fill(find(By.name("message"))).with("Toinen topic");
        find(By.name("add_message")).submit();
        assertTrue(pageSource().contains("Toinen topic"));
    }
    
    @Test
    public void cantAddEmptyOrTooLongMessage() {
        loginAsAndGoToTopic("admin", "admin");
        fill(find(By.name("message"))).with("");
        find(By.name("add_message")).submit();
        assertTrue(pageSource().contains("may not be empty"));
        
        fill(find(By.name("message"))).with(longString());
        find(By.name("add_message")).submit();
        assertTrue(pageSource().contains("length must be"));
    }
    
    private void enterDetailsAndSubmit(String username, String password) {
        fill(find(By.name("username"))).with(username);
        fill(find(By.name("password"))).with(password);
        find(By.name("submit")).submit();
    }
    
    private void loginAsAndGoToTopic(String username, String password) {
        goTo("http://localhost:" + port + "/login");
        enterDetailsAndSubmit(username, password);
        goTo("http://localhost:" + port + "/topic/" + topicRepository.findAll().get(0).getId());
    }
    
    private String longString() {
        String s = "k";
        for (int i = 0; i < 1000; i++) {
            s += "k";
        }
        return s;
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
