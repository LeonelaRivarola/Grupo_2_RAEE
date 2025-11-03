package com.ayds2.Proyecto.ayds2.cu04.service;

import org.springframework.stereotype.Service;
import com.ayds2.Proyecto.ayds2.cu04.dao.CarritoDAOCU04;

@Service
public class ServiceCarritoCU04 {

    private CarritoDAOCU04 carritoDAO = new CarritoDAOCU04();

    public String verCarrito(int id_carrito) {
        
        return carritoDAO.select(id_carrito);
    } 

}
