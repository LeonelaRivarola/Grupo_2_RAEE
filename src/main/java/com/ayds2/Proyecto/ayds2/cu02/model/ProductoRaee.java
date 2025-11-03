package com.ayds2.Proyecto.ayds2.cu02.model;

import lombok.Data;

@Data
public class ProductoRaee {
    private int id_productoRAEE;
    private String nombre;
    private double precio;
    private String descripcion;
    private int stock;
    private int categoria_id;

    public ProductoRaee() {
    }

    public ProductoRaee(int id, String nombre, double precio, String descripcion, int stock, int categoria_id){
        this.id_productoRAEE = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.categoria_id = categoria_id;
        this.stock = stock;
    }
}
