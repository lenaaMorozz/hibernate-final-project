package com.mer.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "country_language", schema = "world")
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "language")
    private String languageName;

    @Column(name = "is_official", columnDefinition = "bit")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isOfficial;

    private BigDecimal percentage;
}
