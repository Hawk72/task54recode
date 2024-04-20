package ru.stepup.task5.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tpp_ref_product_class")
@NoArgsConstructor
@Getter
@Setter
public class TppRefProductClass {
    @Id
    @Column(name = "internal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", length = 100, nullable = false, unique = true)
    private String value;

    @Column(name = "gbi_code", length = 50)
    private String gbiCode;

    @Column(name = "gbi_name", length = 100)
    private String gbiName;

    @Column(name = "product_row_code", length = 50)
    private String productRowCode;

    @Column(name = "product_row_name", length = 100)
    private String productRowName;

    @Column(name = "subclass_code", length = 50)
    private String subclassCode;

    @Column(name = "subclass_name", length = 100)
    private String subclassName;

}
