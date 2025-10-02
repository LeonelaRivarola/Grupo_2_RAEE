package com.ayds2.service;

import org.springframework.stereotype.Service;

import com.ayds2.dao.iProductoRaeeDAO;
import com.ayds2.model.ProductoRaee;

@Service
public class ProductoRaeeService {

    private final iProductoRaeeDAO productoRaeeDAO;

    public ProductoRaeeService(iProductoRaeeDAO productoRaeeDAO) {
        this.productoRaeeDAO = productoRaeeDAO;
    }

    public ProductoRaee obtenerProducto(int id) {
        ProductoRaee producto = productoRaeeDAO.getProducto(id);
        if (producto == null) {
            throw new RuntimeException("Producto con id " + id + " no encontrado");
        }
        return producto;
    }
}
