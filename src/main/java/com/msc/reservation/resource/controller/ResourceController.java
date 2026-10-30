package com.msc.reservation.resource.controller;

import com.msc.reservation.resource.dto.CreateResourceRequest;
import com.msc.reservation.resource.dto.ResourceResponse;
import com.msc.reservation.resource.service.ResourceManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceManagementService resourceService;

    public ResourceController(
            ResourceManagementService resourceService
    ) {
        this.resourceService = resourceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse create(
            @Valid @RequestBody CreateResourceRequest request
    ) {
        return resourceService.create(request);
    }

    @GetMapping("/{id}")
    public ResourceResponse findById(@PathVariable Long id) {
        return resourceService.findById(id);
    }

    @GetMapping
    public List<ResourceResponse> findActiveResources() {
        return resourceService.findActiveResources();
    }
}