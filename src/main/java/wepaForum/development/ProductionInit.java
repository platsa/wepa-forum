package wepaForum.development;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wepaForum.domain.Account;
import wepaForum.domain.Forum;
import wepaForum.repository.AccountRepository;
import wepaForum.repository.ForumRepository;

@Component
@Profile("production")
public class ProductionInit {
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private AccountRepository accountRepository;
    
    @PostConstruct
    public void init() {
        if (forumRepository.findAll().isEmpty()) {
            Forum forum = new Forum("wepa-Forum");
            forumRepository.save(forum);
        }
        if (accountRepository.findByPermission("ADMIN").isEmpty()) {
            accountRepository.save(new Account("admin", "admin", "ADMIN"));
        }
    }
}
