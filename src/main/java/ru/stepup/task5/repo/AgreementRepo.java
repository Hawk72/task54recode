package ru.stepup.task5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.Agreement;

import java.util.List;

@Component
public interface AgreementRepo extends JpaRepository<Agreement, Long> {
    public List<Agreement> findByNumber(String number);
}
