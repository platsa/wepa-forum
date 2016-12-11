package wepaForum.development;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import wepaForum.domain.Account;
import wepaForum.domain.Forum;
import wepaForum.domain.ForumCategory;
import wepaForum.domain.SubForum;
import wepaForum.domain.Topic;
import wepaForum.repository.AccountRepository;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;

@Component
@Profile("default")
public class InitForum {
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
    
    @PostConstruct
    public void init() {
        if (accountRepository.findAll().isEmpty()) {
            Account user = new Account("user", passwordEncoder.encode("user"), "USER");
            accountRepository.save(user);
            
            Account admin = new Account("admin", passwordEncoder.encode("admin"), "ADMIN");
            accountRepository.save(admin);
            
            Account moderator = new Account("moderator", passwordEncoder.encode("moderator"), "MODERATOR");
            accountRepository.save(moderator);
        }
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

        forumRepository.save(forum);
        forumCategoryRepository.save(category);
        subForumRepository.save(subForum);
        topicRepository.save(topic);
    }
    
}
