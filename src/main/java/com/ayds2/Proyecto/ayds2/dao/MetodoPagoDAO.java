package com.ayds2.Proyecto.ayds2.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;

import com.ayds2.Proyecto.ayds2.iface.IMetodoPagoDAO;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

@Repository
public class MetodoPagoDAO implements IMetodoPagoDAO{
    private static final Logger logger = LoggerFactory.getLogger(MetodoPagoDAO.class);

    @Override
    public int obtenerIdMetodoPagoPorNombre(String nombreMetodo) {
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String sql = "SELECT idmetodoPago FROM metodopago WHERE nombre = :nombre";
            Integer id = con.createQuery(sql)
                    .addParameter("nombre", nombreMetodo)
                    .executeScalar(Integer.class);
            return id != null ? id : 1; // Retorna 1 si no se encuentra (por defecto)
        } catch (Exception e) {
            logger.error("Error al obtener ID de método de pago '{}'", nombreMetodo, e);
            return 1;
        }
    }
}