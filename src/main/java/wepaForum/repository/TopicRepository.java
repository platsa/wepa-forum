package wepaForum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaForum.domain.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    
}
