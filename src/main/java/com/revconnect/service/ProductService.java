package com.revconnect.service;

import com.revconnect.entity.Product;
import com.revconnect.entity.User;
import com.revconnect.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(User business, Product product) {
        product.setBusiness(business);
        return productRepository.save(product);
    }

    public List<Product> getProductsByBusiness(User business) {
        return productRepository.findByBusiness(business);
    }

    public void deleteProduct(Long productId, Long businessId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getBusiness().getId().equals(businessId)) {
            throw new RuntimeException("Unauthorized");
        }
        productRepository.delete(product);
    }
}
