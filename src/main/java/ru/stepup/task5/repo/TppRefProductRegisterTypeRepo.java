package ru.stepup.task5.repo;

import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.TppRefProductClass;
import ru.stepup.task5.entity.TppRefProductRegisterType;

import java.util.List;

@Component
public interface TppRefProductRegisterTypeRepo  extends JpaRepository<TppRefProductRegisterType, Long> {
    public TppRefProductRegisterType findByValue(String value);

    @Query(value="SELECT current_schema() as schema")
    public String getSchema();

    public default String getTableName() {
        return TppRefProductRegisterType.class.getAnnotation(Table.class).name();

    }

    public List<TppRefProductRegisterType> findByTppRefProductClass(TppRefProductClass tppRefProductClass);
}
