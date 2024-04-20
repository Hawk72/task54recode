package ru.stepup.task5.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "tpp_ref_product_register_type")
@NoArgsConstructor
@Getter
@Setter
public class TppRefProductRegisterType {
    @Id
    @Column(name = "internal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", length = 100, nullable = false, unique = true)
    private String value;

    @Column(name = "register_type_name", length = 100, nullable = false)
    private String registerTypeName;

    @Column(name = "register_type_start_date")
    private Timestamp registerTypeStartDate;

    @Column(name = "register_type_end_date")
    private Timestamp registerTypeEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_class_code",
            referencedColumnName = "value",
            nullable = false
    )
    private TppRefProductClass tppRefProductClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "account_type",
            referencedColumnName = "value"
    )
    private TppRefAccountType tppRefAccountType;
}
