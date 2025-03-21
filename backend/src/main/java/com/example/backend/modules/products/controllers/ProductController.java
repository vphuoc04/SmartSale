package com.example.backend.modules.products.controllers;
import java.util.List;
import java.util.Map;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

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

import com.example.backend.modules.products.entities.Product;
import com.example.backend.modules.products.entities.ProductImage;
import com.example.backend.modules.products.requests.Product.StoreRequest;
import com.example.backend.modules.products.requests.Product.UpdateRequest;
import com.example.backend.modules.products.resources.ProductResource;
import com.example.backend.modules.products.services.interfaces.ProductServiceInterface;
import com.example.backend.resources.ApiResource;
import com.example.backend.services.JwtService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1")
public class ProductController {
    private final ProductServiceInterface productService;
    //private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private JwtService jwtService;

    public ProductController(
        ProductServiceInterface productService
    ){
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<?> store(@Valid @RequestBody StoreRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            String userId = jwtService.getUserIdFromJwt(token);

            Long addedBy = Long.valueOf(userId);


            Product product = productService.create(request, addedBy);

            ProductResource productResource = ProductResource.builder()
                .id(product.getId())
                .addedBy(product.getAddedBy())
                .editedBy(product.getEditedBy())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .imageUrls(product.getImages() != null 
                    ? product.getImages().stream().map(ProductImage::getImageUrl).toList() 
                    : List.of())
                .build();

            ApiResource<ProductResource> response = ApiResource.ok(productResource, "Product created successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ApiResource.message(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            String userId = jwtService.getUserIdFromJwt(token);

            Long editedBy = Long.valueOf(userId);

            Product product = productService.update(id, request, editedBy);

            ProductResource productResource = ProductResource.builder()
                .id(product.getId())
                .addedBy(product.getAddedBy())
                .editedBy(product.getEditedBy())
                .productCode(product.getProductCode())
                .name(product.getName())
                .price(product.getPrice())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .imageUrls(product.getImages() != null 
                    ? product.getImages().stream().map(ProductImage::getImageUrl).toList() 
                    : List.of())
                .build();

            ApiResource<ProductResource> response = ApiResource.ok(productResource, "Product updated successfully");

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.message(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }
    }

    @GetMapping("/product")
    public ResponseEntity<?> index(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();

        Page<Product> products = productService.paginate(parameters);

        Page<ProductResource> productResource = products.map(product->
            ProductResource.builder()
            .id(product.getId())
            .addedBy(product.getAddedBy())
            .editedBy(product.getEditedBy())
            .productCode(product.getProductCode())
            .name(product.getName())
            .price(product.getPrice())
            .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
            .imageUrls(product.getImages() != null 
                ? product.getImages().stream().map(ProductImage::getImageUrl).toList() 
                : List.of())
            .build()
        );

        ApiResource<Page<ProductResource>> response = ApiResource.ok(productResource, "Fetch product data successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean deleted = productService.delete(id);

            if (deleted) {
                return ResponseEntity.ok(
                    ApiResource.message("Product deleted successfully", HttpStatus.OK)
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
                ApiResource.error("INTERNAL_SERVER_ERROR", "Error", HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }   
    }
}
