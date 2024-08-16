package com.spring.boot.webflux.security.app.persistence.documents;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "productos")
public class Product {

    @Id
    private String id;

    @NotEmpty
    private String name;

    private BigDecimal price;

    private ProductStatus status;

    @Valid
    @NotNull
    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(@Valid @NotNull Category category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotEmpty String getName() {
        return name;
    }

    public void setName(@NotEmpty String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public static enum ProductStatus{
        ENABLED,DISABLED
    }

}
