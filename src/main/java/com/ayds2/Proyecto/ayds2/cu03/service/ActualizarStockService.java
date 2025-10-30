package com.ayds2.Proyecto.ayds2.cu03.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ayds2.Proyecto.ayds2.cu03.dao.ActualizarStockDAO;
import com.ayds2.Proyecto.ayds2.cu04.model.DetalleCarrito;

@Service
public class ActualizarStockService {

    private static final Logger logger = LoggerFactory.getLogger(ActualizarStockService.class);

    //Inyeccion de dependencia
    @Autowired
    private ActualizarStockDAO actualizarStockDAO;

    //Recorre el carrito y actualiza el stock de cada producto restando la cantidad que figura en el detalle
    public void actualizarStock(List<DetalleCarrito> detalles) {
        for (DetalleCarrito d : detalles) {
            try {
                actualizarStockDAO.actualizaStock(d.getProductoRAEE_id(), d.getCantidad());
            } catch (Exception e) {
                logger.error("Error actualizando stock para producto {}: {}", d.getProductoRAEE_id(), e.getMessage());
            }
        }
    }    
}