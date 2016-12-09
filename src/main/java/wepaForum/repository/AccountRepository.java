package wepaForum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wepaForum.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
}
