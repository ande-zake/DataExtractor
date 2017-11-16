/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved.
 */
package com.ande.bridexpro;

import com.ande.bridexpro.controller.RootLayout;
import com.ande.bridexpro.util.Database;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.NotificationPane;

/**
 *
 * @author Ande
 */
public class Main extends Application {
    
    private static Stage primaryStage;
    
    private RootLayout rootLayout;
    
    // <editor-fold defaultstate="collapsed" desc="public method">
        
    public Main(){
        // Constructor is called after BEFORE_LOAD.
        //System.out.println("MyApplication constructor called, thread: " + Thread.currentThread().getName());
    }
    
    @Override
    public void init() throws Exception {
        //System.out.println("MyApplication#init (doing some heavy lifting), thread: " + Thread.currentThread().getName());

        rootLayout = new RootLayout();
        
        // for build the GUI must be insequence
        rootLayout.setAnchorPaneTopBar();
        rootLayout.setGridContent();
        rootLayout.setAnchorPaneBottomBar();
        rootLayout.buildBorderPane();
        
        // after that, called start method
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //LauncherImpl.launchApplication(Main.class, BridexPreloader.class, args);
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws SQLException, IOException {
        
        primaryStage = stage; 
        
        Node root = rootLayout.getBorderPaneRoot();
        
        // set scene
        Scene scene = new Scene((Parent) root);  
        scene.getStylesheets().add(getClass().getResource("/resources/styles/basic.css").toExternalForm());
        
        // set stage
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("Bridex Pro");
        primaryStage.getIcons().add(new Image("/resources/images/logo-1.png"));
        primaryStage.setIconified(true);
        
        // set width / height values to be 75% of users screen resolution
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth() * 0.90);
        primaryStage.setHeight(screenBounds.getHeight() * 0.90);

        // will run after windows show
        
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent window) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Check the connection database
                        if(! Database.isConnect()){
                            NotificationPane np = rootLayout.getNotifPane();
                            np.show("Database is not Connect.\nPlease, check Your Database Connection!");
                        }
                    }
                });
            }
        });
        
        primaryStage.show();
        primaryStage.setIconified(false);
    }
    
    public static Stage getPrimaryStage(){
        return primaryStage != null ? primaryStage : null;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="private method">
    // </editor-fold>
    
}
