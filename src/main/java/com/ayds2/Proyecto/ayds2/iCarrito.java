package com.ayds2.Proyecto.ayds2;

public interface iCarrito {
    public Carrito mostrarCarrito();
    public String agregarProducto(String p); // Recibe un json con la estructura de productoRAEE
    public String eliminarProducto(String p);
    public String vaciarCarrito();
    public int cantidadItems();
    public float calcularTotal();
}
