package com.ayds2.Proyecto.ayds2.cu03.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayds2.Proyecto.ayds2.cu03.service.CompraService;

@RestController
@RequestMapping("/compra")
public class CompraController {
    @Autowired
    /*
     * Autowired inyecta la dependencia de CompraService, reemplaza:
     * private CompraService compraService = new CompraService();
     */
    private CompraService compraService;

    @PostMapping("/comprar")
    public String comprar(@RequestParam String formaEntrega, @RequestParam String metodoPago, @RequestBody String jsonCarrito) {
        return compraService.procesarCompra(formaEntrega, metodoPago, jsonCarrito);
    }
}