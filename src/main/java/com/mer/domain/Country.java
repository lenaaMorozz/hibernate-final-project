package com.mer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "country", schema = "world")
public class Country {
    @Id
    private Integer id;
    @Column(name = "code", length = 3)
    private String code = "";
    @Column(name = "code_2", length = 2)
    private String code2 = "";
    @Column(name = "name", length = 52)
    private String name = "";
    @Enumerated(EnumType.ORDINAL)
    private Continent continent = Continent.ASIA;
    @Column(name = "region", length = 26)
    private String region = "";
    @Column(name = "surface_area", columnDefinition = "decimal(10,2)")
    private BigDecimal surfaceArea = new BigDecimal("0.00");
    @Column(name = "indep_year")
    private Short indepYear;
    private Integer population = 0;
    @Column(name = "life_expectancy", columnDefinition = "decimal(3,1)")
    private BigDecimal lifeExpectancy;
    @Column(name = "gnp", columnDefinition = "decimal(10,2)")
    private BigDecimal gnp;
    @Column(name = "gnpo_id", columnDefinition = "decimal(10,2)")
    private BigDecimal gnpoId;
    @Column(name = "local_name", length = 45)
    private String localName = "";
    @Column(name = "government_form", length = 45)
    private String governmentForm = "";
    @Column(name = "head_of_state", length = 60)
    private String headOfState;
    @OneToOne
    @JoinColumn(name = "capital")
    private City capital;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Set<CountryLanguage> languages;

}
