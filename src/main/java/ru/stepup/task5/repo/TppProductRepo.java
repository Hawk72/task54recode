package ru.stepup.task5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.TppProduct;

import java.util.List;

@Component
public interface TppProductRepo extends JpaRepository<TppProduct, Long> {
    public List<TppProduct> findByNumber(String number);
}
