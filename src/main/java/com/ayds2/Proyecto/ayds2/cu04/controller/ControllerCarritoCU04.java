package com.ayds2.Proyecto.ayds2.cu04.controller;

import org.springframework.http.MediaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.ayds2.Proyecto.ayds2.cu04.service.ServiceCarritoCU04;

@RestController
public class ControllerCarritoCU04 {
    private ServiceCarritoCU04 serviceCarrito;
    private static final Logger registraLog = LoggerFactory.getLogger(ControllerCarritoCU04.class);
    //private final ObjectMapper mapper = new ObjectMapper();
    
    public ControllerCarritoCU04(ServiceCarritoCU04 serviceCarrito) {
        this.serviceCarrito = serviceCarrito;
    }

    @GetMapping(value = "/verCarrito/{id_carrito}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCarrito(@PathVariable("id_carrito") int id_carrito) {
        registraLog.info("Se ha ingresado al método verCarrito del Controller"); 
        return serviceCarrito.verCarrito(id_carrito);
    } 

}
