CREATE TABLE products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    added_by BIGINT UNSIGNED NOT NULL,
    edited_by BIGINT UNSIGNED DEFAULT NULL,
    product_category_id BIGINT UNSIGNED NOT NULL,
    price DOUBLE NOT NULL,
    brand_id BIGINT UNSIGNED NOT NULL,
    product_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_category_id) REFERENCES product_categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES product_brands(id) ON DELETE RESTRICT,
    FOREIGN KEY (added_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (edited_by) REFERENCES users(id) ON DELETE SET NULL
)