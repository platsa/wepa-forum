package wepaForum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaForum.domain.ForumCategory;

public interface ForumCategoryRepository extends JpaRepository<ForumCategory, Long> {
    
}
