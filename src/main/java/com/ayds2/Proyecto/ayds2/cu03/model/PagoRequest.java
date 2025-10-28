package com.ayds2.Proyecto.ayds2.cu03.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/*  
    AllArgsContructor genera un constructor con todos los atributos en el orden que aparecen
*/
public class PagoRequest {
    private BigDecimal monto;
    private String descripcion;
    private String metodoPago;
    private String email;
}