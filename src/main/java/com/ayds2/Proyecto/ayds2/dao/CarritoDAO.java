package com.ayds2.Proyecto.ayds2.dao;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;

import com.ayds2.Proyecto.ayds2.model.Carrito;
import com.ayds2.Proyecto.ayds2.model.DetalleCarrito;
import com.ayds2.Proyecto.ayds2.model.ProductoRaee;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;
import com.google.gson.Gson;
import com.ayds2.Proyecto.ayds2.iface.iCarritoDAO;

@Repository
public class CarritoDAO implements iCarritoDAO {

    private static final Logger logger = LoggerFactory.getLogger(CarritoDAO.class);
   
    // CU-01: Agregar a carrito
    @Override
    public String insert(int id_carrito, int id_ProductoRAEE, int cantidad) {

        // 1 Validar que la cantidad sea positiva
        if (cantidad <= 0) {
            logger.warn("La cantidad debe ser un valor positivo.");
            return "La cantidad debe ser un valor positivo";
        }
        
        logger.info("Verificando stock del producto antes de insertarlo en el carrito.");

        // 2 Verificar que el producto exista y que haya stock suficiente
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String sql = "SELECT stock FROM productoraee WHERE id_ProductoRAEE = :id_ProductoRAEE";
            Integer stock = con.createQuery(sql)
                .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                .executeScalar(Integer.class);

            if (stock == null) {
                logger.warn("El producto no existe en la base de datos.");
                return "El producto no existe en la base de datos";
            }

            if (cantidad > stock) {
                logger.warn("La cantidad solicitada excede el stock disponible.");
                return "La cantidad solicitada excede el stock disponible";
            }

        } catch (Exception e) {
            logger.error("Error al verificar el stock del producto: {}", e.getMessage());
            return "Error al verificar el stock del producto";
        }

        logger.info("Stock suficiente, verificando si el producto ya está en el carrito.");

        // 3 Verificar si el producto ya está en el carrito
        try (Connection con1 = Sql2oDAO.getSql2o().open()) {
            String existeSql = """
                SELECT COUNT(*) 
                FROM detallecarrito 
                WHERE productoRAEE_id = :id_ProductoRAEE 
                AND carritoRAEE_id = :id_carrito
            """;

            int count = con1.createQuery(existeSql)
                .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                .addParameter("id_carrito", id_carrito)
                .executeScalar(Integer.class);

            boolean productoYaEnCarrito = count > 0;

            // 4 Si el producto ya existe → actualizar la cantidad
            if (productoYaEnCarrito) {
                logger.info("El producto ya existe en el carrito, se actualizará la cantidad.");

                String updateDetalle = """
                    UPDATE detallecarrito 
                    SET cantidad = cantidad + :cantidad 
                    WHERE productoRAEE_id = :id_ProductoRAEE 
                    AND carritoRAEE_id = :id_carrito
                """;
                con1.createQuery(updateDetalle)
                    .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                    .addParameter("id_carrito", id_carrito)
                    .addParameter("cantidad", cantidad)
                    .executeUpdate();

                return "Cantidad del producto actualizada exitosamente";

            } else {
                // 5 Si no existe → insertar nuevo producto en el carrito
                String insertDetalle = """
                    INSERT INTO detallecarrito (productoRAEE_id, carritoRAEE_id, cantidad)
                    VALUES (:id_ProductoRAEE, :id_carrito, :cantidad)
                """;
                con1.createQuery(insertDetalle)
                    .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                    .addParameter("id_carrito", id_carrito)
                    .addParameter("cantidad", cantidad)
                    .executeUpdate();

                return "Producto agregado al carrito exitosamente";
            }

        } catch (Exception e) {
            logger.error("Error al insertar o actualizar producto en carrito: {}", e.getMessage());
            return "Error al insertar o actualizar producto en carrito";
        }
    }

    // CU-04: Ver Carrito
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
            List<ProductoRaee> productos = con.createQuery(sqlProductos)
                    .addParameter("id_carritoRAEE", id_carrito)
                    .executeAndFetch(ProductoRaee.class);

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

    // CU-03: Comprar carrito RAEE
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