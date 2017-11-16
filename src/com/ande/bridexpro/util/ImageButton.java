/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved  * 
 */
package com.ande.bridexpro.util;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Ande
 */
public class ImageButton extends Button {
    
    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 5, 5, 5, 5;";
    private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 6 4 4 6;";
    
    public ImageButton(String imageurl) {
        setGraphic(new ImageView(new Image(getClass().getResourceAsStream(imageurl))));
        setStyle(STYLE_NORMAL);
        setCursor(Cursor.HAND);
        
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setStyle(STYLE_PRESSED);
            }            
        });
        
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
               setStyle(STYLE_NORMAL);
            }            
        });
        
        /*
        this.styleProperty().bind(
          Bindings
            .when(this.hoverProperty())
              .then(
                new SimpleStringProperty(STYLE_PRESSED)
              )
              .otherwise(
                new SimpleStringProperty(STYLE_NORMAL)
              )
        );
        */
    }
}
