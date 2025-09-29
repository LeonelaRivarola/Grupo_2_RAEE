package com.ayds2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayds2.Model.ProductoRaee;
import com.ayds2.service.IProductoRaeeService;

@RestController
@RequestMapping("/raee")
public class ProductoRaeeController {

    private final IProductoRaeeService raeeService;

    public ProductoRaeeController(IProductoRaeeService raeeService) {
        this.raeeService = raeeService;
    }

    @GetMapping("/getProductoById")
    public ProductoRaee getProductoById(@RequestParam int id) {
        return raeeService.getProductoById(id);
    }
}