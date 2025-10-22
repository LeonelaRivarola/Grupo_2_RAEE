package com.ayds2.Proyecto.ayds2.cu03.dao;

import java.util.logging.Logger;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;

import com.ayds2.Proyecto.ayds2.cu03.model.Compra;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

@Repository
public class CompraDAO implements ICompraDAO {

    private static final Logger log = Logger.getLogger(CompraDAO.class.getName());

    @Override
    public int registrarCompra(Compra compra) {
        int idGenerado = -1;
        String sqlInsertCompra = """
            INSERT INTO compra (idUsuario, fechaCompra, totalCompra, formaEntrega, metodoPago)
            VALUES (:idUsuario, NOW(), :total, :formaEntrega, :metodoPago)
        """;

        String sqlInsertDetalle = """
            INSERT INTO detalle_compra (idCompra, idProducto, cantidad, precioUnitario)
            VALUES (:idCompra, :idProducto, :cantidad, :precioUnitario)
        """;

        try (Connection con = Sql2oDAO.getSql2o().beginTransaction()) {
            idGenerado = (int) con.createQuery(sqlInsertCompra, true)
                    .addParameter("idUsuario", compra.getIdUsuario())
                    .addParameter("total", compra.getTotal())
                    .addParameter("formaEntrega", compra.getFormaEntrega())
                    .addParameter("metodoPago", compra.getMetodoPago())
                    .executeUpdate()
                    .getKey();

            // Insertar detalles
            for (var detalle : compra.getDetalles()) {
                con.createQuery(sqlInsertDetalle)
                        .addParameter("idCompra", idGenerado)
                        .addParameter("idProducto", detalle.getIdProductoRaee())
                        .addParameter("cantidad", detalle.getCantidad())
                        .addParameter("precioUnitario", detalle.getPrecioUnitario())
                        .executeUpdate();
            }

            con.commit();
        } catch (Exception e) {
            log.severe("Error registrando compra: " + e.getMessage());
        }

        return idGenerado;
    }

    @Override
    public void vaciarCarrito(int idCarrito) {
        String sqlDelete = "DELETE FROM productoraee_has_carritoraee WHERE carritoRAEE_id = :idCarrito";

        try (Connection con = Sql2oDAO.getSql2o().open()) {
            con.createQuery(sqlDelete)
                .addParameter("idCarrito", idCarrito)
                .executeUpdate();
        } catch (Exception e) {
            log.severe("Error al vaciar carrito: " + e.getMessage());
        }
    }
}