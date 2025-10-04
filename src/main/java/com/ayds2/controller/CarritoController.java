package com.ayds2.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
//import com.fasterxml.jackson.databind.ObjectMapper;

import com.ayds2.service.CarritoService;

//Aún quedaría que los metodos devuelvan en JSON.
//Para eso habría que usar ObjectMapper y cambiar los métodos del Service y del DAO para que devuelvan un String en JSON.

@RestController
public class CarritoController {
    private CarritoService carritoService;
    private static final Logger registraLog = LoggerFactory.getLogger(CarritoController.class);
    //private final ObjectMapper mapper = new ObjectMapper();
    
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/mostrarCarrito/{id_carrito}")
    public String getCarrito(@PathVariable("id_carrito") int id_carrito) {
        registraLog.info("Se ha ingresado al método mostrarCarrito del Controller"); 
        return carritoService.mostrarCarrito(id_carrito);
    }

    @PutMapping("/agregarProductoRAEE/{id_carrito}/{id_ProductoRAEE}/{cantidad}")
    public String putAgregarProductoRAEE(@PathVariable("id_carrito") int id_carrito, @PathVariable("id_ProductoRAEE") int id_ProductoRAEE,@PathVariable("cantidad") int cantidad) {
        registraLog.info("Se ha ingresado al metodo agregarProductoRAEE del Controller"); 
        return carritoService.agregarProductoRAEE(id_carrito, id_ProductoRAEE, cantidad);
    }

}