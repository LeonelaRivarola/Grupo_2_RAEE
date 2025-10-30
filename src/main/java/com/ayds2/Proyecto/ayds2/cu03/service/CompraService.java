package com.ayds2.Proyecto.ayds2.cu03.service;

import com.ayds2.Proyecto.ayds2.cu03.dao.ActualizarStockDAO;
import com.ayds2.Proyecto.ayds2.cu03.dao.CompraDAO;
import com.ayds2.Proyecto.ayds2.cu03.dao.UsuarioDAO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@Service
public class CompraService {

    @Autowired private CompraDAO compraDAO;
    @Autowired private ActualizarStockDAO stockDAO;
    @Autowired private CarritoDAOCU04 carritoDAO;
    @Autowired private PagoFactory pagoFactory;
    @Autowired private UsuarioDAO usuarioDAO;
    
    private final Gson gson = new Gson();

    public String procesarCompra(String formaEntrega, String metodoPago, String jsonCarrito) {
        try {
            // Deserializo el json del carrito
            Carrito carrito = gson.fromJson(jsonCarrito, Carrito.class);

            // Calculo el total del carrito
            double total = 0.0;
            Map<Integer, Double> precios = new HashMap<>();
            for (ProductoRAEE p : carrito.getProductos())
                precios.put(p.getId_ProductoRAEE(), p.getPrecio());
            for (DetalleCarrito d : carrito.getDetalles())
                total += precios.getOrDefault(d.getProductoRAEE_id(), 0.0) * d.getCantidad();

            // Obtengo el email del usuario
            String emailUsuario = usuarioDAO.obtenerEmailPorId(carrito.getUsuario_id());

            // Creo la solicitud de pago
            PagoRequest request = new PagoRequest(
                BigDecimal.valueOf(total),
                "Compra carrito id " + carrito.getId_carritoRAEE(),
                metodoPago,
                emailUsuario
            );

            // Proceso el pago
            IPago pago = pagoFactory.getPago(metodoPago);
            PagoResponse pagoResponse = pago.procesarPago(request);

            // Verifico si el pago fue aprobado
            boolean aprobado = pago.validarPago(pagoResponse.getIdPreferencia());
            if (!aprobado) return "{\"error\":\"Pago no aprobado\"}";

            // Actualizo el stock
            for (DetalleCarrito d : carrito.getDetalles())
                stockDAO.actualizaStock(d.getProductoRAEE_id(), d.getCantidad());

            // Obtengo el id del método de pago desde la BD
            int idMetodo = compraDAO.obtenerIdMetodoPagoPorNombre(metodoPago);

            // Creo la compra con los nuevos nombres de atributos
            Compra compra = new Compra();
            compra.setId_Usuario(carrito.getUsuario_id());
            compra.setFecha(new Date(System.currentTimeMillis()));
            compra.setTotal(total);
            compra.setFormaEntrega(formaEntrega);
            compra.setId_metodoPago(idMetodo);
            compra.setIdPagoMP(pagoResponse.getIdPreferencia());

            // Genero los detalles de compra
            List<DetalleCompra> detallesCompra = new ArrayList<>();
            for (DetalleCarrito d : carrito.getDetalles()) {
                DetalleCompra dc = new DetalleCompra();
                dc.setId_ProductoRAEE(d.getProductoRAEE_id());
                dc.setCantidad(d.getCantidad());
                dc.setPrecioUnitario(precios.get(d.getProductoRAEE_id()));
                detallesCompra.add(dc);
            }
            compra.setDetalles(detallesCompra);

            // Registro la compra
            compraDAO.registrarCompra(compra);

            // Limpio los detalles del carrito
            carritoDAO.deleteDetallesByCarritoId(carrito.getId_carritoRAEE());

            // Respuesta exitosa
            return "{\"Compra\":\"Exitosa\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Error procesando compra\"}";
        }
    }
}