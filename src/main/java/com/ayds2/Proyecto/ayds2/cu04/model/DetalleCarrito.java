package com.ayds2.Proyecto.ayds2.cu04.model;
import lombok.Data;

@Data
public class DetalleCarrito {
    private int productoRAEE_id;
    private int carritoRAEE_id;
    private int cantidad;

    public DetalleCarrito(){}

    public DetalleCarrito(int productoRAEE_id, int carritoRAEE_id, int cantidad){
        this.productoRAEE_id = productoRAEE_id;
        this.carritoRAEE_id = carritoRAEE_id;
        this.cantidad = cantidad;
    }
}