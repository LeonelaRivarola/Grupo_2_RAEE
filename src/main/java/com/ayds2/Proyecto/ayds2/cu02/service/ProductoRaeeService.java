package com.ayds2.Proyecto.ayds2.cu02.service;

import org.springframework.stereotype.Service;
import com.ayds2.Proyecto.ayds2.cu02.dao.iProductoRaeeDAO;
import com.ayds2.Proyecto.ayds2.cu02.model.ProductoRaee;

@Service
public class ProductoRaeeService {

    private final iProductoRaeeDAO productoRaeeDAO; // final para que no cambie al crear el servicio por seguridad, por si se implementara otra

    public ProductoRaeeService(iProductoRaeeDAO productoRaeeDAO) {
        this.productoRaeeDAO = productoRaeeDAO;
    }

    public String getProducto(int id) {
        try {
            String producto = productoRaeeDAO.selectProducto(id);
            if (producto == null) {
                throw new RuntimeException("Producto con id " + id + " no encontrado");
            }
            return producto;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener producto con id " + id + ": " + e.getMessage(), e);
        }
    }
}
