package com.ayds2.Proyecto.ayds2.cu03.model;

import lombok.Data;

@Data
public class DetalleCompra {
    private int idDetalleCompra;
    private int idProductoRaee;
    private int idCompra;
    private int cantidad;
    private double precioUnitario;
}