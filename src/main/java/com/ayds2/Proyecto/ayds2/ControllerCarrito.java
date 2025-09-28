package com.ayds2.Proyecto.ayds2;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class ControllerCarrito {
    private iCarrito serviceCarrito;
    private static final Logger registraLog = LoggerFactory.getLogger(ControllerCarrito.class);
    //private final ObjectMapper mapper = new ObjectMapper();
    
    public ControllerCarrito(iCarrito serviceCarrito) {
        this.serviceCarrito = serviceCarrito;
    }

    @GetMapping("/mostrarCarrito")
    public Carrito getCarrito() { //Que devuelva un carrito es temporal, lo ideal es que devuelva un String en JSON usando ObjectMapper.
        registraLog.info("Se ha ingresado al metodo mostrarCarrito del Controller"); 
        return serviceCarrito.mostrarCarrito();
    }
}
