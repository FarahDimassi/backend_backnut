package com.example.backnut.controllers;

import com.example.backnut.models.ScannedProduct;
import com.example.backnut.repository.ScannedProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/scannedproducts")
public class ScannedProductController {

    @Autowired
    private ScannedProductRepository repository;

    @PostMapping
    public ResponseEntity<ScannedProduct> save(@RequestBody ScannedProduct product) {
        product.setScanDate(LocalDateTime.now());
        return ResponseEntity.ok(repository.save(product));
    }
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<ScannedProduct>> getByUserIdAndDate(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScannedProduct> products = repository.findByUserIdAndExactDate(userId, date);
        return ResponseEntity.ok(products);
    }
}

