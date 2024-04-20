package ru.stepup.task5.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tpp_product")
@NoArgsConstructor
@Getter
@Setter
public class TppProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code_id")
    private Long productCodeId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "number", length = 50)
    private String number;

    @Column(name = "priority")
    private Long priority;

    @Column(name = "date_of_conclusion")
    private Timestamp dateOfConclusion;

    @Column(name = "start_date_time")
    private Timestamp startDateTime;

    @Column(name = "end_date_time")
    private Timestamp endDateTime;

    @Column(name = "days")
    private Long days;

    @Column(name = "penalty_rate")
    private BigDecimal penaltyRate;

    @Column(name = "nso")
    private BigDecimal nso;

    @Column(name = "threshold_amount")
    private BigDecimal thresholdAmount;

    @Column(name = "requisite_type", length = 50)
    private String requisiteType;

    @Column(name = "interest_rate_type", length = 50)
    private String interestRateType;

    @Column(name = "tax_rate")
    private BigDecimal taxRate;

    @Column(name = "reasone_close", length = 100)
    private String reasoneClose;

    @Column(name = "state", length = 50)
    private String state;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private List<Agreement> agreements = new ArrayList<>();
}
