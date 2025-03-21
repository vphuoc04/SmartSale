package com.example.backend.modules.users.services.interfaces;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.backend.modules.users.entities.UserCatalogue;
import com.example.backend.modules.users.requests.UserCatalogue.StoreRequest;
import com.example.backend.modules.users.requests.UserCatalogue.UpdateRequest;

public interface UserCatalogueServiceInterface {
    UserCatalogue create(StoreRequest request, Long createdBy);
    Page<UserCatalogue> paginate(Map<String, String[]> parameters);
    UserCatalogue update(Long id, UpdateRequest request, Long updatedBy);
    boolean delete(Long id);
}
