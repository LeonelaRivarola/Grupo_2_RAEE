package com.ayds2.Proyecto.ayds2.controller;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayds2.Proyecto.ayds2.service.ServiceCarrito;

@RestController
@RequestMapping("/carrito")
public class ControllerCarrito {
    private ServiceCarrito serviceCarrito;
    private static final Logger logger = LoggerFactory.getLogger(ControllerCarrito.class);
    
    public ControllerCarrito(ServiceCarrito serviceCarrito) {
        this.serviceCarrito = serviceCarrito;
    }

    // CU-01: Agregar a carrito
    // http://localhost:8081/carrito/agregar?id_carrito=1&id_producto=1&cantidad=1
    @PutMapping("/agregar")
    public String agregarProductoRAEE(@RequestParam int id_carrito,
                                      @RequestParam int id_producto,
                                      @RequestParam int cantidad) {

        logger.info("CU-01 -> ingresar a agregarProductoRAEE del Controller");

        return serviceCarrito.agregarProductoRAEE(id_carrito, id_producto, cantidad);
    }

    // CU-04: Ver carrito
    // http://localhost:8081/carrito/ver?id_carrito=1
    @GetMapping(value = "/ver", produces = MediaType.APPLICATION_JSON_VALUE)
    public String verCarrito(@RequestParam int id_carrito) {

        logger.info("CU-04 -> ingresar a verCarrito del Controller");

        return serviceCarrito.verCarrito(id_carrito);
    }

}
