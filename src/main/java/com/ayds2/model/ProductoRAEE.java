package com.ayds2.model;

import lombok.Data;

@Data
public class ProductoRAEE {
    private int id;
    private String nombre;
    private float precio;
    private String descripcion;
    private int categoria_id;
    // private int stock;

    public ProductoRAEE(int id, String nombre, float precio, String descripcion, int categoria_id){ //,int stock)
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.categoria_id = categoria_id;
        //this.stock = stock;
    }
}
