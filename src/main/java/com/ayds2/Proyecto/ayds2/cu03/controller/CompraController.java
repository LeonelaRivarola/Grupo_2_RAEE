package com.ayds2.Proyecto.ayds2.cu03.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ayds2.Proyecto.ayds2.cu03.service.CompraService;

//http://localhost:8081/compra/comprar?fomraEntrega=domicilio&metodoPago=mercadoPago
@RestController
@RequestMapping("/compra")
public class CompraController {

    @Autowired
    private CompraService compraService;

    //1.1
    //El jsonCarrito va en el body raw, se envia el que se obtenga del caso de uso ver carrito
    @PostMapping("/comprar")
    public String comprar(@RequestParam String formaEntrega,
                          @RequestParam String metodoPago,
                          @RequestBody String jsonCarrito) {
        return compraService.procesarCompra(formaEntrega, metodoPago, jsonCarrito);
    }
}