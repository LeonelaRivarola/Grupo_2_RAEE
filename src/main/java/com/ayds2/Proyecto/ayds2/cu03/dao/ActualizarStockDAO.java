package com.ayds2.Proyecto.ayds2.cu03.dao;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

@Repository
public class ActualizarStockDAO implements IActualizarStockDAO {
    // Resta del stock de la tabla productoraee la cantidad comprada
    @Override
    public void actualizaStock(int idProducto, int cantidad) {
        String sql = "UPDATE productoraee SET stock = stock - :cantidad WHERE id_ProductoRAEE = :idProducto";
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            con.createQuery(sql)
                    .addParameter("cantidad", cantidad)
                    .addParameter("idProducto", idProducto)
                    .executeUpdate();
        }
    }
}