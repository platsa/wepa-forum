package wepaForum.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import wepaForum.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUsername(String username);
    List<Account> findByPermission(String permission);
}
