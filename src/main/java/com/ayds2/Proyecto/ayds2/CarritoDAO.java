package com.ayds2.Proyecto.ayds2;

//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
import java.util.logging.Logger;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;
//import com.fasterxml.jackson.databind.ObjectMapper;

public class CarritoDAO implements iCarritoDAO{
    /* 
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
    } */

    // CU-01: Agregar a carrito.
    @Override
    public String insert(int id_carrito, int id_ProductoRAEE, int cantidad) {

        //pequeño control para que no se puedan insertar cantidades negativas o cero.
        if (cantidad <= 0) {
            Logger.getLogger("CarritoDAO").warning("La cantidad debe ser un valor positivo.");
            return "La cantidad debe ser un valor positivo";
        }
        
        Logger.getLogger("CarritoDAO").info("Verificando stock del producto antes de insertarlo en el carrito.");
        //lo primero que hay que chequear es si la cantidad es menor o igual al valor que hay de stock del producto.
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String sql = "SELECT stock FROM productoraee WHERE id_ProductoRAEE = :id_ProductoRAEE";
            Logger.getLogger("CarritoDAO").info("Ejecutando consulta: " + sql);
            int stock = con.createQuery(sql)
                .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                .executeScalar(Integer.class);

            if (cantidad > stock) {
                Logger.getLogger("CarritoDAO").warning("La cantidad solicitada excede el stock disponible.");
                return "La cantidad solicitada excede el stock disponible";
            }
        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error al verificar el stock del producto: " + e.getMessage());
            return "Error al verificar el stock del producto";
        }
        Logger.getLogger("CarritoDAO").info("La cantidad solicitada está dentro del stock disponible, procediendo a insertar o actualizar el producto en el carrito.");

        // En segundo lugar verifico si el producto ya existe en el carrito.
        try (Connection con1 = Sql2oDAO.getSql2o().open()) {
            boolean productoraee_has_carritoraeeBefore = con1.createQuery("SELECT COUNT(*) FROM productoraee_has_carritoraee WHERE productoRAEE_id = :id_ProductoRAEE AND carritoraee_id = :id_carrito")
                .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                .addParameter("id_carrito", id_carrito)
                .executeScalar(Boolean.class);

            //si ya existe, hago un update de la cantidad. Si no existe, hago el insert.
            if (productoraee_has_carritoraeeBefore) {
                Logger.getLogger("CarritoDAO").info("El producto ya existe en el carrito, se va a relizar un update.");
                String updateSql = "UPDATE productoraee_has_carritoraee SET cantidad = cantidad + :cantidad WHERE productoRAEE_id = :id_ProductoRAEE AND carritoraee_id = :id_carrito";
                con1.createQuery(updateSql)
                    .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                    .addParameter("id_carrito", id_carrito)
                    .addParameter("cantidad", cantidad)
                    .executeUpdate();
                updateSql = "UPDATE productoraee SET stock = stock - :cantidad WHERE id_ProductoRAEE = :id_ProductoRAEE";
                con1.createQuery(updateSql)
                    .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                    .addParameter("cantidad", cantidad)
                    .executeUpdate();
                return "Cantidad del producto actualizada exitosamente";

            } else {
                String insertSql = "INSERT INTO productoraee_has_carritoraee (productoRAEE_id, carritoraee_id , cantidad) VALUES (:id_ProductoRAEE, :id_carrito, :cantidad)";
                 try (Connection con2 = Sql2oDAO.getSql2o().open()) {
                    con2.createQuery(insertSql)
                        .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                        .addParameter("id_carrito", id_carrito)
                        .addParameter("cantidad", cantidad)
                        .executeUpdate();
                    insertSql = "UPDATE productoraee SET stock = stock - :cantidad WHERE id_ProductoRAEE = :id_ProductoRAEE";
                    con2.createQuery(insertSql)
                        .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                        .addParameter("cantidad", cantidad)
                        .executeUpdate();
                    return "Producto agregado al carrito exitosamente";
                    } catch (Exception e) {
                        Logger.getLogger("CarritoDAO").severe("Error al ejecutar consulta: " + e.getMessage());
                        return "Error al insertar el producto en el carrito";
                }
            }
        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error al verificar existencia del producto en el carrito: " + e.getMessage());
            return "Error al verificar existencia del producto en el carrito";
        }
    }
}