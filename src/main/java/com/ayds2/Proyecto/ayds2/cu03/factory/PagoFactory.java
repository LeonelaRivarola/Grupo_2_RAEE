package com.ayds2.Proyecto.ayds2.cu03.factory;

import com.ayds2.Proyecto.ayds2.cu03.service.IPago;
import com.ayds2.Proyecto.ayds2.cu03.service.MercadoPagoService;

@org.springframework.stereotype.Component
public class PagoFactory{
    /*
     * Devuelvo la implementación concreta de pago según el tipo solicitado.
     * Actualmente solo usamos Mercado Pago
     */
    public IPago getPago(String tipo) {
        if (tipo == null) tipo = "";
        String t = tipo.toLowerCase();
        if (t.contains("mercado") || t.contains("mp")) {
            return new MercadoPagoService();
        }
        // Si agregamos más métodos de pago, se retornaráin aquí
        return new MercadoPagoService(); // por defecto
    }
}