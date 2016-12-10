package wepaForum.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Topic {
    @NotBlank
    @Length(max = 100)
    private String subject;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Message> messages;
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public List<Message> getMessages() {
        if (messages == null)
            messages = new ArrayList<>();
        return messages;
    }
    
    public void addMessage(Message message) {
        if (messages == null)
            messages = new ArrayList<>();
        messages.add(message);
    }
    
    public void deleteMessage(Message message) {
        messages.remove(message);
    }
}
