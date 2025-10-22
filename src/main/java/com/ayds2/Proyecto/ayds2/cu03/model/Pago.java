package com.ayds2.Proyecto.ayds2.cu03.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
/*  AllArgsContructor genera un constructor con todos los atributos de la siguiente forma:
 *  public Pago(boolean exito, String mensaje) {
     this.exito = exito;
     this.mensaje = mensaje;
    }
 */
public class Pago {
    private Boolean pagoExitoso;
    private String mensaje;
}
