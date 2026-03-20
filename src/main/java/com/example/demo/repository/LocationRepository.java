package com.example.demo.repository;

import com.example.demo.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
