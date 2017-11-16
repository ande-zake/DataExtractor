/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ande.bridexpro.model.CriticalReport;
import com.ande.bridexpro.model.Layer;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Administrator
 */
public class TextAreaPractic extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        TextArea ta = new TextArea();
        ta.setStyle("-fx-font-family : \"Courier New\"; -fx-font-size : 12px;");
        ta.setPrefHeight(1000);
        ta.setEditable(false);
        File file = new File("D:\\TIDUA\\_Data\\CR_Benteng_Selayar\\2016\\Desember\\20161201\\CI160.000");
        CriticalReport cr = new CriticalReport(file);
        ta.setText(cr.getText().toString());
        
        TextField tf = new TextField();
        
        Button btn = new Button("Click");
        btn.setOnAction(e->{
            blockCRLikeTrap(ta, tf.getText());
        }); 
        
        VBox vb = new VBox();
        vb.getChildren().addAll(tf,ta,btn);
        
        Scene scene = new Scene(vb);
        stage.setScene(scene);
        stage.show();
    }
    
    private void blockCRLikeTrap(TextArea ta, String trap){
        String[] lines = ta.getText().split("\\n");
        
        for(int i=0; i<lines.length; i++){
            
                Pattern pattern = Pattern.compile(trap);
                Matcher matcher = pattern.matcher(lines[i]);
                
                if(matcher.find()){
                    //highlight per baris
                    ta.selectRange(265, 500);
                }
            
        }
    }
}
