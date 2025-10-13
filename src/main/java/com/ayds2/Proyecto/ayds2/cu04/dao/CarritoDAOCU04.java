package com.ayds2.Proyecto.ayds2.cu04.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CarritoDAOCU04 implements iCarritoDAOCU04{
   
    // CU-04: Agregar a carrito.
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
                "SELECT pr.id_ProductoRAEE, pr.nombre, pr.precio, pr.descripcion, pr.categoria_id,pr.stock, pc.cantidad " +
                "FROM productoraee pr JOIN productoraee_has_carritoraee pc ON pr.id_ProductoRAEE = pc.productoRAEE_id WHERE pc.carritoRAEE_id = :id_carrito")
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

}