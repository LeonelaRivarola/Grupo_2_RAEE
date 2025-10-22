package com.ayds2.Proyecto.ayds2.cu03.factory;

import com.ayds2.Proyecto.ayds2.cu03.service.IPagoService;
import com.ayds2.Proyecto.ayds2.cu03.service.MercadoPagoService;

public class PagoFactory{

    public static IPagoService getPagoService(String metodoPago) {
        switch (metodoPago.toLowerCase()) {
            case "mercadopago":
            default:
                return new MercadoPagoService();
        }
    }
}
