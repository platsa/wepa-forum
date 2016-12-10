package wepaForum.controller;

import java.util.Iterator;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    
    @RequestMapping(method = RequestMethod.GET)
    public String view(Model model) {
        model.addAttribute("forums", forumRepository.findAll());
        return "forum";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    public String add(@PathVariable("id") Long id, @RequestParam String category) {
        ForumCategory forumCategory = new ForumCategory(category);
        
        forumRepository.findOne(id).getForumCategories().add(forumCategory);
        forumCategoryRepository.save(forumCategory);
        return "redirect:/forum";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{forumId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String delete(@PathVariable("forumId") Long forumId, @PathVariable("id") Long id) {
        ForumCategory category = forumCategoryRepository.findOne(id);
        //Poistetaan kaikki jäämät.
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
        System.out.println("------------------------------" + subForumRepository.findAll().size());
        return "redirect:/forum";
    }
}
