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
public class ForumCategory extends AbstractPersistable<Long> {
    @NotBlank
    @Length(min = 1, max = 100)
    private String category;
    @OneToMany(fetch = FetchType.EAGER)
    private List<SubForum> subForums;
    
    public ForumCategory() {}
    
    public ForumCategory(String category) {
        this.category = category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setSubForums(List<SubForum> subForums) {
        this.subForums = subForums;
    }
    
    public List<SubForum> getSubForums() {
        if (subForums == null)
            subForums = new ArrayList<>();
        return subForums;
    }
    
    public void addSubForum(SubForum subForum) {
        if (subForums == null)
            subForums = new ArrayList<>();
        subForums.add(subForum);
    }
    
    public void deleteSubForum(SubForum subForum) {
        subForums.remove(subForum);
    }
}
