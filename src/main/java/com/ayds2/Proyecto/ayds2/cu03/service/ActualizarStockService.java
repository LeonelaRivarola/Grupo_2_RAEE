package com.ayds2.Proyecto.ayds2.cu03.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds2.Proyecto.ayds2.cu03.dao.ActualizarStockDAO;
import com.ayds2.Proyecto.ayds2.cu04.model.DetalleCarrito;

@Service
public class ActualizarStockService {

    private final Logger logger = Logger.getLogger("ActualizarStockService");

    //Inyeccion de dependencia
    @Autowired
    private ActualizarStockDAO actualizarStockDAO;

    //Recorre el carrito y actualiza el stock de cada producto restando la cantidad que figura en el detalle
    public void actualizarStock(List<DetalleCarrito> detalles) {
        for (DetalleCarrito d : detalles) {
            try {
                actualizarStockDAO.actualizaStock(d.getProductoRAEE_id(), d.getCantidad());
            } catch (Exception e) {
                logger.severe("Error actualizando stock para producto " + d.getProductoRAEE_id() + " : " + e.getMessage());
            }
        }
    }    
}