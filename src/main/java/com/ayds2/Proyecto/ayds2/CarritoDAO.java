package com.ayds2.Proyecto.ayds2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CarritoDAO implements iCarritoDAO{
    
    @Override
    public String select(int id_carrito) {
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respuesta = new HashMap<>();

        Logger.getLogger("CarritoDAO").info("Ejecutando consulta para obtener el carrito.\n");
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            List<Map<String, Object>> carrito = con.createQuery(
                "SELECT id_carritoRAEE, usuario_id FROM carritoraee WHERE id_carritoRAEE = :id_carrito")
                .addParameter("id_carrito", id_carrito)
                .executeAndFetchTable()
                .asList();

            respuesta.put("carrito", carrito);
        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error al ejecutar consulta carrito\n");
            respuesta.put("error_carrito", e.getMessage());
        }
        Logger.getLogger("CarritoDAO").info("Ejecutando consulta para obtener los productos del carrito.\n");
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            List<Map<String, Object>> productos = con.createQuery(
                "SELECT pr.id_ProductoRAEE, pr.nombre, pr.descripcion, pr.precio, pr.stock, pc.cantidad FROM productoraee pr JOIN productoraee_has_carritoraee pc ON pr.id_ProductoRAEE = pc.productoRAEE_id WHERE pc.carritoRAEE_id = :id_carrito")
                .addParameter("id_carrito", id_carrito)
                .executeAndFetchTable()
                .asList();

            respuesta.put("productos", productos);
        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error al ejecutar consulta productos\n");
            respuesta.put("error_productos", e.getMessage());
        }

        try {
            return mapper.writeValueAsString(respuesta);
        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error serializando JSON\n");
            return "{\"error\": \"Error serializando JSON\"}";
        }
    }


    @Override
    public String insert(int id_carrito, int id_ProductoRAEE, int cantidad) {
        //funciona bien cuando se añade por primera vez un producto al carrito. Ahora si quisieramos por ejemplo añadir una unidad mas de un producto que ya esta en el carrito, deberia actualizarse la cantidad y no insertar una nueva fila.
        
        String sql = "INSERT INTO productoraee_has_carritoraee (productoRAEE_id, carritoraee_id , cantidad) VALUES (:id_ProductoRAEE, :id_carrito, :cantidad)";
        Logger.getLogger("CarritoDAO").info("Ejecutando consulta: " + sql);
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            con.createQuery(sql)
                .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                .addParameter("id_carrito", id_carrito)
                .addParameter("cantidad", cantidad)
                .executeUpdate();
            return "Producto agregado al carrito exitosamente";
        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error al ejecutar consulta: " + e.getMessage());
            return "Error al insertar el producto en el carrito";
        }
    }
}