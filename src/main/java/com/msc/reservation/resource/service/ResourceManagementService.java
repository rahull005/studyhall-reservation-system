package com.msc.reservation.resource.service;

import com.msc.reservation.exception.ResourceNotFoundException;
import com.msc.reservation.resource.dto.CreateResourceRequest;
import com.msc.reservation.resource.dto.ResourceResponse;
import com.msc.reservation.resource.entity.ResourceEntity;
import com.msc.reservation.resource.repository.ResourceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceManagementService {

    private final ResourceRepository resourceRepository;

    public ResourceManagementService(
            ResourceRepository resourceRepository
    ) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public ResourceResponse create(CreateResourceRequest request) {
        String name = request.name().trim();

        if (resourceRepository.existsByNameIgnoreCase(name)) {
            throw new DataIntegrityViolationException(
                    "A resource with this name already exists."
            );
        }

        String description = request.description();

        if (description != null) {
            description = description.isBlank()
                    ? null
                    : description.trim();
        }

        ResourceEntity entity =
                new ResourceEntity(name, description);

        return toResponse(resourceRepository.saveAndFlush(entity));
    }

    @Transactional(readOnly = true)
    public ResourceResponse findById(Long id) {
        ResourceEntity entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> findActiveResources() {
        return resourceRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ResourceResponse toResponse(ResourceEntity entity) {
        return new ResourceResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}