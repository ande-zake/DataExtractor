/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved.
 */
package com.ande.bridexpro.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Ande
 */
public class Layer {
    
    /**
    * Trap 
    * 'Ă' =  U+0102, 258
    * 'Ĥ' =  U+0124, 292
    * [a-zA-Z] = Letter
    * \d = Number
    */
    
    private final IntegerProperty IDLayer;
    private final StringProperty layerName;
    private final IntegerProperty countOfLines;
    private final StringProperty trap;
    private final StringProperty trapOri;
           
    final static public char LETTER_TRAP_ORI = 'Ĥ';
    final static public char NUMBER_TRAP_ORI = 'Ă';
    
    // <editor-fold defaultstate="collapsed" desc="contructor">
    public Layer(int idLayer){
        this.IDLayer = new SimpleIntegerProperty(idLayer);
        this.layerName = new SimpleStringProperty();
        this.countOfLines = new SimpleIntegerProperty();
        this.trap = new SimpleStringProperty();
        this.trapOri = new SimpleStringProperty();
    }
    
    public Layer(int idLayer, String ln, int cl, String tr, String trOri){
        this.IDLayer = new SimpleIntegerProperty(idLayer);
        this.layerName = new SimpleStringProperty(ln);
        this.countOfLines = new SimpleIntegerProperty(cl);
        this.trap = new SimpleStringProperty(tr);
        this.trapOri= new SimpleStringProperty(trOri);
    }
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc="public method">
    public int getIDLayer(){
        return IDLayer.get();
    }
    public String getLayerName(){
        return layerName.get();
    }
    public int getCountOfLines(){
        return countOfLines.get();
    }
    public String getTrap(){
        return trap.get();
    }
    public String getTrapOri(){
        return trapOri.get();
    }
    
    public void setLayerName(String ln){
        this.layerName.set(ln);
    }
    public void setTrap(String tr){
        this.trap.set(tr);
    }
    public void setTrapOri(String trOri){
        this.trapOri.set(trOri);
    }
    
    public ObservableValue IDLayerProperty(){
        return IDLayer.asObject();
    }
    public StringProperty layerNameProperty(){
        return layerName;
    }
    public ObservableValue countOfLinesProperty(){
        return countOfLines.asObject();
    }
    public StringProperty trapProperty(){
        return trap;
    }
    public StringProperty trapOriProperty(){
        return trapOri;
    }
    // </editor-fold>
}
