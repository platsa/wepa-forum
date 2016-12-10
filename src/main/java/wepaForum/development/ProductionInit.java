package wepaForum.development;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import wepaForum.domain.Forum;
import wepaForum.repository.ForumRepository;

@Component
@Profile("production")
public class ProductionInit {
    @Autowired
    ForumRepository forumRepository;
    
    @PostConstruct
    public void init() {
        if (forumRepository.findAll().isEmpty()) {
            Forum forum = new Forum("wepa-Forum");
            forumRepository.save(forum);
        }
    }
}
