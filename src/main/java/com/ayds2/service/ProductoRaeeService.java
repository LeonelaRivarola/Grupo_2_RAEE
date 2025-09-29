package com.ayds2.service;

import com.ayds2.Model.ProductoRaee;
import com.ayds2.dao.IRaeeDAO;

public class ProductoRaeeService implements IProductoRaeeService{
    private final IRaeeDAO raeeDAO;

    public ProductoRaeeService(IRaeeDAO raeeDAO) {
        this.raeeDAO = raeeDAO;
    }

    @Override
    public ProductoRaee getProductoById(int id) {
        return raeeDAO.selectById(id);
    }
    
}
