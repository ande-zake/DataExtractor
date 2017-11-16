/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved.
 */
package com.ande.bridexpro.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Ande
 */
public class MaskingFile {
        
    private ArrayList<Layer> layers;
    private ArrayList<Field> fields;
    private String name;
    private int iteratorIDLayer;
    private int iteratorIDField;
    
    // <editor-fold defaultstate="collapsed" desc="contructor">
    public MaskingFile(String name){
        layers = new ArrayList<Layer>();
        fields = new ArrayList<Field>();
        this.name = name;
        iteratorIDLayer = 0;
        iteratorIDField = 0;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="public method">
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    public ArrayList<Layer> getLayers(){
        return layers;
    }
    public void addLayer(Layer layer){
        layers.add(layer);
        iteratorIDLayer++;
    }
    public void deleteLayer(int idLayer){
        layers.remove(getIndexLayer(idLayer));
    } 
    
    public ArrayList<Field> getFields(){
        return fields; 
    }
    public void addField(Field field){
        fields.add(field);
        iteratorIDField++;
    }
    public void deleteField(int idField){
        fields.remove(getIndexField(idField));
    }
    
    public int getIteratorIDLayer(){
        return iteratorIDLayer;
    }
    public int getIteratorIDField(){
        return iteratorIDField;
    }
    
    public Field getField(int idLayer, int positionText){
        for(Field f : fields){
            int orgPosEnd = f.getOriginalPos()+(f.getPos2()-f.getPos1());
            if(f.getIDLayer() == idLayer && f.getOriginalPos() < positionText && orgPosEnd > positionText){
                return f;
            }
        }
        return null;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="private method">
    private int getIndexLayer(int idLayer){
        int index = -1;
        
        int loop = 0;
        for (Layer layer : layers) {
            if(layer.getIDLayer() == idLayer){
                return index = loop;
            }
            loop++;
        }
        return index;
    }
    
    private int getIndexField(int idField){
        int index = -1;
        
        int loop = 0;
        for (Field field : fields) {
            if(field.getIDField() == idField){
                return index = loop;
            }
            loop++;
        }
        return index;
    }
    // </editor-fold>
    
}
