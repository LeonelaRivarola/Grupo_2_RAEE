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
        // Inserto una nueva compra en la tabla compra
        String sqlInsertCompra = """
            INSERT INTO compra (fecha, formaEntrega, total, id_Usuario, id_metodoPago, idPagoMP)
            VALUES (:fecha, :formaEntrega, :total, :id_Usuario, :id_metodoPago, :idPagoMP)
        """;

        try (Connection con = Sql2oDAO.getSql2o().open()) {
            org.sql2o.Query q = con.createQuery(sqlInsertCompra, true)
                    .addParameter("fecha", compra.getFechaCompra())
                    .addParameter("formaEntrega", compra.getFormaEntrega())
                    .addParameter("total", compra.getTotal())
                    .addParameter("id_Usuario", compra.getIdUsuario())
                    .addParameter("id_metodoPago", obtenerIdMetodoPago(con, compra.getMetodoPago()))
                    .addParameter("idPagoMP", compra.getIdPagoMP());

            // Recupero el id autogenerado de la compra
            Number key = (Number) q.executeUpdate().getKey();
            int idCompra = key.intValue();

            // Inserto los detalles de la compra
            String sqlDetalle = """
                INSERT INTO detallecompra (cantidad, precioUnitario, id_compra, id_ProductoRAEE)
                VALUES (:cantidad, :precioUnitario, :id_compra, :id_ProductoRAEE)
            """;

            for (DetalleCompra dc : compra.getDetalles()) {
                // Obtiene el precio del producto (puede venir del objeto ProductoRAEE dentro del DetalleCompra)
                double precioUnitario = dc.getPrecioUnitario();

                con.createQuery(sqlDetalle)
                .addParameter("cantidad", dc.getCantidad())
                .addParameter("precioUnitario", precioUnitario)
                .addParameter("id_compra", idCompra)
                .addParameter("id_ProductoRAEE", dc.getIdProductoRaee())
                .executeUpdate();
            }

        }
    }

    // Obtengo el id del método de pago según su nombre
    private int obtenerIdMetodoPago(Connection con, String nombreMetodo) {
        String sql = "SELECT idmetodoPago FROM metodopago WHERE nombre = :nombre";
        Integer id = con.createQuery(sql)
                .addParameter("nombre", nombreMetodo)
                .executeScalar(Integer.class);
        return id != null ? id : 1; // Retorno 1 si no se encuentra (por defecto)
    }
}