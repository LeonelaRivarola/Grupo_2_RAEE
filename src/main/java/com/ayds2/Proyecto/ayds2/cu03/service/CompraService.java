package com.ayds2.Proyecto.ayds2.cu03.service;

import com.ayds2.Proyecto.ayds2.cu02.dao.ProductoRaeeDAO;
import com.ayds2.Proyecto.ayds2.cu03.dao.CompraDAO;
import com.ayds2.Proyecto.ayds2.cu03.dao.MetodoPagoDAO;
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
    @Autowired private CarritoDAOCU04 carritoDAO;
    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private MetodoPagoDAO metodoPagoDAO;
    @Autowired private PagoFactory pagoFactory;
    
    private final Gson gson = new Gson();

    public String procesarCompra(String formaEntrega, String metodoPago, String jsonCarrito) {

        logger.info("Iniciando procesamiento de compra. FormaEntrega={}, MetodoPago={}", formaEntrega, metodoPago);

        try {
            // 1. Deserialización del carrito
            // Convierte el JSON recibido desde el frontend (jsonCarrito) en un objeto Carrito de Java.
            Carrito carrito = gson.fromJson(jsonCarrito, Carrito.class);
            int usuarioId = carrito.getUsuario_id();
            int idCarrito = carrito.getId_carritoRAEE();

            logger.debug("Carrito deserializado correctamente para usuario {}", usuarioId);

            // 2. Cálculo del total de la compra
            // Se recorren los productos del carrito para obtener los precios y calcular el monto total.
            double total = 0.0;
            Map<Integer, Double> precios = new HashMap<>();

            // Se guarda el precio de cada producto en un mapa (idProducto → precio)
            for (ProductoRAEE p : carrito.getProductos())
                precios.put(p.getId_ProductoRAEE(), p.getPrecio());

            // Se recorre el detalle del carrito para multiplicar precio * cantidad
            for (DetalleCarrito d : carrito.getDetalles())
                total += precios.getOrDefault(d.getProductoRAEE_id(), 0.0) * d.getCantidad();
                
            logger.info("Total calculado para carrito {}: ${}", idCarrito, total);

            // 1.1.1. Obtención del email del usuario
            // Consulta a la base de datos para recuperar el correo del usuario asociado al carrito.
            String emailUsuario = usuarioDAO.obtenerEmailPorId(usuarioId);
            logger.debug("Email obtenido para usuario {}: {}", usuarioId, emailUsuario);

            // 4. Creación del objeto PagoRequest
            // Contiene los datos necesarios para generar la preferencia de pago (monto, descripción, método, email).
            PagoRequest request = new PagoRequest(
                BigDecimal.valueOf(total),
                "Compra carrito id " + idCarrito,
                metodoPago,
                emailUsuario
            );

            // 1.1.2. Procesamiento del pago
            // Obtiene la implementación concreta de pago (por ejemplo, Mercado Pago) mediante la fábrica de pagos.
            IPago pago = pagoFactory.getPago(metodoPago);
            // Envía la solicitud de pago al servicio correspondiente (crea la preferencia de pago).
            // 1.1.3. Obtención de la respuesta del pago
            PagoResponse pagoResponse = pago.procesarPago(request);
            String idPreferencia = pagoResponse.getIdPreferencia();
            logger.info("Preferencia de pago generada: {}", idPreferencia);

            // 1.1.4. Validación del pago
            // Verifica si el pago fue aprobado. En entorno de prueba, simula aprobación.
            boolean aprobado = pago.validarPago(idPreferencia);
            if (!aprobado) {
                // Si el pago no fue aprobado, se devuelve una respuesta con error.
                logger.warn("Pago no aprobado para carrito {}", idCarrito);
                return "{\"error\":\"Pago no aprobado\"}";
            }

            // 1.1.5. Actualización del stock
            // Recorre todos los productos del carrito y descuenta la cantidad comprada del inventario.
            for (DetalleCarrito d : carrito.getDetalles())
                stockDAO.actualizaStock(d.getProductoRAEE_id(), d.getCantidad());

            logger.info("Stock actualizado correctamente para carrito {}", idCarrito);

            // 1.1.6. Obtención del ID del método de pago desde la base de datos
            int idMetodo = metodoPagoDAO.obtenerIdMetodoPagoPorNombre(metodoPago);

            // 9. Creación del objeto Compra
            // Se instancia un objeto Compra con toda la información necesaria para registrar la transacción.
            Compra compra = new Compra();
            compra.setId_Usuario(usuarioId);
            compra.setFecha(new Date(System.currentTimeMillis())); // Fecha actual
            compra.setTotal(total);
            compra.setFormaEntrega(formaEntrega);
            compra.setId_metodoPago(idMetodo);
            compra.setIdPagoMP(idPreferencia); // ID generado por el servicio de pago

            // 10. Generación de los detalles de la compra
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

            // 1.1.7. Registro de la compra en la base de datos
            // Guarda la compra y sus detalles asociados mediante el DAO correspondiente.
            compraDAO.registrarCompra(compra);

            // 1.1.8. Limpieza del carrito
            // Borra los detalles del carrito ya procesado, dejando el carrito vacío.
            carritoDAO.deleteDetallesByCarritoId(idCarrito);

            // 13. Log final de éxito
            // Registra en logs que la compra se completó correctamente y devuelve la respuesta JSON de éxito.
            logger.info("Compra registrada exitosamente. ID usuario={}, Total={}", usuarioId, total);
            return "{\"Compra\":\"Exitosa\"}";

        } catch (Exception e) {
            // 14. Manejo de errores
            // Si ocurre cualquier excepción en el proceso, se registra el error y se devuelve un mensaje genérico.
            logger.error("Error procesando compra", e);
            return "{\"error\":\"Error procesando compra\"}";
        }
    }
}