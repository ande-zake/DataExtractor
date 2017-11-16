/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 * @author zake
 */
public class Configuration {
        
    private final static String CONFIG_FILE_DIR = System.getProperty("user.home") + File.separator +"BridexProjects" + File.separator + "myapp";
    private final static File CONFIG_FILE = new File(CONFIG_FILE_DIR + File.separator + "config.properties");
    private static Properties configProps = new Properties();
    
    static {
        try{
            // loads properties from file
            if(! CONFIG_FILE.exists()){
                Files.createDirectories(Paths.get(CONFIG_FILE_DIR));
                // sets default properties
                configProps.setProperty("dbHost", "localhost");
                configProps.setProperty("dbPort", "3306");
                configProps.setProperty("dbUser", "root");
                configProps.setProperty("dbPassword", "");
                configProps.setProperty("dbName", "");

                OutputStream outputStream = new FileOutputStream(CONFIG_FILE);
                configProps.store(outputStream, "Configuration Preferences");
                outputStream.close();
            }   
            InputStream inputStream = new FileInputStream(CONFIG_FILE);
            configProps.load(inputStream);
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
    public static void saveProperties(String[][] properties) throws IOException {
        for (String[] propertie : properties) {
            configProps.setProperty(propertie[0], propertie[1]);
        }
        
        OutputStream outputStream = new FileOutputStream(CONFIG_FILE);
        configProps.store(outputStream, "Configuration Preferences");
        outputStream.close();        
    }
    
    public static String getDatabaseName(){
        return configProps.getProperty("dbName");
    }
    
    public static void setDatabaseName(String dbName){
        configProps.setProperty("dbName", dbName);
    }
    
    public static String getDatabaseUser(){
        return configProps.getProperty("dbUser");
    }
    
    public static void setDatabaseUser(String user){
        configProps.setProperty("dbUser", user);
    }
    
    public static String getDatabasePassword(){
        return configProps.getProperty("dbPassword");
    }
    
    public static void setDatabasePassword(String password){
        configProps.setProperty("dbPassword", password);
    }
    
    public static String getDatabasePort(){
        return configProps.getProperty("dbPort");
    }
    
    public static void setDatabasePort(String port){
        configProps.setProperty("dbPort", port);
    }
    
    public static String getDatabaseHost(){
        return configProps.getProperty("dbHost");
    }
    
    public static void setDatabaseHost(String host){
        configProps.setProperty("dbHost", host);
    }
}
