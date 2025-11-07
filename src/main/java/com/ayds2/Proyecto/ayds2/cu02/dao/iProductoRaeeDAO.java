package com.ayds2.Proyecto.ayds2.cu02.dao;

public interface iProductoRaeeDAO { 
    // CU-02: Ver Producto RAEE
    public String selectProducto(int id); //selecciona por id
    // CU-03: Comprar carrito RAEE
    public void actualizaStock(int idProducto, int cantidad);
}