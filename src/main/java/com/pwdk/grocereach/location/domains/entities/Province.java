package com.pwdk.grocereach.location.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@NoArgsConstructor
public class Province {
    @Id
    @Column(name = "province_code")
    private Integer id;

    @Column(name = "province_name")
    private String name;

    @OneToMany(mappedBy = "province", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<City> cities;
}
