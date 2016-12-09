package wepaForum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaForum.domain.Message;

public interface MessageRepository  extends JpaRepository<Message, Long> {
    
}
