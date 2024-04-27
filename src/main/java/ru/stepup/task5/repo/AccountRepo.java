package ru.stepup.task5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.Account;

@Component
public interface AccountRepo extends JpaRepository<Account, Long> {

}
