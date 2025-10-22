package com.ayds2.Proyecto.ayds2.cu03.service;

import org.springframework.stereotype.Service;

import com.ayds2.Proyecto.ayds2.cu03.model.Compra;
import com.ayds2.Proyecto.ayds2.cu03.model.Pago;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

import java.math.BigDecimal;

@Service
public class MercadoPagoService implements IPagoService {

    @Override
    public Pago procesarPago(Compra compra) {
        try {
            PaymentClient client = new PaymentClient();

            PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(BigDecimal.valueOf(compra.getTotal()))
                .description("Compra de productos RAEE")
                .paymentMethodId(compra.getMetodoPago()) // ej: "visa", "master", "account_money"
                .payer(
                    PaymentPayerRequest.builder()
                        .email("test_user_123456@testuser.com") // usuario de prueba sandbox
                        .build()
                )
                .installments(1)
                .binaryMode(true) // fuerza "approved" o "rejected"
                .build();

            Payment payment = client.create(request);

            boolean aprobado = "approved".equalsIgnoreCase(payment.getStatus());
            String mensaje = aprobado ? "Pago aprobado (sandbox)" : "Pago no aprobado: " + payment.getStatus();

            return new Pago(aprobado, mensaje);

        } catch (MPApiException e) {
            return new Pago(false, "Error API Mercado Pago: " + e.getMessage());
        } catch (MPException e) {
            return new Pago(false, "Error al procesar pago: " + e.getMessage());
        }
    }
}

