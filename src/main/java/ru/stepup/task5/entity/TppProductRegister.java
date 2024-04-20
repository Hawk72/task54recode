package ru.stepup.task5.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tpp_product_register")
@NoArgsConstructor
@Getter
@Setter
public class TppProductRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "type",
            referencedColumnName = "value",
            nullable = false
    )
    private TppRefProductRegisterType tppRefProductRegisterType;

    @Column(name = "account")
    private Long account;

    @Column(name = "currency_code", length = 30)
    private String currencyCode;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "account_number", length = 25)
    private String accountNumber;
}
