package com.ayds2.Proyecto.ayds2.cu03.dao;

import java.lang.reflect.Field;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ayds2.Proyecto.ayds2.cu03.model.Compra;
import com.ayds2.Proyecto.ayds2.cu03.model.DetalleCompra;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

@Repository
public class CompraDAO implements ICompraDAO {
    private static final Logger logger = LoggerFactory.getLogger(CompraDAO.class);

    @Override
    public void registrarCompra(Compra compra) {
        try (Connection con = Sql2oDAO.getSql2o().open()) {

            // Insertar la compra usando reflexión
            int idCompra = insertarReflexivo(con, compra, "compra", "idcompra");

            // Insertar cada detalle de la compra usando el mismo método reflexivo
            for (DetalleCompra dc : compra.getDetalles()) {
                dc.setId_compra(idCompra);
                insertarReflexivo(con, dc, "detallecompra", "idDetalleCompra"); // asumiendo que DetalleCompra tiene idDetalle
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error al registrar compra y detalles con reflexión", e);
        }
    }

    /**
     * Inserta cualquier objeto en la base de datos usando reflexión.
     */
    private int insertarReflexivo(Connection con, Object obj, String nombreTabla, String campoPK)
            throws IllegalAccessException {

        String sql = generarInsertReflexivo(obj, nombreTabla, campoPK);
        org.sql2o.Query query = con.createQuery(sql, true);

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String nombreCampo = field.getName();

            // Ignorar la PK autoincremental y listas
            if (nombreCampo.equalsIgnoreCase(campoPK) || List.class.isAssignableFrom(field.getType()))
                continue;

            Object valor = field.get(obj);
            query.addParameter(nombreCampo, valor);
        }

        Number key = (Number) query.executeUpdate().getKey();
        return key != null ? key.intValue() : -1;
    }

    /**
     * Genera la sentencia SQL INSERT dinámicamente usando reflexión sobre el objeto.
     */
    private String generarInsertReflexivo(Object obj, String nombreTabla, String campoPK) {
        List<String> columnas = new java.util.ArrayList<>();
        List<String> valores = new java.util.ArrayList<>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String nombreCampo = field.getName();

            if (nombreCampo.equalsIgnoreCase(campoPK) || List.class.isAssignableFrom(field.getType()))
                continue;

            columnas.add(nombreCampo);
            valores.add(":" + nombreCampo);
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                nombreTabla,
                String.join(", ", columnas),
                String.join(", ", valores));

        logger.debug("SQL generado reflexivamente: {}", sql);
        return sql;
    }

    /**
     * Obtiene el ID del método de pago según su nombre.
     */
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
