package com.ayds2.Proyecto.ayds2.cu04.model;

import lombok.Data;

@Data
public class ProductoRAEE {
    private int id_ProductoRAEE;
    private String nombre;
    private int categoria_id;
    private String descripcion;
    private float precio;

    public ProductoRAEE(int id, String nombre, int categoria, String descripcion, float precio) {
        this.id_ProductoRAEE = id;
        this.nombre = nombre;
        this.categoria_id = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
    }
}
