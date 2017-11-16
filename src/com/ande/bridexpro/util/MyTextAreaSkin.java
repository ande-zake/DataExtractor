/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.util;

import com.sun.javafx.scene.control.skin.TextAreaSkin;
import com.sun.javafx.scene.control.skin.TextInputControlSkin;
import java.lang.reflect.Field;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TextArea;

import javafx.beans.binding.DoubleBinding;

/**
 *
 * @author Administrator
 */
public class MyTextAreaSkin extends TextAreaSkin {

    public MyTextAreaSkin(TextArea textInput) {
        super(textInput);
        caretVisible = new BooleanBinding(){
            {
                bind(
                        textInput.focusedProperty(), 
                        textInput.anchorProperty(),
                        textInput.caretPositionProperty(),
                        textInput.disableProperty(),
                        displayCaret,
                        blinkProperty()
                );
            }
            @Override
            protected boolean computeValue() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                // RT-10682 : On windows, we show the caret during selection,
                //            but on others we hide it
                return !blinkProperty().get() && displayCaret.get() && textInput.isFocused() && 
                        (textInput.getCaretPosition() == textInput.getAnchor()) && 
                        !textInput.isDisabled();
            }
        };
        caretPath.opacityProperty().bind(new DoubleBinding(){
            {
                bind(caretVisible);
            }
            @Override
            protected double computeValue() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                return caretVisible.get() ? 1.0 : 0.0;
            }
        });
    }
    
    BooleanProperty blinkAlias;
    
    BooleanProperty blinkProperty(){
        if(blinkAlias == null){
            Class<?> clazz = TextInputControlSkin.class;
            try{
                Field field = clazz.getDeclaredField("blink");
                field.setAccessible(true);
                blinkAlias = (BooleanProperty) field.get(this);
            }catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return blinkAlias;
    }
    
}
