package wepaForum.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wepaForum.domain.Account;
import wepaForum.repository.AccountRepository;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final String permission = "USER";
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @ModelAttribute("account")
    private Account getAccount() {       
        Account account = new Account();
        account.setPermission(permission);
        return account;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String view() {
        return "registration";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String addAccount(
            @Valid @ModelAttribute("account") Account account,
            BindingResult bindingResult) {
        if (!accountRepository.findByUsername(account.getUsername()).isEmpty()) {
            bindingResult.addError(new FieldError("account", "username", "username already taken"));
        }
        if (bindingResult.hasErrors()) {
            System.out.println("----------------------" + bindingResult);
            return "registration";
        }        
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
        return "redirect:/login";
    }
}
