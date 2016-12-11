package wepaForum.service;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import wepaForum.domain.Account;
import wepaForum.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = Logger.getLogger(CustomUserDetailsService.class.getName());
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Account> accounts = accountRepository.findByUsername(username);
        if (accounts.isEmpty()) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        Account account = accounts.get(0);
        LOGGER.log(Level.INFO, "User {0} tried to log in", new Object[]{account.getUsername()});
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(account.getPermission())));
    }
    
    
}
