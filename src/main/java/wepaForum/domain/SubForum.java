package wepaForum.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class SubForum extends AbstractPersistable<Long> {
    @NotBlank
    @Column(unique = true)
    @Length(max = 100)
    private String subject;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Topic> topics;
    
    public SubForum() {}
    
    public SubForum(String subject) {
        this.subject = subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
    
    public List<Topic> getTopics() {
        if (topics == null)
            topics = new ArrayList<>();
        return topics;
    }
    
    public void addTopic(Topic topic) {
        if (topics == null)
            topics = new ArrayList<>();
        topics.add(topic);
    }
    
    public void deleteTopic(Topic topic) {
        topics.remove(topic);
    }
}
