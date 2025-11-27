package com.ayds2.Proyecto.ayds2.service;

import org.springframework.stereotype.Service;

import com.ayds2.Proyecto.ayds2.dao.CarritoDAO;

@Service
public class ServiceCarrito{

    private CarritoDAO carritoDAO = new CarritoDAO();

    // CU-01: Agregar a carrito
    public String agregarProductoRAEE(int id_carrito, int id_ProductoRAEE, int cantidad) {
        return carritoDAO.insert(id_carrito, id_ProductoRAEE, cantidad);
    }

    // CU-04: Ver Carrito
    public String verCarrito(int id_carrito) {
        return carritoDAO.select(id_carrito);
    } 

}
