package com.example.backend.modules.attributes.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.modules.attributes.entities.Attribute;
import com.example.backend.modules.attributes.mappers.AttributeMapper;
import com.example.backend.modules.attributes.mappers.AttributeValueMapper;
import com.example.backend.modules.attributes.requests.Attribute.StoreRequest;
import com.example.backend.modules.attributes.requests.Attribute.UpdateRequest;
import com.example.backend.modules.attributes.resources.AttributeResource;
import com.example.backend.modules.attributes.services.interfaces.AttributeServiceInterface;
import com.example.backend.resources.ApiResource;
import com.example.backend.services.JwtService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1")
public class AttributeController {
    private final AttributeServiceInterface attributeService;
    private final AttributeMapper attributeMapper;

    @Autowired
    private JwtService jwtService;

    public AttributeController(
        AttributeServiceInterface attributeService,
        AttributeValueMapper attributeValueMapper,
        AttributeMapper attributeMapper,
        JwtService jwtService
    ){
        this.attributeService = attributeService;
        this.attributeMapper = attributeMapper;
        this.jwtService = jwtService;
    }

    @PostMapping("/attribute") 
    public ResponseEntity<?> store(@Valid @RequestBody StoreRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            String userId = jwtService.getUserIdFromJwt(token);
            Long addedBy = Long.valueOf(userId);

            Attribute attribute = attributeService.add(request, addedBy);
            AttributeResource attributeResource = attributeMapper.toResource(attribute);
            ApiResource<AttributeResource> response = ApiResource.ok(attributeResource, "Attribute added successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("NETWORK_ERROR", HttpStatus.UNAUTHORIZED));
        }
    }

    @PutMapping("/attribute/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            String userId = jwtService.getUserIdFromJwt(token);
            Long editedBy = Long.valueOf(userId);

            Attribute attribute = attributeService.edit(id, request, editedBy);
            AttributeResource attributeResource = attributeMapper.toResource(attribute);
            ApiResource<AttributeResource> response = ApiResource.ok(attributeResource, "Attribute edited successfully");

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error("INTERNAL_SERVER_ERROR", "ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }
    }

    @GetMapping("/attribute")
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        Page<Attribute> attributes = attributeService.paginate(parameters);
        Page<AttributeResource> attributeResource = attributeMapper.toPageResource(attributes);
        ApiResource<Page<AttributeResource>> response = ApiResource.ok(attributeResource, "Attribute fetch success");
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/attribute/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean deleted = attributeService.delete(id);

            if (deleted) {
                return ResponseEntity.ok(
                    ApiResource.message("Attribute deleted successfully", HttpStatus.OK)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResource.error("NOT_FOUND", "Error", HttpStatus.NOT_FOUND)
                );
            }

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error("INTERNAL_SERVER_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }   
    }
}
