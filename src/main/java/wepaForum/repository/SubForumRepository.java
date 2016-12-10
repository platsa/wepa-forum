package wepaForum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaForum.domain.SubForum;


public interface SubForumRepository extends JpaRepository<SubForum, Long> {
    
}
