package com.ayds2.model;

import lombok.Data;

@Data
public class ProductoRaee {
    private int id_ProductoRAEE;
    private String nombre;
    private double precio;
    private String descripcion;
    private int categoria_id;
    private int stock;

    public ProductoRaee(int id, String nombre, double precio, String descripcion, int categoria_id){ //,int stock)
        this.id_ProductoRAEE = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.categoria_id = categoria_id;
        this.stock = stock;
    }
}
