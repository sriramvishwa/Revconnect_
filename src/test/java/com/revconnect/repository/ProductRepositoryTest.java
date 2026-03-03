package com.revconnect.repository;

import com.revconnect.entity.Product;
import com.revconnect.entity.User;
import com.revconnect.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User business;

    @BeforeEach
    void setUp() {
        business = new User();
        business.setUsername("business");
        business.setEmail("biz@example.com");
        business.setPassword("password");
        business.setRole(UserRole.BUSINESS);
        entityManager.persist(business);
        entityManager.flush();
    }

    @Test
    void findByBusiness_ReturnsProducts() {
        Product p = new Product();
        p.setName("Test Product");
        p.setBusiness(business);
        entityManager.persist(p);
        entityManager.flush();

        List<Product> products = productRepository.findByBusiness(business);
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Test Product");
    }
}
