package com.ayds2.Proyecto.ayds2.cu04.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.cu04.model.Carrito;
import com.ayds2.Proyecto.ayds2.cu04.model.DetalleCarrito;
import com.ayds2.Proyecto.ayds2.cu04.model.ProductoRAEE;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;
import com.google.gson.Gson;

@Repository
public class CarritoDAOCU04 implements iCarritoDAOCU04 {

    private static final Logger logger = LoggerFactory.getLogger(CarritoDAOCU04.class);

    @Override
    public String select(int id_carrito) {
        logger.info("Ejecutando consulta para obtener el carrito y sus productos (id_carrito={}).", id_carrito);

        try (Connection con = Sql2oDAO.getSql2o().open()) {

            // Recupera los datos generales del carrito
            String sqlCarrito = "SELECT id_carritoRAEE, usuario_id FROM carritoraee " +
                                "WHERE id_carritoRAEE = :id_carritoRAEE";
            Carrito carrito = con.createQuery(sqlCarrito)
                    .addParameter("id_carritoRAEE", id_carrito)
                    .executeAndFetchFirst(Carrito.class);

            // Recupera los productos asociados al carrito
            String sqlProductos = "SELECT pr.id_ProductoRAEE, pr.nombre, pr.categoria_id, pr.descripcion, pr.precio, pr.stock " +
                                "FROM productoraee pr " +
                                "JOIN detallecarrito pc ON pr.id_ProductoRAEE = pc.productoRAEE_id " +
                                "WHERE pc.carritoRAEE_id = :id_carritoRAEE";
            List<ProductoRAEE> productos = con.createQuery(sqlProductos)
                    .addParameter("id_carritoRAEE", id_carrito)
                    .executeAndFetch(ProductoRAEE.class);

            // Recupera las cantidades de cada producto en el carrito
            String sqlDetalles = "SELECT productoRAEE_id, carritoRAEE_id, cantidad " +
                                "FROM detallecarrito " +
                                "WHERE carritoRAEE_id = :id_carritoRAEE";
            List<DetalleCarrito> detalles = con.createQuery(sqlDetalles)
                    .addParameter("id_carritoRAEE", id_carrito)
                    .executeAndFetch(DetalleCarrito.class);

            // Asigna productos y detalles al objeto Carrito
            carrito.setProductos(productos);
            carrito.setDetalles(detalles);

            logger.debug("Carrito {} obtenido correctamente con {} productos.", id_carrito, productos.size());

            // Convierte el carrito a JSON y lo devuelve
            Gson gson = new Gson();
            return gson.toJson(carrito);

        } catch (Exception e) {
            logger.error("Error al ejecutar consulta del carrito con id {}: {}", id_carrito, e.getMessage(), e);
            return "{\"error\": \"Error al obtener el carrito y sus productos\"}";
        }
    }

    // ---- Métodos nuevos ----
    @Override
    public void deleteDetallesByCarritoId(int id_carrito) {
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String sql = "DELETE FROM detallecarrito WHERE carritoRAEE_id = :id_carritoRAEE";
            con.createQuery(sql)
               .addParameter("id_carritoRAEE", id_carrito)
               .executeUpdate();

            logger.info("Detalles del carrito {} eliminados correctamente.", id_carrito);

        } catch (Exception e) {
            logger.error("Error al eliminar detalles del carrito {}: {}", id_carrito, e.getMessage(), e);
            throw e;
        }
    } 
}