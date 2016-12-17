package wepaForum.controller;

import java.util.Calendar;
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
import wepaForum.domain.Account;
import wepaForum.domain.Message;
import wepaForum.domain.Topic;
import wepaForum.repository.AccountRepository;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.TopicRepository;
import wepaForum.service.DeletingService;

@Controller
@RequestMapping("/topic")
public class TopicController {
    private static final Logger LOGGER = Logger.getLogger(TopicController.class.getName());
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private DeletingService deletingService;
    
    @ModelAttribute("message")
    private Message getMessage() {       
        return new Message();
    }
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") Long id, Model model) {
        model.addAttribute("topic", topicRepository.findOne(id));
        return "topic";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}/users", method = RequestMethod.GET)
    public String viewUsers(@PathVariable("id") Long id, Model model) {
        model.addAttribute("topic", topicRepository.findOne(id));
        return "topicusers";
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    @RequestMapping(value = "{id}", method = RequestMethod.POST)
    @Transactional
    public String addMessage(
            @Valid @ModelAttribute("message") Message message,
            BindingResult bindingResult, @PathVariable("id") Long id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("topic", topicRepository.findOne(id));
            LOGGER.log(Level.INFO, "{0} failed to write a new message", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName()});
            return "topic";
        }
        Account account = accountRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).get(0);
        message.setDate(Calendar.getInstance().getTime());
        message.setUsername(account.getUsername());
        messageRepository.save(message);
        Topic topic = topicRepository.findOne(id);
        topic.addMessage(message);
        topic.addAccount(account);
        account.addTopic(topic);
        LOGGER.log(Level.INFO, "User {0} wrote a message to topic {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), topicRepository.findOne(id).getSubject()});
        return "redirect:/topic/" + id;
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @RequestMapping(value = "{topicId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteMessage(@PathVariable("topicId") Long topicId, @PathVariable("id") Long id) {
        deletingService.deleteMessage(topicId, id);
        LOGGER.log(Level.INFO, "User {0} deleted a message from topic {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), topicRepository.findOne(topicId).getSubject()});
        return "redirect:/topic/" + topicId;
    }
}
