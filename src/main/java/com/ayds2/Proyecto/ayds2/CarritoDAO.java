package com.ayds2.Proyecto.ayds2;

import java.util.logging.Logger;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

public class CarritoDAO implements iCarritoDAO{
    
    @Override
    public Carrito select() {
        String sql = "SELECT * FROM carritoraee WHERE usuario_id = 1"; // Por ahora solo hay un carrito
        Logger.getLogger("CarritoDAO").info("Ejecutando consulta: " + sql);
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            return con.createQuery(sql).executeAndFetchFirst(Carrito.class);
        } catch (Exception e) {
            Logger.getLogger("CarritoDAO").severe("Error al ejecutar consulta: " + e.getMessage());
            return null;
        }
    }
}
