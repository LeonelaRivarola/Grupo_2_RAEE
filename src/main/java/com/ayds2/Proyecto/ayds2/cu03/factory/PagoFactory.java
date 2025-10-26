package com.ayds2.Proyecto.ayds2.cu03.factory;

import com.ayds2.Proyecto.ayds2.cu03.service.IPago;
import com.ayds2.Proyecto.ayds2.cu03.service.MercadoPagoService;

@org.springframework.stereotype.Component
public class PagoFactory{
    // Por ahora siempre devolvemos MercadoPago si el tipo contiene "mercado" o "mp"
    public IPago getPago(String tipo) {
        if (tipo == null) tipo = "";
        String t = tipo.toLowerCase();
        if (t.contains("mercado") || t.contains("mp")) {
            return new MercadoPagoService();
        }
        // Si agregás otros métodos de pago, acá los devuelves.
        return new MercadoPagoService(); // fallback
    }
}