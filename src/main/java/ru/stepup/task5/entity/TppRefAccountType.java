package ru.stepup.task5.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tpp_ref_account_type")
@NoArgsConstructor
@Getter
@Setter
public class TppRefAccountType {
    @Id
    @Column(name = "internal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", length = 100, nullable = false, unique = true)
    private String value;

}
