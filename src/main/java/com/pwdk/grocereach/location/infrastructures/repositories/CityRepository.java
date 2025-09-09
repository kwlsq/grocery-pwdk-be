package com.pwdk.grocereach.location.infrastructures.repositories;

import com.pwdk.grocereach.location.domains.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    List<City> findByProvinceId(Integer provinceId);

}

