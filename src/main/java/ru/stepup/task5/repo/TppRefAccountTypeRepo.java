package ru.stepup.task5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.TppRefAccountType;

@Component
public interface TppRefAccountTypeRepo extends JpaRepository<TppRefAccountType, Long> {
}
