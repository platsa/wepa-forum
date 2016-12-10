package wepaForum.controller;

import java.util.Iterator;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaForum.domain.ForumCategory;
import wepaForum.domain.Message;
import wepaForum.domain.SubForum;
import wepaForum.domain.Topic;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;

@Controller
@RequestMapping("/forum")
public class ForumController {
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private ForumCategoryRepository forumCategoryRepository;
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    
    @ModelAttribute
    private ForumCategory getForumCategory() {
        return new ForumCategory();
    }
    
    @ModelAttribute
    private SubForum getSubForum() {       
        return new SubForum();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String view(Model model) {
        model.addAttribute("forums", forumRepository.findAll());
        return "forum";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    public String addForumCategory(
            @Valid @ModelAttribute ForumCategory forumCategory,
            BindingResult bindingResult, @PathVariable("id") Long id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("forums", forumRepository.findAll());
            return "forum";
        }
        
        forumCategoryRepository.save(forumCategory);
        forumRepository.findOne(id).addForumCategory(forumCategory);
        System.out.println("---------------------------Täällä!!");
        return "redirect:/forum";
    }
    
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{forumId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteForumCategory(@PathVariable("forumId") Long forumId, @PathVariable("id") Long id) {
        ForumCategory category = forumCategoryRepository.findOne(id);
        //Poistetaan ensin kaikki jäämät.
        for (Iterator<SubForum> itSub = category.getSubForums().iterator(); itSub.hasNext();) {
            SubForum subForum = itSub.next();
            for (Iterator<Topic> itTopic = subForum.getTopics().iterator(); itTopic.hasNext();) {
                Topic topic = itTopic.next();
                for (Iterator<Message> itMessage = topic.getMessages().iterator(); itMessage.hasNext();) {
                    Message message = itMessage.next();
                    itMessage.remove();
                    messageRepository.delete(message.getId());
                }
                itTopic.remove();
                topicRepository.delete(topic.getId());
            }
            itSub.remove();
            subForumRepository.delete(subForum.getId());
        }
        forumRepository.findOne(forumId).getForumCategories().remove(category);
        forumCategoryRepository.delete(id);
        return "redirect:/forum";
    }
}
