package com.ayds2.Proyecto.ayds2.cu01.dao;

public interface iCarritoDAO {
    // CU-01: Agregar a carrito
    public String insert(int id_carrito, int id_ProductoRAEE, int cantidad);
    // CU-04: Ver Carrito
    public String select(int id_carrito);
    // CU-03: Comprar carrito RAEE
    public void deleteDetallesByCarritoId(int id_carrito);
}
