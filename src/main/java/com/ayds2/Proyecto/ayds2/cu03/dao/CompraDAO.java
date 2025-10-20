package com.ayds2.Proyecto.ayds2.cu03.dao;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.ayds2.Proyecto.ayds2.cu03.model.Compra;
import com.ayds2.Proyecto.ayds2.cu03.model.DetalleCompra;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

public class CompraDAO implements ICompraDAO {
    private final Sql2o sql2o;

    public CompraDAO() {
        this.sql2o = Sql2oDAO.getSql2o();
    }

    public int registrarCompra(Compra compra) {
        String sqlInsertCompra = """
            INSERT INTO compra (idUsuario, fechaCompra, totalCompra, formaEntrega, metodoPago)
            VALUES (:idUsuario, :fechaCompra, :totalCompra, :formaEntrega, :metodoPago)
        """;

        String sqlInsertDetalle = """
            INSERT INTO detalle_compra (idCompra, idProductoRAEE, cantidad, precioUnitario)
            VALUES (:idCompra, :idProductoRAEE, :cantidad, :precioUnitario)
        """;

        try (Connection conn = sql2o.beginTransaction()) {

            // Insertar la cabecera (COMPRA)
            int idCompra = conn.createQuery(sqlInsertCompra, true)
                    .addParameter("idUsuario", compra.getIdUsuario())
                    .addParameter("fechaCompra", compra.getFechaCompra())
                    .addParameter("totalCompra", compra.getTotal())
                    .addParameter("formaEntrega", compra.getFormaEntrega())
                    .addParameter("metodoPago", compra.getMetodoPago())
                    .executeUpdate()
                    .getKey(Integer.class);

            // Insertar los detalles (DETALLE_COMPRA)
            List<DetalleCompra> detalles = compra.getDetalles();
            if (detalles != null && !detalles.isEmpty()) {
                for (DetalleCompra d : detalles) {
                    conn.createQuery(sqlInsertDetalle)
                            .addParameter("idCompra", idCompra)
                            .addParameter("idProductoRAEE", d.getIdProductoRaee())
                            .addParameter("cantidad", d.getCantidad())
                            .addParameter("precioUnitario", d.getPrecioUnitario())
                            .executeUpdate();
                }
            }

            // Confirmar la transacción
            conn.commit();
            return idCompra; // Éxito

        } catch (Exception e) {
            System.err.println(" Error al registrar la compra: " + e.getMessage());
            return -1; // Falla
        }
    }
}
