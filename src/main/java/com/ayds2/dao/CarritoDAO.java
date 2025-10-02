package com.ayds2.dao;

import org.sql2o.Sql2o;
import org.sql2o.Connection;

public class CarritoDAO implements iCarritoDAO {

    private final Sql2o sql2o;

    public CarritoDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void insert(int carritoId, int productoId, int cantidad) {
        String sql = "INSERT INTO productoRAEE_has_carritoRAEE (carritoRAEE_id, productoRAEE_id, cantidad) " +
                "VALUES (:carritoId, :productoId, :cantidad)";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("carritoId", carritoId)
                    .addParameter("productoId", productoId)
                    .addParameter("cantidad", cantidad)
                    .executeUpdate();
        }
    }

    @Override
    public int crearCarrito(int usuarioId) {
        String sql = "INSERT INTO carritoRAEE (usuario_id) VALUES (:usuarioId)";
        try (Connection con = sql2o.open()) {
            return (int) con.createQuery(sql, true)
                    .addParameter("usuarioId", usuarioId)
                    .executeUpdate()
                    .getKey(Integer.class);
        }
    }

    @Override
    public int getCarritoIdByUsuario(int usuarioId) {
        String sql = "SELECT id_carritoRAEE FROM carritoRAEE WHERE usuario_id = :usuarioId";
        try (Connection con = sql2o.open()) {
            Integer id = con.createQuery(sql)
                    .addParameter("usuarioId", usuarioId)
                    .executeScalar(Integer.class);
            return id != null ? id : 0;
        }
    }

}
