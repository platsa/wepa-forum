package wepaForum.service;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wepaForum.domain.Account;
import wepaForum.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(account.getPermission())));
    }
    
    @PostConstruct
    public void init() {
        if (accountRepository.findAll().size() == 0) {
            Account user = new Account("user", passwordEncoder.encode("user"), "USER");
            accountRepository.save(user);
            
            Account admin = new Account("admin", passwordEncoder.encode("admin"), "ADMIN");
            accountRepository.save(admin);
            
            Account moderator = new Account("moderator", passwordEncoder.encode("moderator"), "MODERATOR");
            accountRepository.save(moderator);
        }

    }
}
