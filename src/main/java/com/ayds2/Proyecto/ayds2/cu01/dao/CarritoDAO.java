package com.ayds2.Proyecto.ayds2.cu01.dao;

import java.util.logging.Logger;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

public class CarritoDAO implements iCarritoDAO {
   
    // CU-01: Agregar producto al carrito
    @Override
    public String insert(int id_carrito, int id_ProductoRAEE, int cantidad) {

        // 1️⃣ Validar que la cantidad sea positiva
        if (cantidad <= 0) {
            Logger.getLogger("CarritoDAO").warning("La cantidad debe ser un valor positivo.");
            return "La cantidad debe ser un valor positivo";
        }
        
        Logger.getLogger("CarritoDAO").info("Verificando stock del producto antes de insertarlo en el carrito.");

        // 2️⃣ Verificar que el producto exista y que haya stock suficiente
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String sql = "SELECT stock FROM productoraee WHERE id_ProductoRAEE = :id_ProductoRAEE";
            Integer stock = con.createQuery(sql)
                .addParameter("id_ProductoRAEE", id_ProductoRAEE)
                .executeScalar(Integer.class);

            if (stock == null) {
                Logger.getLogger("CarritoDAO").warning("El producto no existe en la base de datos.");
                return "El producto no existe en la base de datos";
            }

            if (cantidad > stock) {
                Logger.getLogger("CarritoDAO").warning("La cantidad solicitada excede el stock disponible.");
                return "La cantidad solicitada excede el stock disponible";
            }

        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error al verificar el stock del producto: " + e.getMessage());
            return "Error al verificar el stock del producto";
        }

        Logger.getLogger("CarritoDAO").info("Stock suficiente, verificando si el producto ya está en el carrito.");

        // 3️⃣ Verificar si el producto ya está en el carrito
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

            // 4️⃣ Si el producto ya existe → actualizar la cantidad
            if (productoYaEnCarrito) {
                Logger.getLogger("CarritoDAO").info("El producto ya existe en el carrito, se actualizará la cantidad.");

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
                // 5️⃣ Si no existe → insertar nuevo producto en el carrito
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
            Logger.getLogger("CarritoDAO").severe("Error al insertar o actualizar producto en carrito: " + e.getMessage());
            return "Error al insertar o actualizar producto en carrito";
        }
    }
}