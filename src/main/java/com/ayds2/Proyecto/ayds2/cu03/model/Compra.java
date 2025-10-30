package com.ayds2.Proyecto.ayds2.cu03.model;

import java.sql.Date;
import java.util.List;
import lombok.Data;

@Data
public class Compra {
    private int idcompra;         // Igual a la columna 'idcompra' (PK autoincremental)
    private Date fecha;           // Coincide con 'fecha'
    private String formaEntrega;  // Coincide con 'formaEntrega'
    private double total;         // Coincide con 'total'
    private int id_Usuario;       // Coincide con 'id_Usuario'
    private int id_metodoPago;    // Coincide con 'id_metodoPago'
    private String idPagoMP;      // Coincide con 'idPagoMP'

    // Relación con detallecompra (no se guarda directamente, solo para registrar detalles)
    private List<DetalleCompra> detalles;
}