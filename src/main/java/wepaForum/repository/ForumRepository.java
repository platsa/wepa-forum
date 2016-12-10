package wepaForum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaForum.domain.Forum;


public interface ForumRepository extends JpaRepository<Forum, Long> {
    
}
