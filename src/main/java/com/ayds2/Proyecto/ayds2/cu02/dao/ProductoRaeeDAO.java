package com.ayds2.Proyecto.ayds2.cu02.dao;

import org.springframework.stereotype.Repository;
import com.ayds2.Proyecto.ayds2.cu02.model.ProductoRaee;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;
import com.google.gson.Gson;

import org.sql2o.Connection;

@Repository
public class ProductoRaeeDAO implements iProductoRaeeDAO {

    @Override
    public String selectProducto(int id) {
        String sql = "SELECT p.id_ProductoRAEE, p.nombre, p.precio, p.descripcion, " +
             "p.categoria_id, p.stock " +
             "FROM productoraee p " +
             "WHERE p.id_ProductoRAEE = :id";
         try (Connection con = Sql2oDAO.getSql2o().open()) {
        ProductoRaee producto = con.createQuery(sql)
                .addParameter("id", id)
                .executeAndFetchFirst(ProductoRaee.class);

        if (producto == null) {
            return "{\"mensaje\": \"Producto con id " + id + " no encontrado\"}";
        }

        // Convertir a JSON usando Gson
        Gson gson = new Gson();
        return gson.toJson(producto);
        
        } catch (Exception e) {
            System.err.println("Error en getProducto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
