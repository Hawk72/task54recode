package ru.stepup.task5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.TppRefProductClass;

@Component
public interface TppRefProductClassRepo extends JpaRepository<TppRefProductClass, Long> {
    public TppRefProductClass findByValue(String value);

}
