package wepaForum.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaForum.domain.Topic;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;
import wepaForum.service.DeletingService;

@Controller
@RequestMapping("/subforum")
public class SubForumController {
    private static final Logger LOGGER = Logger.getLogger(SubForumController.class.getName());
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private DeletingService deletingService;
    
    @ModelAttribute("topic")
    private Topic getTopic() {       
        return new Topic();
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") Long id, Model model) {
        model.addAttribute("subForum", subForumRepository.findOne(id));
        return "subforum";
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    public String addTopic(
            @Valid @ModelAttribute("topic") Topic topic,
            BindingResult bindingResult, @PathVariable("id") Long id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("subForum", subForumRepository.findOne(id));
            return "subforum";
        }
        topicRepository.save(topic);
        subForumRepository.findOne(id).addTopic(topic);
        LOGGER.log(Level.INFO, "User {0} created topic {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), topic.getSubject()});
        return "redirect:/subforum/" + id;
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @RequestMapping(value = "{subforumId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteTopic(@PathVariable("subforumId") Long subforumId, @PathVariable("id") Long id) {
        String subject = topicRepository.findOne(id).getSubject();
        deletingService.deleteTopic(subforumId, id);
        LOGGER.log(Level.INFO, "User {0} deleted subject {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), subject});
        return "redirect:/subforum/" + subforumId;
    }
}
