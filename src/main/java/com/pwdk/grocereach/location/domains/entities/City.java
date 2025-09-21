package com.pwdk.grocereach.location.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
public class City {
    @Id
    @Column(name = "city_code")
    private Integer id;

    @Column(name = "city_name")
    private String name;

    @Column(name = "rajaongkir_id", unique = true)
    private Integer rajaOngkirId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_province_code", referencedColumnName = "province_code")
    @JsonIgnore
    private Province province;
}

