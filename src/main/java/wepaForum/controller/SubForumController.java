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
import wepaForum.domain.Topic;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;

@Controller
@RequestMapping("/subforum")
public class SubForumController {
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    
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
        
        return "redirect:/subforum/" + id;
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @RequestMapping(value = "{subforumId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteTopic(@PathVariable("subforumId") Long subforumId, @PathVariable("id") Long id) {
        Topic topic = topicRepository.findOne(id);
        //Poistetaan ensin kaikki jäämät.
        for (Iterator<Message> itMessage = topic.getMessages().iterator(); itMessage.hasNext();) {
            Message message = itMessage.next();
            itMessage.remove();
            messageRepository.delete(message.getId());
        }
        subForumRepository.findOne(subforumId).deleteTopic(topic);
        topicRepository.delete(id);
        return "redirect:/subforum/" + subforumId;
    }
}
