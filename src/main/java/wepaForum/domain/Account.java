package wepaForum.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Account extends AbstractPersistable<Long> {
    @NotBlank
    @Column(unique = true)
    @Length(max = 30)
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String permission;
    @ManyToMany
    private List<Topic> topics;
    
    public Account() {
        
    }

    public Account(String username, String password, String permission) {
        this.username = username;
        this.password = password;
        this.permission = permission;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public String getPermission() {
        return this.permission;
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
}
