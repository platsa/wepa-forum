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
import wepaForum.domain.ForumCategory;
import wepaForum.domain.SubForum;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.service.DeletingService;

@Controller
@RequestMapping("/forum")
public class ForumController {
    private static final Logger LOGGER = Logger.getLogger(ForumController.class.getName());
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private ForumCategoryRepository forumCategoryRepository;
    @Autowired
    private DeletingService deletingService;
    
    @ModelAttribute("forumCategory")
    private ForumCategory getForumCategory() {
        return new ForumCategory();
    }
    
    @ModelAttribute("subForum")
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
            @Valid @ModelAttribute("forumCategory") ForumCategory forumCategory,
            BindingResult bindingResult, @PathVariable("id") Long id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("forums", forumRepository.findAll());
            LOGGER.log(Level.INFO, "{0} failed to create a new forumcategory", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName()});
            return "forum";
        }
        
        forumCategoryRepository.save(forumCategory);
        forumRepository.findOne(id).addForumCategory(forumCategory);
        LOGGER.log(Level.INFO, "User {0} created forumcategory {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), forumCategory.getCategory()});
        return "redirect:/forum";
    }
    
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{forumId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteForumCategory(@PathVariable("forumId") Long forumId, @PathVariable("id") Long id) {
        String category = forumCategoryRepository.findOne(id).getCategory();
        deletingService.deleteForumCategory(forumId, id);
        LOGGER.log(Level.INFO, "User {0} deleted forumcategory {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), category});
        return "redirect:/forum";
    }
}
