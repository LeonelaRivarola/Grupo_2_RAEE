package com.ayds2.Proyecto.ayds2.model;

import lombok.Data;

@Data
public class DetalleCompra {
    private int iddetalleCompra;  // PK autoincremental
    private int cantidad;
    private double precioUnitario;
    private int id_compra;
    private int id_ProductoRAEE;
}