package wepaForum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.TopicRepository;

@Controller
@RequestMapping("/topic")
public class MessageController {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") Long id, Model model) {
        model.addAttribute("topic", topicRepository.findOne(id));
        return "topic";
    }
}
