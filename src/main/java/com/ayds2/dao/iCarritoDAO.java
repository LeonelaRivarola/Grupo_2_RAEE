package com.ayds2.dao;

public interface iCarritoDAO {
    void insert(int carritoId, int productoId, int cantidad);
    int getCarritoIdByUsuario(int usuarioId);
    int crearCarrito(int usuarioId);
}
