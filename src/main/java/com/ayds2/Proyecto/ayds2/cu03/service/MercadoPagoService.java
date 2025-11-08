package com.ayds2.Proyecto.ayds2.cu03.service;

import com.mercadopago.client.preference.*;
import com.ayds2.Proyecto.ayds2.cu03.model.PagoRequest;
import com.ayds2.Proyecto.ayds2.cu03.model.PagoResponse;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.core.MPRequestOptions;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MercadoPagoService implements IPago {

    @Override
    // Creo una preferencia de pago en Mercado Pago (Modo Sandbox)
    public PagoResponse procesarPago(PagoRequest request) throws Exception {
        // Configuro el producto a pagar
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(request.getDescripcion())
                .quantity(1)
                .unitPrice(request.getMonto())
                .currencyId("ARS")
                .build();
        // Configuro el pagador
        PreferencePayerRequest payer = PreferencePayerRequest.builder()
                .email(request.getEmail())
                .build();

        // Creo la preferencia de pago
        PreferenceRequest prefReq = PreferenceRequest.builder()
                .items(Collections.singletonList(item))
                .payer(payer)
                .externalReference(UUID.randomUUID().toString())
                .build();

        // Envio la solicitud a Mercado Pago
        PreferenceClient client = new PreferenceClient();
        Preference pref = client.create(prefReq,
                MPRequestOptions.builder()
                        .customHeaders(Map.of("X-Idempotency-Key", UUID.randomUUID().toString()))
                        .build());

        // Devuelvo los datos necesarios para iniciar el checkout
        PagoResponse r = new PagoResponse();
        r.setIdPreferencia(pref.getId());
        r.setInitPoint(pref.getInitPoint());
        r.setSandboxInitPoint(pref.getSandboxInitPoint());
        return r;
    }

    /*
     * Dado que no se cuenta con Front para realizar el checkout y completar los webhooks del pago,
     * simulamos la validación del pago asumiendo que siempre es aprobado. Esto se debe a que no 
     * podemos obtener un paymentId real.
     */
    @Override
    public boolean validarPago(String prefId) {
        
        try {
            System.out.println("Simulación: validación omitida para prefId = " + prefId);
            return true;
        } catch (Exception e) {
            System.err.println("Error validando pago: " + e.getMessage());
            return false;
        }
        /* Esto sería para entorno real con pagos reales
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));
            return payment != null && "approved".equalsIgnoreCase(payment.getStatus());
        } catch (Exception e) {
            System.err.println("Error validando pago: " + e.getMessage());
            return false;
        }
        */
    }
}