package com.ayds2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds2.model.ProductoRAEE;
import com.ayds2.service.ProductoRAEEService;

@RestController
@RequestMapping("/producto")
public class ProductoRAEEController {

    private final ProductoRAEEService productoService;

    public ProductoRAEEController(ProductoRAEEService productoRAEEService){
        this.productoService = productoRAEEService;
    }

    @GetMapping("/{id}")
    public ProductoRAEE getProductoRAEE(@PathVariable int id){
        return productoService.obtenerProducto(id);
    }
}
