package com.ayds2.Proyecto.ayds2.cu04.dao;

import java.util.List;
import java.util.logging.Logger;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.cu04.model.Carrito;
import com.ayds2.Proyecto.ayds2.cu04.model.DetalleCarrito;
import com.ayds2.Proyecto.ayds2.cu04.model.ProductoRAEE;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;
import com.google.gson.Gson;

public class CarritoDAOCU04 implements iCarritoDAOCU04 {

    @Override
    public String select(int id_carrito) {
        
        Logger logger = Logger.getLogger("CarritoDAO");
        logger.info("Ejecutando consulta para obtener el carrito y sus productos.\n");

        try (Connection con = Sql2oDAO.getSql2o().open()) {
            //primero recuperamos el carrito
            String sqlCarrito = "SELECT id_carritoRAEE, usuario_id FROM carritoraee " + 
                                "WHERE id_carritoRAEE = :id_carrito";
            Carrito carrito = con.createQuery(sqlCarrito)
                    .addParameter("id_carrito", id_carrito)
                    .executeAndFetchFirst(Carrito.class);

            //luego recuperamos los productos que están en el carrito
            String sqlProductos = "SELECT pr.id_ProductoRAEE, pr.nombre, pr.categoria_id, pr.descripcion, pr.precio, pr.stock " +
                                "FROM productoraee pr " +
                                "JOIN productoraee_has_carritoraee pc ON pr.id_ProductoRAEE = pc.productoRAEE_id " +
                                "WHERE pc.carritoRAEE_id = :id_carrito";
            List<ProductoRAEE> productos = con.createQuery(sqlProductos)
                    .addParameter("id_carrito", id_carrito)
                    .executeAndFetch(ProductoRAEE.class);

            //y  por ultimo recuperamos cuanto cantidad hay de cada producto en el carrito
            String sqlDetalles = "SELECT productoRAEE_id, carritoRAEE_id, cantidad " +
                                "FROM productoraee_has_carritoraee " +
                                "WHERE carritoRAEE_id = :id_carrito";
            List<DetalleCarrito> detalles = con.createQuery(sqlDetalles)
                    .addParameter("id_carrito", id_carrito)
                    .executeAndFetch(DetalleCarrito.class);

            carrito.setProductos(productos);
            carrito.setDetalles(detalles);

            Gson gson = new Gson();
            return gson.toJson(carrito);

        } catch (Exception e) {
            logger.severe("Error al ejecutar consulta del carrito: " + e.getMessage());
            e.printStackTrace();
            return "{\"error\": \"Error al obtener el carrito y sus productos\"}";
        }
    }
}
