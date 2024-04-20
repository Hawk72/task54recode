package ru.stepup.task5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.TppProductRegister;

import java.util.List;

@Component
public interface TppProductRegisterRepo extends JpaRepository<TppProductRegister, Long> {
    public List<TppProductRegister> findByProductId(long productId);
}
