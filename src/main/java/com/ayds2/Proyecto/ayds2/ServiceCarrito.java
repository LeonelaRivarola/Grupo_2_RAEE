package com.ayds2.Proyecto.ayds2;

import org.springframework.stereotype.Service;

@Service
public class ServiceCarrito implements iCarrito {

    @Override
    public Carrito mostrarCarrito() { //Que devuelva un carrito es temporal, lo ideal es que devuelva un String en JSON con ObjetcMapper.
        CarritoDAO carritoDAO = new CarritoDAO();
        return carritoDAO.select();
    }

    @Override
    public String agregarProducto(String p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'agregarProducto'");
    }

    @Override
    public String eliminarProducto(String p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminarProducto'");
    }

    @Override
    public String vaciarCarrito() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'vaciarCarrito'");
    }

    @Override
    public int cantidadItems() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cantidadItems'");
    }

    @Override
    public float calcularTotal() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calcularTotal'");
    }
    
}
