package com.ayds2.Proyecto.ayds2;

import lombok.Data;

@Data
public class ProductoRAEE {
    private int id;
    private String nombre;
    private int categoria;
    private String descripcion;
    private float precio;

    public ProductoRAEE(int id, String nombre, int categoria, String descripcion, float precio) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
    }
}
