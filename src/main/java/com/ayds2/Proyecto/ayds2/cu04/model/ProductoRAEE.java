package com.ayds2.Proyecto.ayds2.cu04.model;

import lombok.Data;

@Data
public class ProductoRAEE {
    private int id_ProductoRAEE;
    private String nombre;
    private int categoria_id;
    private String descripcion;
    private double precio;
    private int stock;
}
