package wepaForum.service;

import java.util.Calendar;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
@ActiveProfiles("test")
public class DeletingTest {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private ForumCategoryRepository forumCategoryRepository;
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private DeletingService deletingService;
    
    public DeletingTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        deleteDb();
        setUpDb();
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    @Transactional
    public void deleteMessageDeletesItFromEveryWhere() {            
        deletingService.deleteMessage(topicRepository.findAll().get(0).getId(),
                topicRepository.findAll().get(0).getMessages().get(0).getId());
        
        assertTrue(messageRepository.findAll().isEmpty());
        assertTrue(topicRepository.findAll().get(0).getMessages().isEmpty());
    }
    
    @Test
    @Transactional
    public void deleteTopicDeletesItsDescendantsFromEveryWhere() {            
        deletingService.deleteTopic(subForumRepository.findAll().get(0).getId(),
                subForumRepository.findAll().get(0).getTopics().get(0).getId());
        
        assertTrue(messageRepository.findAll().isEmpty());
        assertTrue(topicRepository.findAll().isEmpty());
        assertTrue(subForumRepository.findAll().get(0).getTopics().isEmpty());
        assertTrue(accountRepository.findByUsername("admin").get(0).getTopics().isEmpty());
    }
    
    @Test
    @Transactional
    public void deleteSubForumDeletesItsDescendantsFromEveryWhere() {
        deletingService.deleteSubForum(forumCategoryRepository.findAll().get(0).getId(),
                forumCategoryRepository.findAll().get(0).getSubForums().get(0).getId());
        assertTrue(messageRepository.findAll().isEmpty());
        assertTrue(topicRepository.findAll().isEmpty());
        assertTrue(subForumRepository.findAll().isEmpty());
        assertTrue(forumCategoryRepository.findAll().get(0).getSubForums().isEmpty());
        assertTrue(accountRepository.findByUsername("admin").get(0).getTopics().isEmpty());
    }
    
    @Test
    @Transactional
    public void deleteForumCategoryDeletesItsDescendantsFromEveryWhere() {
        deletingService.deleteForumCategory(forumRepository.findAll().get(0).getId(),
                forumRepository.findAll().get(0).getForumCategories().get(0).getId());
        assertTrue(messageRepository.findAll().isEmpty());
        assertTrue(topicRepository.findAll().isEmpty());
        assertTrue(subForumRepository.findAll().isEmpty());
        assertTrue(forumCategoryRepository.findAll().isEmpty());
        assertTrue(forumRepository.findAll().get(0).getForumCategories().isEmpty());
        assertTrue(accountRepository.findByUsername("admin").get(0).getTopics().isEmpty());
    }
    
    public void setUpDb() {
        Account user = new Account("user", "user", "USER");
        accountRepository.save(user);
            
        Account admin = new Account("admin", "admin", "ADMIN");
        accountRepository.save(admin);
            
        Account moderator = new Account("moderator", "moderator", "MODERATOR");
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
    public void deleteDb() {
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
