package com.ayds2.Proyecto.ayds2.model;

import lombok.Data;

@Data
public class Usuario {
    private int id_Usuario;
    private String nombre;
    private String email;
    private String contraseña;
    private int Rol_id;
}