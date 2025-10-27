package com.ayds2.Proyecto.ayds2.cu03.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayds2.Proyecto.ayds2.cu03.service.CompraService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Logger;

@RestController
@RequestMapping("/compra")
public class CompraController {

    private final Logger logger = Logger.getLogger("CompraController");

    @Autowired
    private CompraService compraService;

    // === Endpoint principal: crear preferencia ===
    @PostMapping("/comprar")
    public String comprar(@RequestParam String formaEntrega,
                          @RequestParam String metodoPago,
                          @RequestBody String jsonCarrito) {
        return compraService.procesarCompra(formaEntrega, metodoPago, jsonCarrito);
    }

    // === Nuevo endpoint: notificación IPN de Mercado Pago ===
    @PostMapping("/notificacionMP")
    public ResponseEntity<String> recibirNotificacion(@RequestBody String body) {
        try {
            logger.info("Notificación recibida de Mercado Pago: " + body);
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();

            // Verificar si contiene un payment_id dentro del campo "data"
            if (json.has("data")) {
                String paymentId = json.getAsJsonObject("data").get("id").getAsString();
                logger.info("Payment ID recibido: " + paymentId);

                boolean aprobado = compraService.validarPagoYRegistrarCompra(paymentId);
                if (aprobado) {
                    logger.info("Compra registrada exitosamente para paymentId=" + paymentId);
                    return ResponseEntity.ok("{\"mensaje\":\"Compra exitosa\"}");
                } else {
                    logger.warning("Pago rechazado o pendiente para paymentId=" + paymentId);
                    return ResponseEntity.ok("{\"mensaje\":\"Pago rechazado\"}");
                }
            }

            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Formato de notificación inválido\"}");

        } catch (Exception e) {
            logger.severe("Error procesando notificación de Mercado Pago: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"Error procesando notificación\"}");
        }
    }
}
