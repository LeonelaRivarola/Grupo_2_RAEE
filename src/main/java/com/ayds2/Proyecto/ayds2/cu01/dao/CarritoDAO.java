package com.ayds2.Proyecto.ayds2.cu01.dao;

import java.util.logging.Logger;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

public class CarritoDAO implements iCarritoDAO{
   
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