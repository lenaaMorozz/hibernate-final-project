package com.mer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "country_language", schema = "world")
public class CountryLanguage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    @Column(name = "language", length = 30)
    private String language = "";
    @Column(name = "is_official", columnDefinition = "BIT")
    private Boolean isOfficial = false;
    @Column(name = "percentage", columnDefinition = "decimal(4,1)")
    private BigDecimal percentage = new BigDecimal("0.0");

}
