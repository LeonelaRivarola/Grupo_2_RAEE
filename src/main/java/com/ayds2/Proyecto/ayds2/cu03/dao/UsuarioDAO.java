package com.ayds2.Proyecto.ayds2.cu03.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

@Repository
public class UsuarioDAO implements IUsuarioDAO {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAO.class);

    // Devuelvo el email del usaurio dado su id
    @Override
    public String obtenerEmailPorId(int usuarioId) {
        String sql = "SELECT email FROM usuario WHERE id_usuario = :usuarioId";
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            return con.createQuery(sql)
                    .addParameter("usuarioId", usuarioId)
                    .executeScalar(String.class);
        } catch (Exception e) {
            logger.error("Error obteniendo email de usuario: {}", e.getMessage());
            return "sin_email@prueba.com";
        }
    }
}