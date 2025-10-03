package com.ayds2.Proyecto.ayds2.cu01.controller;

//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import com.fasterxml.jackson.databind.ObjectMapper;
 
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ayds2.Proyecto.ayds2.cu01.service.ServiceCarrito;


//Aún quedaría que los metodos devuelvan en JSON.
//Para eso habría que usar ObjectMapper y cambiar los métodos del Service y del DAO para que devuelvan un String en JSON.

@RestController
public class ControllerCarrito {
    private ServiceCarrito serviceCarrito;
    private static final Logger registraLog = LoggerFactory.getLogger(ControllerCarrito.class);
    //private final ObjectMapper mapper = new ObjectMapper();
    
    public ControllerCarrito(ServiceCarrito serviceCarrito) {
        this.serviceCarrito = serviceCarrito;
    }

   /*  @GetMapping(value = "/mostrarCarrito/{id_carrito}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCarrito(@PathVariable("id_carrito") int id_carrito) {
        registraLog.info("Se ha ingresado al método mostrarCarrito del Controller"); 
        return serviceCarrito.mostrarCarrito(id_carrito);
    } */

    @PutMapping("/agregarProductoRAEE/{id_carrito}/{id_ProductoRAEE}/{cantidad}") // Agregar producto RAEE al carrito. <--------------------------------
    public String putAgregarProductoRAEE(@PathVariable("id_carrito") int id_carrito, @PathVariable("id_ProductoRAEE") int id_ProductoRAEE,@PathVariable("cantidad") int cantidad) {
        registraLog.info("Se ha ingresado al metodo agregarProductoRAEE del Controller"); 
        return serviceCarrito.agregarProductoRAEE(id_carrito, id_ProductoRAEE, cantidad);
    }

}
