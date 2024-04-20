package ru.stepup.task5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ru.stepup.task5.entity.AccountPool;

@Component
public interface AccountPoolRepo extends JpaRepository<AccountPool, Long> {
    @Query(value = "select * from account_pool a where branch_code = coalesce(:branch_code, branch_code) and currency_code = coalesce(:currency_code, currency_code) and mdm_code = coalesce(:mdm_code, mdm_code) and priority_code = coalesce(:priority_code, priority_code) and registry_type_code = coalesce(:registry_type_code, registry_type_code) FETCH FIRST 1 ROWS ONLY"
        ,nativeQuery = true)
    public AccountPool findAccountPool(@Param("branch_code") String branchCode, @Param("currency_code") String currencyCode, @Param("mdm_code") String mdmCode, @Param("priority_code") String priorityCode, @Param("registry_type_code") String registryTypeCode);

}
