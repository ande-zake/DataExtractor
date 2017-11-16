/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
 
/**
 *
 * @author ande
 * 
 * this class relation with configuration
 * some of variable like dbName, dbUser, dbPassword, dbHost will save to File Configuration
 */
public class Database {
    
    private static Connection connect;
    
    /*
    * @var dataType
    * 0 = INT -> integer
    * 1 = VARCHAR -> String
    * 2 = NUMBER -> Double
    * 3 = DATE -> date
    */
        
    public static final String[] DATATYPE = {"INT","VARCHAR", "NUMBER", "DATE"};
    public static final String[] DATEFORMAT = {"dd/MM/yyyy", "dd/MM/yy", "MM/dd/yyyy", "MM/dd/yy", "dd-MM-yyyy", "dd-MM-yy", "MM-dd-yyyy", "MM-dd-yy"};
    private static String message;
    
    static {
        
    }
       
    public static Connection connect() {
        if(connect == null){
            try {                
                String user = Configuration.getDatabaseUser();
                String pass = Configuration.getDatabasePassword();
                String name = Configuration.getDatabaseName();
                String host = Configuration.getDatabaseHost();
                String port = Configuration.getDatabasePort();
                
                //String url  ="jdbc:mysql://"+ host +":"+ port +"/"+name;
                String url  ="jdbc:mysql://"+host+":"+port+"/"+name+"?autoReconnect=true&useSSL=false";
                
                //DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                connect = DriverManager.getConnection(url, user, pass);
                message = "Connection to Database";
            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                //System.out.println("WARNING: Can not create connection!");
                message = "WARNING: Can not create connection !";
                return null;
            }
        }
        return connect;   
    }
    
    public static void createDatabase(String dbName){
        try{
            if(connect == null){
                connect = connect();
            }
            Statement s = connect.createStatement();
            int result = s.executeUpdate("CREATE DATABASE "+dbName);
            Configuration.setDatabaseName(dbName);
            
            message = "Create Database \""+ dbName+"\" is Success";
        }catch(SQLException e){
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            //System.out.println("WARNING: Can not Delete database!");
            message = "WARNING: Can not Delete database!";
        }
    }
    
    public static void deleteDatabase(String dbName){
        try{
           if(connect == null){
                connect = connect();
            }
            Statement s = connect.createStatement();
            int result = s.executeUpdate("DROP DATABASE "+dbName);
            if(Configuration.getDatabaseName().equals(dbName)) Configuration.setDatabaseName("");
            
            message = "Delete Database \""+ dbName+"\" is Success";
        }catch(SQLException e){
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            //System.out.println("WARNING: Can not Delete database!");
            message = "WARNING: Can not Delete database!";
        }
    }
    
    public static ResultSet readTable(String tableName){
        
        ResultSet result = null;
        
        try{
           if(connect == null){
                connect = connect();
            }
            Statement s = connect.createStatement();
            result = s.executeQuery("SELECT * FROM "+tableName);
            
        }catch(SQLException e){
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("WARNING: Can not create connection!");
            return null;
            //System.out.println("WARNING: Can not Read table!");
        }
        return result;
    }
    
    /*
    * get all database 
    */
    public static ResultSet getAllTables(String dbName){
        ResultSet result = null;
        
        try{
           if(connect == null){
                connect = connect();
            }
           
           if(isConnect()){
               DatabaseMetaData s = connect.getMetaData();
               result = s.getTables(null, null, "%", null);
           }
            
        }catch(SQLException e){
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("WARNING: Can not Read All tables!");
            return null;
            //System.out.println("WARNING: Can not Read All tables!");
        }
        
        return result;
    }
    
    public static boolean isConnect(){
        if(connect == null){
            return false;
        }
        else{
            return true;
        }
    }
    
    public static String message(){
        return message;
    }
}
