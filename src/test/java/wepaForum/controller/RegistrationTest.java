package wepaForum.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import wepaForum.domain.Account;
import wepaForum.repository.AccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class RegistrationTest {
    private static final String REGISTRATION_URI = "/register";
    private static final String TOOLONG = "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk";
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private AccountRepository accountRepository;
    private MockMvc mockMvc;
    
    
    public RegistrationTest() {
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
    public void testGet() throws Exception {
        mockMvc.perform(get(REGISTRATION_URI)).andExpect(status().isOk());
    }
    
    @Test
    public void canAddWithoutPermission() throws Exception {
        mockMvc.perform(post(REGISTRATION_URI).param("username", "username")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, accountRepository.findAll().size());
        
    }
    
    @Test
    @WithMockUser(username = "user", authorities = "USER")
    public void canAddWithUserPermission() throws Exception {
        mockMvc.perform(post(REGISTRATION_URI).param("username", "username")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, accountRepository.findAll().size());
        
    }
    
    @Test
    @WithMockUser(username = "moderator", authorities = "MODERATOR")
    public void canAddWithModPermission() throws Exception {
        mockMvc.perform(post(REGISTRATION_URI).param("username", "username")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, accountRepository.findAll().size());
        
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void canAddWithAdminPermission() throws Exception {
        mockMvc.perform(post(REGISTRATION_URI).param("username", "username")
                .param("password", "password"))
                .andExpect(status().is3xxRedirection());
        assertEquals(2, accountRepository.findAll().size());
        
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void cannotAddEmptyOrTooLong() throws Exception {
        mockMvc.perform(post(REGISTRATION_URI).param("username", "")
                .param("password", "password"))
                .andExpect(status().is2xxSuccessful());
        assertEquals(1, accountRepository.findAll().size());
        
        mockMvc.perform(post(REGISTRATION_URI).param("username", "username")
                .param("password", ""))
                .andExpect(status().is2xxSuccessful());
        assertEquals(1, accountRepository.findAll().size());
        
        mockMvc.perform(post(REGISTRATION_URI).param("username", TOOLONG)
                .param("password", "password"))
                .andExpect(status().is2xxSuccessful());
        assertEquals(1, accountRepository.findAll().size());        
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void cannotAddSameUserName() throws Exception {
        mockMvc.perform(post(REGISTRATION_URI).param("username", "test")
                .param("password", "password"))
                .andExpect(status().is2xxSuccessful());
        assertEquals(1, accountRepository.findAll().size());
    }
    
    public void setUpDb() {
        accountRepository.save(new Account("test", "test", "USER"));
    }
    //Poistetaan kaikki kahteen kertaan, muuten jää joku kummittelemaan
    public void deleteDb() {
        accountRepository.deleteAll();
        accountRepository.deleteAll();
    }
}
