package com.ayds2.Proyecto.ayds2.cu03.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds2.Proyecto.ayds2.cu03.dao.CompraDAO;
import com.ayds2.Proyecto.ayds2.cu03.factory.PagoFactory;
import com.ayds2.Proyecto.ayds2.cu03.model.Compra;
import com.ayds2.Proyecto.ayds2.cu03.model.DetalleCompra;
import com.ayds2.Proyecto.ayds2.cu03.model.PagoRequest;
import com.ayds2.Proyecto.ayds2.cu03.model.PagoResponse;
import com.ayds2.Proyecto.ayds2.cu04.dao.CarritoDAOCU04;
import com.ayds2.Proyecto.ayds2.cu04.model.Carrito;
import com.ayds2.Proyecto.ayds2.cu04.model.DetalleCarrito;
import com.ayds2.Proyecto.ayds2.cu04.model.ProductoRAEE;
import com.google.gson.Gson;
import com.mercadopago.resources.payment.Payment;

@Service
public class CompraService {

    private final Logger logger = Logger.getLogger("CompraService");
    private final Gson gson = new Gson();

    @Autowired
    private PagoFactory pagoFactory;

    @Autowired
    private ActualizarStockService actualizarStockService;

    @Autowired
    private CompraDAO compraDAO;

    @Autowired
    private CarritoDAOCU04 carritoDAOCU04;

    /**
     * Paso 1: crear preferencia de pago y devolver URL de checkout
     */
    public String procesarCompra(String formaEntrega, String metodoPago, String jsonCarrito) {
        try {
            // Parsear carrito
            Carrito carrito = gson.fromJson(jsonCarrito, Carrito.class);

            // Calcular total
            double total = 0.0;
            if (carrito.getDetalles() != null && carrito.getProductos() != null) {
                java.util.Map<Integer, Double> precioMap = new java.util.HashMap<>();
                for (ProductoRAEE p : carrito.getProductos()) {
                    precioMap.put(p.getId_ProductoRAEE(), (double) p.getPrecio());
                }
                for (DetalleCarrito dc : carrito.getDetalles()) {
                    Double precio = precioMap.get(dc.getProductoRAEE_id());
                    if (precio == null) precio = 0.0;
                    total += precio * dc.getCantidad();
                }
            }

            // Crear solicitud de pago
            PagoRequest pagoRequest = new PagoRequest(
                BigDecimal.valueOf(total),
                "Compra carrito id " + carrito.getId_carritoRAEE(),
                metodoPago,
                "usuario@" + carrito.getUsuario_id()
            );

            IPago pago = pagoFactory.getPago(metodoPago);

            // 1. Crear preferencia en Mercado Pago
            PagoResponse pagoResponse = pago.procesarPago(pagoRequest);

            // 2. Guardar la preferencia temporalmente (opcional, para tracking)
            logger.info("Preferencia creada: " + pagoResponse.getIdPreferencia());

            // 3. Retornar la URL de pago al frontend (sandboxInitPoint)
            return "{\"mensaje\":\"Preferencia creada. Complete el pago en la URL indicada.\"," +
                   "\"sandbox_url\":\"" + pagoResponse.getSandboxInitPoint() + "\"," +
                   "\"idPreferencia\":\"" + pagoResponse.getIdPreferencia() + "\"}";

        } catch (Exception e) {
            logger.severe("Error en procesarCompra: " + e.getMessage());
            e.printStackTrace();
            return "{\"error\":\"Error procesando compra\"}";
        }
    }

    /**
     * Paso 2: validar el pago real (desde webhook) y registrar la compra en BD
     */
    public boolean validarPagoYRegistrarCompra(String paymentId) {
        try {
            MercadoPagoService mpService = new MercadoPagoService();
            Payment payment = mpService.obtenerPago(paymentId);

            if (payment != null && "approved".equalsIgnoreCase(payment.getStatus())) {
                logger.info("Pago aprobado por Mercado Pago. Registrando compra...");

                // Crear y registrar la compra
                Compra compra = new Compra();
                compra.setFechaCompra(new Date(System.currentTimeMillis()));
                compra.setTotal(payment.getTransactionAmount().doubleValue());
                compra.setFormaEntrega("domicilio"); // Podés ajustarlo si querés hacerlo dinámico
                compra.setMetodoPago("MercadoPago");
                compra.setIdPagoMP(paymentId);

                compraDAO.registrarCompra(compra);
                return true;
            } else {
                logger.warning("Pago no aprobado. Estado: " + (payment != null ? payment.getStatus() : "null"));
            }

        } catch (Exception e) {
            logger.severe("Error validando y registrando compra: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}