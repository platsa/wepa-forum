package wepaForum.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaForum.domain.Account;
import wepaForum.repository.AccountRepository;

@Controller
@RequestMapping("/users")
public class UserController {
    private final String[] permissions = {"USER", "MODERATOR", "ADMIN"};
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @ModelAttribute("account")
    private Account getAccount() {       
        return new Account();
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    public String view(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());        
        model.addAttribute("permissions", permissions);
        return "users";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    public String addAccount(
            @Valid @ModelAttribute("account") Account account,
            BindingResult bindingResult, Model model) {
        if (!accountRepository.findByUsername(account.getUsername()).isEmpty()) {
            bindingResult.addError(new FieldError("account", "username", "username already taken"));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountRepository.findAll());        
            model.addAttribute("permissions", permissions);
            LOGGER.log(Level.INFO, "User {0} failed to create a new user", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName()});
            return "users";
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
        LOGGER.log(Level.INFO, "User {0} created user {1} with permission {2}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), account.getUsername(), account.getPermission()});
        return "redirect:/users";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteAccount(@PathVariable("id") Long id) {
        String username = accountRepository.findOne(id).getUsername();
        accountRepository.delete(id);
        LOGGER.log(Level.INFO, "User {0} deleted user {1}", new Object[]{SecurityContextHolder.getContext().getAuthentication().getName(), username});
        return "redirect:/users";
    }
}
