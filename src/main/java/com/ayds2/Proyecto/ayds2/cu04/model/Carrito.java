package com.ayds2.Proyecto.ayds2.cu04.model;

import java.util.List;

import lombok.Data;

@Data
public class Carrito {
    private int id_carritoRAEE; 
    private int usuario_id;  
    private List<ProductoRAEE> productos;
    private List<DetalleCarrito> detalles;   
}

