package com.ayds2.service;

import org.springframework.stereotype.Service;

import com.ayds2.dao.iProductoRAEEDAO;
import com.ayds2.model.ProductoRAEE;

@Service
public class ProductoRAEEService implements iProductoRAEEService {

    private final iProductoRAEEDAO productoRAEEDAO;

    public ProductoRAEEService(iProductoRAEEDAO productoRAEEDAO) {
        this.productoRAEEDAO = productoRAEEDAO;
    }

    @Override
    public ProductoRAEE obtenerProducto(int id) {
        return productoRAEEDAO.getProducto(id);
    }

}
