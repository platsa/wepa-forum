package wepaForum.config;

import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.PostConstruct;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import wepaForum.domain.Forum;
import wepaForum.repository.ForumRepository;

@Configuration
@Profile("production")
public class ProductionProfile {
    @Autowired
    ForumRepository forumRepository;
    
    @PostConstruct
    public void init() {
        if (forumRepository.findAll().isEmpty()) {
            Forum forum = new Forum("wepa-Forum");
            forumRepository.save(forum);
        }
    }

    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}
