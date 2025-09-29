package com.ayds2.dao;

import org.springframework.stereotype.Repository;

import com.ayds2.model.ProductoRAEE;
import com.ayds2.utils.Sql2oDAO;
import com.ayds2.model.ProductoRAEE;
import org.sql2o.Sql2o;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoRAEEDAO implements iProductoRAEEDAO {

    private final Sql2o sql2o;

    public ProductoRAEEDAO() {
        // Usamos el singleton manual
        this.sql2o = Sql2oDAO.getSql2o();
    }

    @Override
    public ProductoRAEE getProducto(int id) {
        String sql = "SELECT id, nombre, precio, descripcion, categoria_id " +
                     "FROM producto WHERE id = :id";
        try (var conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("id", id)
                    .executeAndFetchFirst(ProductoRAEE.class);
        }
    }
}
