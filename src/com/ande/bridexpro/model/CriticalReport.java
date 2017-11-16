/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

/**
 *
 * @author Ande
 */
public class CriticalReport extends File implements Report{
    // name get from file name
    private String name;    
    private String description;
    
    private int COUNT_OF_LINE;
    private int MAX_CHAR_LINES;
    
    // <editor-fold defaultstate="collapsed" desc="constructor">
    //-- base on File
    public CriticalReport(File cr){
        this(cr, "");
    }
    
    public CriticalReport(File cr, String description){
        super(cr.getPath());
        this.name = getBaseName(this.getName());
        this.description = description;
        setVariableInit();
    }
    
    //-- Base On Uri
    public CriticalReport(URI uri){
        this(uri, "");
    }
    
    public CriticalReport(URI uri, String description){
        super(uri);
        this.name = getBaseName(this.getName());
        this.description = description;
        setVariableInit();
    }
    
    //-- base on absolute directory
    public CriticalReport(String dir){
        this(dir, "");
    } 
    
    public CriticalReport(String dir, String description){
        super(dir);
        this.name = getBaseName(this.getName());
        this.description = description;
        setVariableInit();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="public method">
    @Override
    public String getReportName() {
        return name;
    }

    @Override
    public String getReportDescription() {
        return description;
    }

    public String getReportVersion() {
        return reportVersion(this);
    }
      
    public void setName(String name){
        this.name = name;
    }
    
    public void setDescription(String desc){
        this.description = desc;
    }
    
    public int getCountOfLine(){
        return COUNT_OF_LINE;
    }
    
    public int getMaxCharLines(){
        return MAX_CHAR_LINES;
    }
    
    public StringBuilder getText(){
        StringBuilder sb = new StringBuilder(1024);
        String curLine = "";
        
        try{
            FileReader fr = new FileReader(this); 
            BufferedReader br = new BufferedReader(fr);
            
            while(curLine != null){
                curLine = br.readLine();
                sb.append(curLine);
                
                if(curLine != null){
                    int l = curLine.length(); 
                    //add the space until MAX_CHAR_LINES
                    if(l < MAX_CHAR_LINES){ 
                        for(int i=l; i<MAX_CHAR_LINES; i++){
                            sb.append(" ");
                        }
                    }
                }
                
                sb.append("\n");
            }
            
        }catch(Exception e){
            System.out.println(curLine);
            e.printStackTrace();
        }
        
        return sb;
    }
    
    // static method
    public static int getLinesFromString(String str){
        int result = 0;
        
        try(BufferedReader reader = new BufferedReader(new StringReader(str))){
            String line = reader.readLine();
            while( line != null ){
                result++;
                line = reader.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="private">
    
    private int getCountOfLine(File file){
        int lines = 0;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            while (reader.readLine() != null) lines++;
            reader.close();
            
        }catch(IOException e){
            e.printStackTrace();
        }
        return lines;
    }
    
    /*
    * @param nm is FIle.getName();
    */
    private String getBaseName(String nm){
        int pos = nm.lastIndexOf(".");
        if(pos > 0){
            nm = nm.substring(0, pos);
        }
        
        return nm;
    }
    
    private String reportVersion(File cr){        
        String result = null;
        
        try{
            FileReader fr = new FileReader(this); 
            BufferedReader br = new BufferedReader(fr);
            
            result = br.readLine();
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    // set COUNT_OF_LINES and MAX_CHAR_LINES
    private void setVariableInit(){
        int lines = 0;
        int max = 0;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(this));
            
            String line = reader.readLine();
            while (line != null){
                
                lines++;
                
                if(line.length() > max){
                    max = line.length();
                }
                
                line = reader.readLine();
            }
            reader.close();
            
        }catch(IOException e){
            e.printStackTrace();
        }
        
        this.COUNT_OF_LINE = lines;
        this.MAX_CHAR_LINES = max;
    }
    // </editor-fold>

}
