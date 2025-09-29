package com.ayds2.dao;

import com.ayds2.Model.Categoria;
import com.ayds2.Model.ProductoRaee;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class ProductoRaeDAO implements IRaeeDAO {
    private final Sql2o sql2o;

    public ProductoRaeDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public ProductoRaee selectById(int id) {
        String sqlRaee = "SELECT id_ProductoRAEE AS idProductoRAEE, nombre, precio, descripcion, categoria_id " +
                     "FROM productoRAEE WHERE id_ProductoRAEE = :id";
        String sqlCategoria = "SELECT id_categoria AS idCategoria, nombre " +
                          "FROM categoria WHERE id_categoria = :idCategoria";

        try (Connection con = sql2o.open()) {
            // Traemos el RAEE
            ProductoRaee raee = con.createQuery(sqlRaee)
                       .addParameter("id", id)
                       .executeAndFetchFirst(ProductoRaee.class);

            if (raee == null) return null;

            // Traemos la categoría y la seteamos
            Categoria categoria = con.createQuery(sqlCategoria)
                                 .addParameter("idCategoria", raee.getCategoria().getIdCategoria())
                                 .executeAndFetchFirst(Categoria.class);

            raee.setCategoria(categoria);

            return raee;
        }
    }
}
