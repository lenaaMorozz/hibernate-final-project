package com.mer.redis;

import com.mer.domain.Continent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityCountry {
        private Integer id;

        private String name;

        private String district;

        private Integer population;

        private String code;

        private String code2;

        private String countryName;

        private Continent continent;

        private String countryRegion;

        private BigDecimal countrySurfaceArea;

        private Integer countryPopulation;

        private Set<Language> languages;
    }
