package com.ayds2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ayds2.service.CarritoService;


@RestController
@RequestMapping("/carrito")
public class CarritoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService){
        this.carritoService = carritoService;
    }

    @PostMapping("/{usuarioId}/agregar")
    public ResponseEntity<?> agregarProducto(@PathVariable int usuarioId,
                                            @RequestParam int productoId,
                                            @RequestParam int cantidad) {
        try {
            carritoService.agregarProductoRAEE(usuarioId, productoId, cantidad);
            logger.info("Producto {} agregado al carrito del usuario {} con cantidad {}",  productoId, usuarioId, cantidad);
            
            return ResponseEntity.ok("Producto agregado correctamente");
        } catch (Exception e) {
            logger.error("Error al agregar producto {} al carrito del usuario {}: {}", 
                         productoId, usuarioId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
