package com.ayds2.Proyecto.ayds2.cu01.service;

import org.springframework.stereotype.Service;
import com.ayds2.Proyecto.ayds2.cu01.dao.CarritoDAO;

@Service
public class ServiceCarrito{

    private CarritoDAO carritoDAO = new CarritoDAO();

    /* public String mostrarCarrito(int id_carrito) {
        return carritoDAO.select(id_carrito);
    } */

    public String agregarProductoRAEE(int id_carrito, int id_ProductoRAEE, int cantidad) {
        return carritoDAO.insert(id_carrito, id_ProductoRAEE, cantidad);
    }

}
