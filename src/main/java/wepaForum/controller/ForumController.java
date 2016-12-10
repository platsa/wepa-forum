package wepaForum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaForum.repository.ForumRepository;

@Controller
@RequestMapping("/forum")
public class ForumController {
    @Autowired
    private ForumRepository forumRepository;
    
    @RequestMapping(method = RequestMethod.GET)
    public String view(Model model) {
        model.addAttribute("forums", forumRepository.findAll());
        return "forum";
    }
}
