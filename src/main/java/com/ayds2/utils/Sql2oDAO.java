package com.ayds2.utils;

import org.sql2o.Sql2o;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author Daniel
 * Singleton que obtiene el nexo con la BD
 */
public class Sql2oDAO {
    static Sql2o sql2o;

    public static Sql2o getSql2o() {
        if (sql2o == null) {
             sql2o = new Sql2o("jdbc:mysql://localhost:3306/raee", "root", "");
        }
        return sql2o;
    }
    
}