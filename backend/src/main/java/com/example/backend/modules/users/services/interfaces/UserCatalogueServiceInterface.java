package com.example.backend.modules.users.services.interfaces;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.backend.modules.users.entities.UserCatalogue;
import com.example.backend.modules.users.requests.UserCatalogue.StoreRequest;

public interface UserCatalogueServiceInterface {
    UserCatalogue create(StoreRequest request);
    Page<UserCatalogue> paginate(Map<String, String[]> parameters);
}
