package com.ayds2.Proyecto.ayds2.cu02.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ayds2.Proyecto.ayds2.cu02.service.ProductoRaeeService;

@RestController
@RequestMapping("/producto")
public class ProductoRaeeController {

    private final ProductoRaeeService productoService;
    private static final Logger logger = LoggerFactory.getLogger(ProductoRaeeController.class);

    public ProductoRaeeController(ProductoRaeeService productoRaeeService) {
        this.productoService = productoRaeeService;
    }

    // CU-02: Ver Producto RAEE
    // http://localhost:8081/producto/ver?id=1
    @GetMapping(value = "/ver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductoById(@RequestParam int id) {

        logger.info("CU-02 -> ver Producto RAEE con id {}", id);

        try {
            String productoRaee = productoService.getProducto(id);
            return ResponseEntity.ok(productoRaee);

        } catch (Exception e) {
            logger.error("Error CU-02 al obtener producto id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el producto con id " + id);
        }
    }
}
