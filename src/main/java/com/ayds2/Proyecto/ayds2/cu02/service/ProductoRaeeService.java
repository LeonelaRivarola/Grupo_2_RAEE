package com.ayds2.Proyecto.ayds2.cu02.service;

import org.springframework.stereotype.Service;

import com.ayds2.Proyecto.ayds2.cu02.dao.ProductoRaeeDAO;

@Service
public class ProductoRaeeService {

    private final ProductoRaeeDAO productoRaeeDAO; // final para que no cambie al crear el servicio por seguridad, por si se implementara otra

    public ProductoRaeeService(ProductoRaeeDAO productoRaeeDAO) {
        this.productoRaeeDAO = productoRaeeDAO;
    }

    public String getProducto(int id) {
        try {
            String productoRaee = productoRaeeDAO.selectProducto(id);
            if (productoRaee == null) {
                throw new RuntimeException("Producto con id " + id + " no encontrado");
            }
            return productoRaee;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener producto con id " + id + ": " + e.getMessage(), e);
        }
    }
}
