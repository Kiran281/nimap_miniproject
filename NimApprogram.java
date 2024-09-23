package com.program;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.util.List;

@SpringBootApplication
public class NimApprogram implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(NimApprogram.class, args);
    }

    @Override
    public void run(String... args) {
        // Initial data can be added here if needed
    }

    @Entity
    public static class Category {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;

        @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
        private List<Product> products;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Product> getProducts() { return products; }
        public void setProducts(List<Product> products) { this.products = products; }
    }

    @Entity
    public static class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;

        @ManyToOne
        @JoinColumn(name = "category_id")
        private Category category;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Category getCategory() { return category; }
        public void setCategory(Category category) { this.category = category; }
    }

    public interface CategoryRepository extends JpaRepository<Category, Long> {}

    public interface ProductRepository extends JpaRepository<Product, Long> {}

    @RestController
    @RequestMapping("/api/categories")
    public static class CategoryController {
        private final CategoryRepository categoryRepository;

        public CategoryController(CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
        }

        @GetMapping
        public Page<Category> getAllCategories(@RequestParam(defaultValue = "0") int page) {
            return categoryRepository.findAll(PageRequest.of(page, 10));
        }

        @PostMapping
        public Category createCategory(@RequestBody Category category) {
            return categoryRepository.save(category);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
            return categoryRepository.findById(id)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}")
        public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
            category.setId(id);
            return ResponseEntity.ok(categoryRepository.save(category));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }

    @RestController
    @RequestMapping("/api/products")
    public static class ProductController {
        private final ProductRepository productRepository;

        public ProductController(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @GetMapping
        public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int page) {
            return productRepository.findAll(PageRequest.of(page, 10));
        }

        @PostMapping
        public Product createProduct(@RequestBody Product product) {
            return productRepository.save(product);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Product> getProductById(@PathVariable Long id) {
            return productRepository.findById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}")
        public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
            product.setId(id);
            return ResponseEntity.ok(productRepository.save(product));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
}
