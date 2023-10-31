package com.mer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "city", schema = "world")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", length = 35)
    private String name = "";
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    @Column(name = "district", length = 20)
    private String district = "";
    private Integer population = 0;
}
