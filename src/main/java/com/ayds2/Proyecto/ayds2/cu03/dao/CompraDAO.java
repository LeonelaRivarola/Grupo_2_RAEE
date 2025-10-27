package com.ayds2.Proyecto.ayds2.cu03.dao;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;

import com.ayds2.Proyecto.ayds2.cu03.model.Compra;
import com.ayds2.Proyecto.ayds2.cu03.model.DetalleCompra;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

@Repository
public class CompraDAO implements ICompraDAO {

    @Override
    public void registrarCompra(Compra compra) {
        String sqlInsertCompra = "INSERT INTO compra (fecha, formaEntrega, total, usuario_id, metodoPago, idPagoMP) " +
                         "VALUES (:fecha, :formaEntrega, :total, :usuario_id, :metodoPago, :idPagoMP)";

        try (Connection con = Sql2oDAO.getSql2o().open()) {
            org.sql2o.Query q = con.createQuery(sqlInsertCompra, true)
                    .addParameter("fecha", compra.getFechaCompra())
                    .addParameter("formaEntrega", compra.getFormaEntrega())
                    .addParameter("total", compra.getTotal())
                    .addParameter("usuario_id", compra.getIdUsuario())
                    .addParameter("metodoPago", compra.getMetodoPago())
                    .addParameter("idPagoMP", compra.getIdPagoMP());


            Number key = (Number) q.executeUpdate().getKey();
            int idCompra = key.intValue();

            // insertar detalles
            String sqlDetalle = "INSERT INTO detalleCompra (cantidad, precioUnitario, compra_idcompra, productoRAEE_id_ProductoRAEE) " +
                    "VALUES (:cantidad, :precioUnitario, :idCompra, :idProducto)";

            for (DetalleCompra dc : compra.getDetalles()) {
                con.createQuery(sqlDetalle)
                        .addParameter("cantidad", dc.getCantidad())
                        .addParameter("precioUnitario", dc.getPrecioUnitario())
                        .addParameter("idCompra", idCompra)
                        .addParameter("idProducto", dc.getIdProductoRaee())
                        .executeUpdate();
            }
        }
    }
}