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
    //Inyeccion de dependencias
    @Autowired private CompraDAO compraDAO;
    @Autowired private ActualizarStockDAO stockDAO;
    @Autowired private CarritoDAOCU04 carritoDAO;
    @Autowired private PagoFactory pagoFactory;
    @Autowired private UsuarioDAO usuarioDAO;
    
    private final Gson gson = new Gson();

    //Método que gestiona el flujo completo de la compra de un carrito
    public String procesarCompra(String formaEntrega, String metodoPago, String jsonCarrito) {
        try {
            //Deserializo el json a un objeto de tipo Carrito
            Carrito carrito = gson.fromJson(jsonCarrito, Carrito.class);

            // Calculo el total del carrito
            double total = 0.0;
            Map<Integer, Double> precios = new HashMap<>();
            for (ProductoRAEE p : carrito.getProductos()) precios.put(p.getId_ProductoRAEE(), p.getPrecio());
            for (DetalleCarrito d : carrito.getDetalles())
                total += precios.getOrDefault(d.getProductoRAEE_id(), 0.0) * d.getCantidad();

            // Obtengo el email del usuario para asociarlo al pago
            String emailUsuario = usuarioDAO.obtenerEmailPorId(carrito.getUsuario_id());

            //Creo una solicitud de pago con la información necesaria
            PagoRequest request = new PagoRequest(
                BigDecimal.valueOf(total),
                "Compra carrito id " + carrito.getId_carritoRAEE(),
                metodoPago,
                emailUsuario
            );

            // Proceso el pago mediante el uso del factory
            IPago pago = pagoFactory.getPago(metodoPago);
            PagoResponse pagoResponse = pago.procesarPago(request);

            // Valido el pafo (dado que es modo sandbox, el pago siempre sera aprobado)
            boolean aprobado = pago.validarPago(pagoResponse.getIdPreferencia());
            if (!aprobado) return "{\"error\":\"Pago no aprobado\"}";

            // Actualizo el stock de los productos comprados
            for (DetalleCarrito d : carrito.getDetalles())
                stockDAO.actualizaStock(d.getProductoRAEE_id(), d.getCantidad());

            // Creo el objeto compra 
            Compra compra = new Compra();
            compra.setIdUsuario(carrito.getUsuario_id());
            compra.setFechaCompra(new Date(System.currentTimeMillis()));
            compra.setTotal(total);
            compra.setFormaEntrega(formaEntrega);
            compra.setMetodoPago(metodoPago);
            compra.setIdPagoMP(pagoResponse.getIdPreferencia());
            // junto a los detalles de compra
            List<DetalleCompra> detallesCompra = new ArrayList<>();
            for (DetalleCarrito d : carrito.getDetalles()) {
                DetalleCompra dc = new DetalleCompra();
                dc.setIdProductoRaee(d.getProductoRAEE_id());
                dc.setCantidad(d.getCantidad());
                dc.setPrecioUnitario(precios.get(d.getProductoRAEE_id()));
                detallesCompra.add(dc);
            }
            compra.setDetalles(detallesCompra);

            // Registro la compra en la base de datos
            compraDAO.registrarCompra(compra);

            // Limpio el carrito del usuario
            carritoDAO.deleteDetallesByCarritoId(carrito.getId_carritoRAEE());
            
            // Retorno respuesta de éxito
            return "{\"Compra\":\"Exitosa\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Error procesando compra\"}";
        }
    }
}