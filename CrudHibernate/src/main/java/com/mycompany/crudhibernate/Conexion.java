/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.crudhibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
public class Conexion {
    
   private static Connection conexion;
   
   static {
        String url = "jdbc:mysql://localhost:3306/crud?zeroDateTimeBehavior=CONVERT_TO_NULL";
        String user = "root";
        String password = "";
       
       try {
            conexion = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión establecida con la base de datos");
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   public static Connection getConexion() {
       return conexion;
   }
}
