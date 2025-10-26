package com.ayds2.Proyecto.ayds2.cu03.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.sql.Date;
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

    public String procesarCompra(String formaEntrega, String metodoPago, String jsonCarrito) {
        try {
            // parsear carrito
            Carrito carrito = gson.fromJson(jsonCarrito, Carrito.class);

            // calcular total
            double total = 0.0;
            if (carrito.getDetalles() != null && carrito.getProductos() != null) {
                // mapear idProducto -> precio
                java.util.Map<Integer, Double> precioMap = new java.util.HashMap<>();
                for (ProductoRAEE p : carrito.getProductos()) {
                    precioMap.put(p.getId_ProductoRAEE(), (double) p.getPrecio());
                }
                for (DetalleCarrito dc : carrito.getDetalles()) {
                    Double p = precioMap.get(dc.getProductoRAEE_id());
                    if (p == null) p = 0.0;
                    total += p * dc.getCantidad();
                }
            }

            // 1. validar pago
            PagoRequest pagoRequest = new PagoRequest(
                    BigDecimal.valueOf(total),
                    "Compra carrito id " + carrito.getId_carritoRAEE(),
                    metodoPago,
                    "usuario@" + carrito.getUsuario_id()
                    ); // si tenés email real sacalo de usuario

            IPago pago = pagoFactory.getPago(metodoPago);
            PagoResponse pagoResponse = pago.procesarPago(pagoRequest);

            if (pagoResponse == null) {
                logger.severe("Pago rechazado o error en procesamiento");
                return "{\"error\":\"Pago rechazado\"}";
            }

            // 2. actualizar stock
            if (carrito.getDetalles() != null) {
                actualizarStockService.actualizarStock(carrito.getDetalles());
            }

            // 3. registrar compra
            Compra compra = new Compra();
            compra.setIdUsuario(carrito.getUsuario_id());
            compra.setFechaCompra(new Date(System.currentTimeMillis()));
            compra.setTotal(total);
            compra.setFormaEntrega(formaEntrega);
            compra.setMetodoPago(metodoPago);

            // convertir detalles del carrito a detalles de compra con precio unitario
            List<DetalleCompra> detallesCompra = new ArrayList<>();
            java.util.Map<Integer, Double> precioMap = new java.util.HashMap<>();
            if (carrito.getProductos() != null) {
                for (ProductoRAEE p : carrito.getProductos()) {
                    precioMap.put(p.getId_ProductoRAEE(), (double) p.getPrecio());
                }
            }
            if (carrito.getDetalles() != null) {
                for (DetalleCarrito dc : carrito.getDetalles()) {
                    DetalleCompra d = new DetalleCompra();
                    d.setIdProductoRaee(dc.getProductoRAEE_id());
                    d.setCantidad(dc.getCantidad());
                    Double precioUnit = precioMap.getOrDefault(dc.getProductoRAEE_id(), 0.0);
                    d.setPrecioUnitario(precioUnit);
                    detallesCompra.add(d);
                }
            }
            compra.setDetalles(detallesCompra);

            compraDAO.registrarCompra(compra);

            // 4. eliminar carrito y detalles del carrito
            carritoDAOCU04.deleteDetallesByCarritoId(carrito.getId_carritoRAEE());
            carritoDAOCU04.deleteCarritoById(carrito.getId_carritoRAEE());

            // 5. responder
            return "{\"Compra\":\"Exitosa\"}";

        } catch (Exception e) {
            logger.severe("Error en procesarCompra: " + e.getMessage());
            e.printStackTrace();
            return "{\"error\":\"Error procesando compra\"}";
        }
    }    
}