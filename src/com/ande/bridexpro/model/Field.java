/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved.
 */
package com.ande.bridexpro.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Ande
 */
public class Field {
    
    /*
    * @var type FROM class Database
    * 0 = INT -> int
    * 1 = VARCHAR -> String
    * 2 = NUMBER -> Double
    * 3 = DATE -> date
    */
    
    /*
    * @var dateFormat FROM class Database
    * {"dd/MM/yyyy", "dd/MM/yy", "MM/dd/yyyy", "MM/dd/yy", "dd-MM-yyyy", "dd-MM-yy", "MM-dd-yyyy", "MM-dd-yy"}
    */
    
    private final SimpleIntegerProperty IDField;
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty fieldOrdinal;
    private final SimpleStringProperty type;
    private final SimpleIntegerProperty rowPosition;
    private final SimpleIntegerProperty pos1;
    private final SimpleIntegerProperty pos2;
    private final SimpleIntegerProperty originalPos;
    private final SimpleBooleanProperty content;
    private final SimpleStringProperty dateFormat;
    private final SimpleIntegerProperty IDLayer;
        
    // <editor-fold defaultstate="collapsed" desc="contructor">
    public Field(int idField, String name, int fieldOrdinal, int rowPosition, int ps1, int ps2, int orgPos, boolean cntent, int idLayer){
        this(idField, name, fieldOrdinal, null, rowPosition, ps1, ps2, orgPos, cntent, null, idLayer);
    }
    
    public Field(int idField, String nm, int fo, String tp, int rp, int ps1, int ps2, int orgPos, boolean cntent, String dtFrmt, int idLayer){
        this.IDField = new SimpleIntegerProperty(idField);
        this.name = new SimpleStringProperty(nm);
        this.fieldOrdinal = new SimpleIntegerProperty(fo);
        this.type = new SimpleStringProperty(tp);
        this.rowPosition = new SimpleIntegerProperty(rp);
        this.pos1 = new SimpleIntegerProperty(ps1);
        this.pos2 = new SimpleIntegerProperty(ps2);
        this.originalPos = new SimpleIntegerProperty(orgPos);
        this.content = new SimpleBooleanProperty(cntent);
        this.dateFormat = new SimpleStringProperty(dtFrmt);
        this.IDLayer = new SimpleIntegerProperty(idLayer);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="public method">
    public int getIDField(){
        return IDField.get();
    }
    public String getName(){
        return name.get();
    }
    public int getFieldOrdinal(){
        return fieldOrdinal.get();
    }
    public String getType(){
        return type.get();
    }
    public int getRowPosition(){
        return rowPosition.get();
    }
    public int getPos1(){
        return pos1.get();
    }
    public int getPos2(){
        return pos2.get();
    }
    public int getOriginalPos(){
        return originalPos.get();
    }
    public boolean getContentStatus(){
        return content.get();
    }
    public String getDateFormat(){
        return dateFormat.get();
    }
    public int getIDLayer(){
        return IDLayer.get();
    }
    
    public void setFieldOrdinal(int fo){
        this.fieldOrdinal.set(fo);
    }
    public void setName(String nm){
        this.name.set(nm);
    }
    public void setType(String tp){
        this.type.set(tp);
    }
    public void setRowPosition(int rp){
        this.rowPosition.set(rp);
    }
    public void setPos1(int ps1){
        this.pos1.set(ps1);
    }
    public void setPos2(int ps2){
        this.pos2.set(ps2);
    }
    public void setOriginalPos(int orgPos){
        this.originalPos.set(orgPos);
    }
    public void setContentStatus(boolean cntent){
        this.content.set(cntent);
    }
    public void setDateFormat(String dtFrmt){
        this.dateFormat.set(dtFrmt);
    }
    // </editor-fold>
}
