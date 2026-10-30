package com.msc.reservation.resource.repository;

import com.msc.reservation.resource.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository
        extends JpaRepository<ResourceEntity, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<ResourceEntity> findByActiveTrueOrderByNameAsc();
}