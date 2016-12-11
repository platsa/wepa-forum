package wepaForum.controller;

import java.util.Calendar;
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
import wepaForum.domain.Message;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.TopicRepository;

@Controller
@RequestMapping("/topic")
public class TopicController {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    
    @ModelAttribute("message")
    private Message getMessage() {       
        return new Message();
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") Long id, Model model) {
        model.addAttribute("topic", topicRepository.findOne(id));
        return "topic";
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    public String addMessage(
            @Valid @ModelAttribute("message") Message message,
            BindingResult bindingResult, @PathVariable("id") Long id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("topic", topicRepository.findOne(id));
            return "topic";
        }
        message.setDate(Calendar.getInstance().getTime());
        message.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        messageRepository.save(message);
        topicRepository.findOne(id).addMessage(message);
        
        return "redirect:/topic/" + id;
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @RequestMapping(value = "{topicId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteTopic(@PathVariable("topicId") Long topicId, @PathVariable("id") Long id) {
        Message message = messageRepository.findOne(id);
        topicRepository.findOne(topicId).deleteMessage(message);
        messageRepository.delete(id);
        return "redirect:/topic/" + topicId;
    }
}
