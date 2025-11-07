package com.ayds2.Proyecto.ayds2.cu04.model;

import java.util.List;
import com.ayds2.Proyecto.ayds2.cu02.model.ProductoRaee;
import lombok.Data;

@Data
public class Carrito {
    private int id_carritoRAEE; 
    private int usuario_id;  
    private List<ProductoRaee> productos;
    private List<DetalleCarrito> detalles;   
}