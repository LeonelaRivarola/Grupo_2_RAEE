package com.ayds2.service;

import org.springframework.stereotype.Service;

import com.ayds2.dao.ProductoRaeeDAO;
import com.ayds2.dao.iCarritoDAO;
import com.ayds2.model.ProductoRaee;

@Service
public class CarritoService {

    private final iCarritoDAO carritoDAO;
    private final ProductoRaeeDAO productoRaeeDAO;

    public CarritoService(iCarritoDAO carritoDAO, ProductoRaeeDAO productoRaeeDAO) {
        this.carritoDAO = carritoDAO;
        this.productoRaeeDAO = productoRaeeDAO;
    }

    public void agregarProductoRAEE(int usuarioId, int productoId, int cantidad) {
       
        ProductoRaee producto = productoRaeeDAO.selectProducto(productoId);
        
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }
        if (cantidad > producto.getStock()) {
            throw new RuntimeException("No hay stock suficiente");
        }

        int carritoId = carritoDAO.getCarritoIdByUsuario(usuarioId);
        if (carritoId == 0) {
            carritoId = carritoDAO.crearCarrito(usuarioId);
        }

        carritoDAO.insert(carritoId, productoId, cantidad);
    }

}
