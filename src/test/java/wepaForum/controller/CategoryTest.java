package wepaForum.controller;

import javax.servlet.http.HttpSession;
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
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wepaForum.repository.AccountRepository;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.SubForumRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class CategoryTest {
    private static final String CATEGORIES_URI = "/category/";
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private ForumCategoryRepository forumCategoryRepository;
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private AccountRepository accountRepository;
    private MockMvc mockMvc;
    
    
    public CategoryTest() {
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
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void cannotAddWithoutPermission() throws Exception {
        HttpSession session = mockMvc.perform(formLogin().user("admin").password("admin"))
                .andReturn()
                .getRequest()
                .getSession();
        mockMvc.perform(post(CATEGORIES_URI + forumCategoryRepository.findAll().get(0).getId())
                .session((MockHttpSession) session))
                .andExpect(status().isForbidden());
        
    }
    
    @Test
    @Transactional
    public void cannotDeleteWithoutPermission() throws Exception {
        MockHttpSession session = (MockHttpSession) mockMvc.perform(formLogin().user("admin").password("admin"))
                .andReturn()
                .getRequest()
                .getSession();
        mockMvc.perform(delete(CATEGORIES_URI + forumCategoryRepository.findAll().get(0).getId() + "/"
            + forumCategoryRepository.findAll().get(0).getSubForums().get(0).getId())
                .session(session))
                .andExpect(status().isForbidden());
        assertEquals(1, subForumRepository.findAll().size());
    }
}
