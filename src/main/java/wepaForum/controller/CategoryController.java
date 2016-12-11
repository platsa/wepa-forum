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
import wepaForum.domain.Message;
import wepaForum.domain.SubForum;
import wepaForum.domain.Topic;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;

@Controller
@RequestMapping("/category")
public class CategoryController {
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
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    public String addSubForum(
            @Valid @ModelAttribute("subForum") SubForum subForum,
            BindingResult bindingResult, @PathVariable("id") Long id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("forums", forumRepository.findAll());
            return "forum";
        }
        subForumRepository.save(subForum);
        forumCategoryRepository.findOne(id).addSubForum(subForum);
        
        return "redirect:/forum";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{categoryId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteSubForum(@PathVariable("categoryId") Long categoryId, @PathVariable("id") Long id) {
        SubForum subForum = subForumRepository.findOne(id);
        //Poistetaan ensin kaikki jäämät.
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
        forumCategoryRepository.findOne(categoryId).getSubForums().remove(subForum);
        subForumRepository.delete(id);
        return "redirect:/forum";
    }
}
