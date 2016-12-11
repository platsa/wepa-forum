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
import wepaForum.domain.SubForum;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.service.DeletingService;

@Controller
@RequestMapping("/category")
public class CategoryController {
    private static final Logger LOGGER = Logger.getLogger(CategoryController.class.getName());
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private ForumCategoryRepository forumCategoryRepository;
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private DeletingService deletingService;
    
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
        LOGGER.log(Level.INFO, "User {0} created subforum {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), subForum.getSubject()});
        return "redirect:/forum";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{categoryId}/{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteSubForum(@PathVariable("categoryId") Long categoryId, @PathVariable("id") Long id) {
        String subject = subForumRepository.findOne(id).getSubject();
        deletingService.deleteSubForum(categoryId, id);
        LOGGER.log(Level.INFO, "User {0} deleted subforum {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), subject});
        return "redirect:/forum";
    }
}
