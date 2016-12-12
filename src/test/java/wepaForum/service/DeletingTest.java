package wepaForum.service;

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
import org.springframework.test.context.junit4.SpringRunner;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeletingTest {
    
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
    }
    
}
