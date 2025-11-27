package com.ayds2.Proyecto.ayds2.service;

import com.ayds2.Proyecto.ayds2.dao.CarritoDAO;
import com.ayds2.Proyecto.ayds2.dao.CompraDAO;
import com.ayds2.Proyecto.ayds2.dao.MetodoPagoDAO;
import com.ayds2.Proyecto.ayds2.dao.ProductoRaeeDAO;
import com.ayds2.Proyecto.ayds2.dao.UsuarioDAO;
import com.ayds2.Proyecto.ayds2.factory.PagoFactory;
import com.ayds2.Proyecto.ayds2.iface.IPago;
import com.ayds2.Proyecto.ayds2.model.Carrito;
import com.ayds2.Proyecto.ayds2.model.Compra;
import com.ayds2.Proyecto.ayds2.model.DetalleCarrito;
import com.ayds2.Proyecto.ayds2.model.DetalleCompra;
import com.ayds2.Proyecto.ayds2.model.PagoRequest;
import com.ayds2.Proyecto.ayds2.model.PagoResponse;
import com.ayds2.Proyecto.ayds2.model.ProductoRaee;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

@Service
public class CompraService {
    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);

    @Autowired private CompraDAO compraDAO;
    @Autowired private ProductoRaeeDAO stockDAO;
    @Autowired private CarritoDAO carritoDAO;
    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private MetodoPagoDAO metodoPagoDAO;
    @Autowired private PagoFactory pagoFactory;
    
    private final Gson gson = new Gson();

    public String procesarCompra(String formaEntrega, String metodoPago, String jsonCarrito) {

        logger.info("Iniciando procesamiento de compra. FormaEntrega={}, MetodoPago={}", formaEntrega, metodoPago);

        try {
            // Deserialización del carrito
            // Convierte el JSON recibido desde el frontend (jsonCarrito) en un objeto Carrito de Java.
            Carrito carrito = gson.fromJson(jsonCarrito, Carrito.class);
            int usuarioId = carrito.getUsuario_id();
            int idCarrito = carrito.getId_carritoRAEE();

            logger.debug("Carrito deserializado correctamente para usuario {}", usuarioId);

            // Cálculo del total de la compra
            // Se recorren los productos del carrito para obtener los precios y calcular el monto total.
            double total = 0.0;
            Map<Integer, Double> precios = new HashMap<>();

            // Se guarda el precio de cada producto en un mapa (idProducto → precio)
            for (ProductoRaee p : carrito.getProductos())
                precios.put(p.getId_productoRAEE(), p.getPrecio());
            // Se recorre el detalle del carrito para multiplicar precio * cantidad
            for (DetalleCarrito d : carrito.getDetalles())
                total += precios.getOrDefault(d.getProductoRAEE_id(), 0.0) * d.getCantidad();
            
            logger.info("Total calculado para carrito {}: ${}", idCarrito, total);

            // Obtención del email del usuario
            // Consulta a la base de datos para recuperar el correo del usuario asociado al carrito.
            String emailUsuario = usuarioDAO.obtenerEmailPorId(usuarioId);
            logger.debug("Email obtenido para usuario {}: {}", usuarioId, emailUsuario);

            // Creación del objeto PagoRequest
            // Contiene los datos necesarios para generar la preferencia de pago (monto, descripción, método, email).
            PagoRequest request = new PagoRequest(
                BigDecimal.valueOf(total),
                "Compra carrito id " + idCarrito,
                metodoPago,
                emailUsuario
            );

            // Procesamiento del pago
            // Obtiene la implementación concreta de pago (por ejemplo, Mercado Pago) mediante la fábrica de pagos.
            IPago pago = pagoFactory.getPago(metodoPago);
            // Envía la solicitud de pago al servicio correspondiente (crea la preferencia de pago).
            // Obtención de la respuesta del pago
            PagoResponse pagoResponse = pago.procesarPago(request);
            String idPreferencia = pagoResponse.getIdPreferencia();
            logger.info("Preferencia de pago generada: {}", idPreferencia);

            // Validación del pago
            // Verifica si el pago fue aprobado. En entorno de prueba, simula aprobación.
            boolean aprobado = pago.validarPago(idPreferencia);
            if (!aprobado) {
                // Si el pago no fue aprobado, se devuelve una respuesta con error.
                logger.warn("Pago no aprobado para carrito {}", idCarrito);
                return "{\"error\":\"Pago no aprobado\"}";
            }

            // Actualización del stock
            // Recorre todos los productos del carrito y descuenta la cantidad comprada del inventario.
            for (DetalleCarrito d : carrito.getDetalles())
                stockDAO.actualizaStock(d.getProductoRAEE_id(), d.getCantidad());

            logger.info("Stock actualizado correctamente para carrito {}", idCarrito);

            // Obtención del ID del método de pago desde la base de datos
            int idMetodo = metodoPagoDAO.obtenerIdMetodoPagoPorNombre(metodoPago);

            // Creación del objeto Compra
            // Se instancia un objeto Compra con toda la información necesaria para registrar la transacción.
            Compra compra = new Compra();
            compra.setId_Usuario(usuarioId);
            compra.setFecha(new Date(System.currentTimeMillis())); // Fecha actual
            compra.setTotal(total);
            compra.setFormaEntrega(formaEntrega);
            compra.setId_metodoPago(idMetodo);
            compra.setIdPagoMP(idPreferencia); // ID generado por el servicio de pago

            // Generación de los detalles de la compra
            // Convierte los detalles del carrito (DetalleCarrito) en DetalleCompra (para guardar en la BD).
            List<DetalleCompra> detallesCompra = new ArrayList<>();
            for (DetalleCarrito d : carrito.getDetalles()) {
                DetalleCompra dc = new DetalleCompra();
                dc.setId_ProductoRAEE(d.getProductoRAEE_id());
                dc.setCantidad(d.getCantidad());
                dc.setPrecioUnitario(precios.get(d.getProductoRAEE_id()));
                detallesCompra.add(dc);
            }
            compra.setDetalles(detallesCompra);

            // Registro de la compra en la base de datos
            // Guarda la compra y sus detalles asociados mediante el DAO correspondiente.
            compraDAO.registrarCompra(compra);

            // Limpieza del carrito
            // Borra los detalles del carrito ya procesado, dejando el carrito vacío.
            carritoDAO.deleteDetallesByCarritoId(idCarrito);

            // Log final de éxito
            // Registra en logs que la compra se completó correctamente y devuelve la respuesta JSON de éxito.
            logger.info("Compra registrada exitosamente. ID usuario={}, Total={}", usuarioId, total);
            return "{\"Compra\":\"Exitosa\"}";

        } catch (Exception e) {
            // Manejo de errores
            // Si ocurre cualquier excepción en el proceso, se registra el error y se devuelve un mensaje genérico.
            logger.error("Error procesando compra", e);
            return "{\"error\":\"Error procesando compra\"}";
        }
    }
}