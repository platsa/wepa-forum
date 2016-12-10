package wepaForum.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Forum extends AbstractPersistable<Long> {
    @NotBlank
    @Length(max = 100)
    private String name;
    @OneToMany(fetch = FetchType.EAGER)
    private List<ForumCategory> forumCategories;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setForumCategories(List<ForumCategory> forumCategories) {
        this.forumCategories = forumCategories;
    }
    
    public List<ForumCategory> getForumCategories() {
        if (forumCategories == null)
            forumCategories = new ArrayList<>();
        return forumCategories;
    }
    
    public void addForumCategory(ForumCategory forumCategory) {
        if (forumCategories == null)
            forumCategories = new ArrayList<>();
        forumCategories.add(forumCategory);
    }
    
    public void deleteForumCategory(ForumCategory forumCategory) {
        forumCategories.remove(forumCategory);
    }
}
