package com.ayds2.model;

import lombok.Data;

@Data
public class ProductoRaee {
    private int id_ProductoRAEE;
    private String nombre;
    private double precio;
    private String descripcion;
    private String categoria; // private int categoria_id;
    private int stock;

    public ProductoRaee(int id, String nombre, double precio, String descripcion, String categoria,int stock){
        this.id_ProductoRAEE = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.stock = stock;
    }
}
