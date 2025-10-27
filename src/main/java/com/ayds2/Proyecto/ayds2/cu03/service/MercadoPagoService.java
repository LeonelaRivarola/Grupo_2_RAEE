package com.ayds2.Proyecto.ayds2.cu03.service;

import org.springframework.stereotype.Service;

import com.ayds2.Proyecto.ayds2.cu03.model.PagoRequest;
import com.ayds2.Proyecto.ayds2.cu03.model.PagoResponse;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class MercadoPagoService implements IPago {

    private static final Logger logger = Logger.getLogger("PagoMercadoPago");

    public MercadoPagoService() {
        // Asegurar que el token de sandbox esté configurado en tu aplicación a través de
        // la clase de configuración MercadoPagoInit (ya la tenés).
        // `MercadoPagoConfig.setAccessToken(...)` ya debe haberse invocado.
    }

    @Override
    public PagoResponse procesarPago(PagoRequest request) throws Exception {
        try {
            // Crear los items de preferencia
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title(request.getDescripcion())
                    .quantity(1)  // suponemos 1 ítem para simplificar, si tenés múltiples ítems, ajustar.
                    .unitPrice(request.getMonto())
                    .currencyId("ARS") // ajustá por moneda si hace falta
                    .build();

            // Crear el pagador
            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(request.getEmail())
                    .build();

            // Construir la preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(Collections.singletonList(item))
                    .payer(payer)
                    .externalReference("compra-" + UUID.randomUUID().toString())
                    .build();

            // Opciones de request para sandbox – puedes agregar cabeceras de idempotencia si lo deseas
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Idempotency-Key", UUID.randomUUID().toString());

            MPRequestOptions options = MPRequestOptions.builder()
                    .customHeaders(headers)
                    // En sandbox basta usar el token de sandbox. El endpoint automáticamente está en modo pruebas.
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference response = client.create(preferenceRequest, options);

            // Obtener init_point / sandbox_init_point desde la respuesta
            String initPoint = response.getInitPoint();
            String sandboxInitPoint = response.getSandboxInitPoint();

            PagoResponse pagoResponse = new PagoResponse();
            pagoResponse.setIdPreferencia(response.getId());
            pagoResponse.setInitPoint(initPoint);
            pagoResponse.setSandboxInitPoint(sandboxInitPoint);

            logger.info("Preferencia creada en Mercado Pago sandbox. ID=" + response.getId());

            return pagoResponse;

        } catch (MPApiException apiEx) {
            System.out.println("=== ERROR DETALLADO DE MERCADO PAGO ===");
            System.out.println("Mensaje: " + apiEx.getMessage());

            if (apiEx.getApiResponse() != null) {
                System.out.println("Código HTTP: " + apiEx.getApiResponse().getStatusCode());
                System.out.println("Contenido JSON:");
                System.out.println(apiEx.getApiResponse().getContent());
            } else {
                System.out.println("⚠️ No se recibió contenido en la respuesta de la API.");
            }

            throw apiEx;
        } catch (MPException ex) {
            logger.severe("Error SDK Mercado Pago: " + ex.getMessage());
            throw ex;
        } catch (Exception e) {
            logger.severe("Error inesperado Mercado Pago: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean validarPago(String preferenceId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(preferenceId));

            if (payment != null && "approved".equalsIgnoreCase(payment.getStatus())) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error validando pago: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Payment obtenerPago(String paymentId) throws Exception {
        PaymentClient client = new PaymentClient();
        return client.get(Long.parseLong(paymentId));
    }

}