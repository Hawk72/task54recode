package ru.stepup.task5.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account_pool")
@NoArgsConstructor
@Getter
@Setter
public class AccountPool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_code", length = 50)
    private String branchCode;

    @Column(name="currency_code", length = 30)
    private String currencyCode;

    @Column(name = "mdm_code", length = 50)
    private String mdmCode;

    @Column(name = "priority_code", length = 30)
    private String priorityCode;

    @Column(name = "registry_type_code", length = 50)
    private String registryTypeCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "account_pool_id")
    private List<Account> accounts = new ArrayList<>();

    @Override
    public String toString() {
        return "AccountPool{" +
                "id=" + id +
                ", branchCode='" + branchCode + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", mdmCode='" + mdmCode + '\'' +
                ", priorityCode='" + priorityCode + '\'' +
                ", registryTypeCode='" + registryTypeCode + '\'' +
                ", loginAccounts=" + accounts +
                '}';
    }
}
