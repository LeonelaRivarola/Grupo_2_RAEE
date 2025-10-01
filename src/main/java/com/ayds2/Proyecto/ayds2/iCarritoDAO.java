package com.ayds2.Proyecto.ayds2;

public interface iCarritoDAO {
    public String select(int id_carrito);
    public String insert(int id_carrito, int id_ProductoRAEE, int cantidad);
}
