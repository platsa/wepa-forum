package wepaForum.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.SubForumRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import wepaForum.domain.Forum;
import wepaForum.domain.ForumCategory;
import wepaForum.domain.Message;
import wepaForum.domain.SubForum;
import wepaForum.domain.Topic;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.TopicRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class SubForumTest {
    private static final String SUBFORUMS_URI = "/subforum/";
    private static final String TOOLONG = "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"
            + "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk"
            + "kkkkkkkkk";
    @Autowired
    private WebApplicationContext webAppContext;
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
    private MockMvc mockMvc;
    
    
    public SubForumTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply(springSecurity())
                .build();
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
    public void cannotAddWithoutPermission() throws Exception {
        mockMvc.perform(post(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId()).param("subject", "testsubject"))
                .andExpect(status().isUnauthorized());
        assertEquals(1, topicRepository.findAll().size());
        
    }
    
    @Test
    @Transactional    
    public void cannotDeleteWithoutPermission() throws Exception {
        mockMvc.perform(delete(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId() + "/"
            + subForumRepository.findAll().get(0).getTopics().get(0).getId()))
                .andExpect(status().isUnauthorized());
        assertEquals(1, topicRepository.findAll().size());
    }
    
    @Test
    @WithMockUser(username = "user", authorities = "USER")
    public void canAddWithUserPermission() throws Exception {
        mockMvc.perform(post(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId()).param("subject", "testsubject"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, topicRepository.findAll().size());
        
    }
    
    @Test
    @Transactional   
    @WithMockUser(username = "user", authorities = "USER")
    public void cannotDeleteWithUserPermission() throws Exception {
        mockMvc.perform(delete(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId() + "/"
            + subForumRepository.findAll().get(0).getTopics().get(0).getId()))
                .andExpect(status().isForbidden());
        assertEquals(1, topicRepository.findAll().size());
    }
    
    @Test
    @WithMockUser(username = "moderator", authorities = "MODERATOR")
    public void canAddWithModPermission() throws Exception {
        mockMvc.perform(post(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId()).param("subject", "testsubject"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, topicRepository.findAll().size());
        
    }
    
    @Test
    @Transactional   
    @WithMockUser(username = "moderator", authorities = "MODERATOR")
    public void canDeleteWithModPermission() throws Exception {
        mockMvc.perform(delete(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId() + "/"
            + subForumRepository.findAll().get(0).getTopics().get(0).getId()))
                .andExpect(status().is3xxRedirection());
        assertEquals(0, topicRepository.findAll().size());
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void canAddWithAdminPermission() throws Exception {
        mockMvc.perform(post(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId()).param("subject", "testsubject"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, topicRepository.findAll().size());
        
    }
    
    @Test
    @Transactional
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void canDeleteWithAdminPermission() throws Exception {
        mockMvc.perform(delete(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId() + "/"
            + subForumRepository.findAll().get(0).getTopics().get(0).getId()))
                .andExpect(status().is3xxRedirection());
        assertEquals(0, topicRepository.findAll().size());
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void cannotAddEmptyOrTooLong() throws Exception {
        mockMvc.perform(post(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId()).param("subject", ""))
                .andExpect(status().is2xxSuccessful());
        assertEquals(1, topicRepository.findAll().size());
        
        mockMvc.perform(post(SUBFORUMS_URI + subForumRepository.findAll().get(0).getId()).param("subject", TOOLONG))
                .andExpect(status().is2xxSuccessful());
        assertEquals(1, topicRepository.findAll().size());
        
    }
    
    public void setUpDb() {
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
        subForum.addTopic(topic);
        
        Message message = new Message("Hello World!", "admin");
        message.setDate(Calendar.getInstance().getTime());
        messageRepository.save(message);
        topic.addMessage(message);

        forumRepository.save(forum);
        forumCategoryRepository.save(category);
        subForumRepository.save(subForum);
        topicRepository.save(topic);
    }
    //Poistetaan kaikki kahteen kertaan, muuten jää joku kummittelemaan
    public void deleteDb() {
        forumRepository.deleteAll();
        forumCategoryRepository.deleteAll();
        subForumRepository.deleteAll();
        topicRepository.deleteAll();
        messageRepository.deleteAll();
        forumRepository.deleteAll();
        forumCategoryRepository.deleteAll();
        subForumRepository.deleteAll();
        topicRepository.deleteAll();
        messageRepository.deleteAll();
    }
}
