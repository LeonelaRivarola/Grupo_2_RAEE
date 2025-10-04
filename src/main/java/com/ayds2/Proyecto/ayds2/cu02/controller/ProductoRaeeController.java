package com.ayds2.Proyecto.ayds2.cu02.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ayds2.Proyecto.ayds2.cu02.model.ProductoRaee;
import com.ayds2.Proyecto.ayds2.cu02.service.ProductoRaeeService;

@RestController
@RequestMapping("/producto")
public class ProductoRaeeController {

    private final ProductoRaeeService productoService;

    public ProductoRaeeController(ProductoRaeeService productoRaeeService) {
        this.productoService = productoRaeeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoById(@PathVariable int id) {
        try {
            String productoRaee = productoService.getProducto(id);
            return ResponseEntity.ok(productoRaee);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el producto con id " + id);
        }
    }
}
