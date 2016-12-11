package wepaForum.controller;

import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaForum.domain.Account;
import wepaForum.repository.AccountRepository;

@Controller
@RequestMapping("/users")
public class UserController {
    final String[] permissions = {"USER", "MODERATOR", "ADMIN"};
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
        if (bindingResult.hasErrors()) {
            model.addAttribute("accounts", accountRepository.findAll());        
            model.addAttribute("permissions", permissions);
            return "users";
        }        
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
        return "redirect:/users";
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public String deleteAccount(@PathVariable("id") Long id) {
        accountRepository.delete(id);
        return "redirect:/users";
    }
}
