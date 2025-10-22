package com.ayds2.Proyecto.ayds2.cu03.model;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class Compra {
    private int idCompra;
    private int idUsuario;
    private Date fechaCompra;
    private double total;
    private String formaEntrega;
    private String metodoPago;
    private List<DetalleCompra> detalles;
}