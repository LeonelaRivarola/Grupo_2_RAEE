package com.ayds2.Proyecto.ayds2.cu03.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import com.ayds2.Proyecto.ayds2.cu03.model.Compra;
import com.ayds2.Proyecto.ayds2.cu03.model.DetalleCompra;
import com.ayds2.Proyecto.ayds2.utils.Sql2oDAO;

@Repository
public class CompraDAO implements ICompraDAO {

    @Override
    public void registrarCompra(Compra compra) {

        try (Connection con = Sql2oDAO.getSql2o().open()) {

            // Generar la sentencia INSERT dinámicamente usando reflexión
            String insertCompraSQL = generarInsertReflexivo(compra, "compra", "idcompra");

            // Crear la query e inyectar los parámetros mediante reflexión
            org.sql2o.Query query = con.createQuery(insertCompraSQL, true);

            for (Field field : compra.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String nombreCampo = field.getName();

                // Ignorar la PK autoincremental y la lista de detalles
                if (nombreCampo.equalsIgnoreCase("idcompra") || nombreCampo.equalsIgnoreCase("detalles"))
                    continue;

                Object valor = field.get(compra);
                query.addParameter(nombreCampo, valor);
            }

            // Ejecutar el insert y obtener el ID generado
            Number key = (Number) query.executeUpdate().getKey();
            int idCompra = key.intValue();

            // Insertar los detalles de la compra
            for (DetalleCompra dc : compra.getDetalles()) {
                dc.setId_compra(idCompra);
                registrarDetalleCompra(con, dc);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al registrar compra con reflexión: " + e.getMessage());
        }
    }

    /**
     * Genera dinámicamente la sentencia SQL INSERT para una clase usando reflexión.
     */
    private String generarInsertReflexivo(Object obj, String nombreTabla, String campoAutoIncremental) {
        Class<?> clazz = obj.getClass();
        List<String> columnas = new ArrayList<>();
        List<String> valores = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String nombreCampo = field.getName();

            // Ignorar la PK y cualquier lista (detalles en nuestro caso)
            if (nombreCampo.equalsIgnoreCase(campoAutoIncremental) || field.getType().equals(List.class))
                continue;

            columnas.add(nombreCampo);
            valores.add(":" + nombreCampo);
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                nombreTabla,
                String.join(", ", columnas),
                String.join(", ", valores));

        System.out.println("SQL generado reflexivamente: " + sql);
        return sql;
    }

    /**
     * Inserta un detalle de compra usando una query normal.
     */
    private void registrarDetalleCompra(Connection con, DetalleCompra dc) {
        String sqlDetalle = """
            INSERT INTO detallecompra (cantidad, precioUnitario, id_compra, id_ProductoRAEE)
            VALUES (:cantidad, :precioUnitario, :id_compra, :id_ProductoRAEE)
        """;
        con.createQuery(sqlDetalle)
                .addParameter("cantidad", dc.getCantidad())
                .addParameter("precioUnitario", dc.getPrecioUnitario())
                .addParameter("id_compra", dc.getId_compra())
                .addParameter("id_ProductoRAEE", dc.getId_ProductoRAEE())
                .executeUpdate();
    }

    /**
     *  Obtiene el ID del método de pago según su nombre.
     */
    public int obtenerIdMetodoPagoPorNombre(String nombreMetodo) {
        try (Connection con = Sql2oDAO.getSql2o().open()) {
            String sql = "SELECT idmetodoPago FROM metodopago WHERE nombre = :nombre";
            Integer id = con.createQuery(sql)
                    .addParameter("nombre", nombreMetodo)
                    .executeScalar(Integer.class);
            return id != null ? id : 1; // Retorna 1 si no se encuentra (por defecto)
        }
    }    
}