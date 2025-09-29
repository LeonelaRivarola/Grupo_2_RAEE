package com.ayds2.Model;

import lombok.Data;

@Data
public class ProductoRaee {
    private int idProductoRAEE;
    private String nombre;
    private double precio;
    private String descripcion;
    private Categoria categoria;
}

