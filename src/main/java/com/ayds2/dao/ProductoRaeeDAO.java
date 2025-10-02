package com.ayds2.dao;



import org.springframework.stereotype.Repository;

import com.ayds2.model.ProductoRaee;
import com.ayds2.utils.Sql2oDAO;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

@Repository
public class ProductoRaeeDAO implements iProductoRaeeDAO {

    private final Sql2o sql2o;

    public ProductoRaeeDAO() {
        this.sql2o = Sql2oDAO.getSql2o();
    }

    @Override
    public ProductoRaee selectProducto(int id) {
        String sql = "SELECT p.id_ProductoRAEE, p.nombre, p.precio, p.descripcion, c.nombre AS categoria, p.stock " +
                 "FROM productoraee p " +
                 "JOIN categoria c ON p.categoria_id = c.id_categoria " +
                 "WHERE p.id_ProductoRAEE = :id";
        try (Connection con = sql2o.open()) {
            System.out.println("Conexión a la BD establecida correctamente!");
            return con.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(ProductoRaee.class);
        } catch (Exception e) {
            System.err.println("Error en getProducto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
