package com.ayds2.Proyecto.ayds2.cu01.controller;
 
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ayds2.Proyecto.ayds2.cu01.service.ServiceCarrito;

// CU-01: Agregar producto RAEE a Carrito.

@RestController
public class ControllerCarrito {
    private ServiceCarrito serviceCarrito;
    private static final Logger registraLog = LoggerFactory.getLogger(ControllerCarrito.class);
    
    public ControllerCarrito(ServiceCarrito serviceCarrito) {
        this.serviceCarrito = serviceCarrito;
    }

    @PutMapping("/agregarProductoRAEE/{id_carrito}/{id_ProductoRAEE}/{cantidad}") // Agregar producto RAEE al carrito. <--------------------------------
    public String putAgregarProductoRAEE(@PathVariable("id_carrito") int id_carrito, @PathVariable("id_ProductoRAEE") int id_ProductoRAEE,@PathVariable("cantidad") int cantidad) {
        registraLog.info("Se ha ingresado al metodo agregarProductoRAEE del Controller"); 
        return serviceCarrito.agregarProductoRAEE(id_carrito, id_ProductoRAEE, cantidad);
    }

}
