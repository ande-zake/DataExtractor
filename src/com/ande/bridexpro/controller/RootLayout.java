/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved.
 */
package com.ande.bridexpro.controller;

import com.ande.bridexpro.Main;
import com.ande.bridexpro.model.ReportProject;
import com.ande.bridexpro.util.Configuration;
import com.ande.bridexpro.util.Database;
import com.ande.bridexpro.util.ImageButton;
import com.ande.bridexpro.util.ReportTreeItem;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import static javafx.geometry.Orientation.VERTICAL;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.action.Action;

/**
 *
 * @author Ande
 */
public class RootLayout {
    private ArrayList<String> reportsMap;
    
    private BorderPane borderPane;
    private GridPane grid;
    private TreeView<String> treeViewReportList;
    private TreeItem<String> itemReport;
    private String selectedReport;
    private TabPane tabPane; 
    private Tab welcomeTab;
    
    private AnchorPane topbarPane;
    private StatusBar bottombarPane;
    
    private NotificationPane notificationPane;    
    private Notifications notificationBuilder;
    
    public RootLayout(){
         notificationBuilder = Notifications.create()
            .hideAfter(Duration.seconds(5))
            .position(Pos.BOTTOM_RIGHT);
    }
    
    // ---- public method
    public void buildBorderPane(){
        borderPane = new BorderPane();
        borderPane.setCenter(grid);
        borderPane.setTop(topbarPane);
        borderPane.setBottom(bottombarPane);
    }
    
    public BorderPane getBorderPaneRoot(){
        return borderPane;
    }
    
    public NotificationPane getNotifPane(){
        return notificationPane;
    }
        
    public void setGridContent() throws SQLException{
        reportsMap = ReportProject.getAllReports(Configuration.getDatabaseName()); 
        buildReportTree(null);
        
        // simple layout: TreeView on left, sample area on right
        grid = new GridPane();
        grid.setPadding(new Insets(5, 10, 10, 5));
        grid.setHgap(10);
        grid.setVgap(10);
        
        // --- left hand side
        // search box
        final TextField searchBox = new TextField();
        searchBox.setPromptText("Search");
        searchBox.getStyleClass().add("search-box");
        searchBox.textProperty().addListener(new InvalidationListener() {
            @Override public void invalidated(Observable o) {
                buildReportTree(searchBox.getText());
            }
        });
        GridPane.setMargin(searchBox, new Insets(5, 0, 0, 0));
        grid.add(searchBox, 0, 0);
        
        // TreeView
        //itemReport = ReportTreeItem.getTreeItem(reportsMap);
        treeViewReportList = new TreeView<>(itemReport);
        treeViewReportList.setShowRoot(false);
        treeViewReportList.getStyleClass().add("samples-tree");
        treeViewReportList.setMinWidth(200);
        treeViewReportList.setMaxWidth(200);
                
        treeViewReportList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>(){
            @Override
            public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
                if(newValue == null || newValue.getChildren().size() > 0){
                    return; 
                }
                selectedReport = newValue.getValue();
                try {
                    openCR(selectedReport);
                } catch (SQLException ex) {
                    Logger.getLogger(RootLayout.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        GridPane.setVgrow(treeViewReportList, Priority.ALWAYS);
        // GridPane.setMargin(treeViewReportList, new Insets(5, 0, 0, 0));
        grid.add(treeViewReportList, 0, 1);
        
        // --- Right hand side
        tabPane = new TabPane();
        tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        tabPane.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {
            @Override public void invalidated(Observable arg0) {
                // add updateTab method
            }
        });
        
        GridPane.setHgrow(tabPane, Priority.ALWAYS);
        GridPane.setVgrow(tabPane, Priority.ALWAYS);
        grid.add(tabPane, 1, 0, 1, 2);
                
        treeViewReportList.requestFocus();
    }
    
    public void setAnchorPaneTopBar(){
        
        topbarPane = new AnchorPane();
        VBox vbox = new VBox();
                
        // -- menu bar
        MenuBar menuBar = new MenuBar();
        
        final Menu menuFile = new Menu("File");
            MenuItem miCreateDB = new MenuItem("Create DB", new ImageView(new Image("/resources/images/file-16.png")));
                miCreateDB.setOnAction((ActionEvent t) -> {
                    // to do action
                });
            MenuItem miSetDB = new MenuItem("Set DB");
            Menu miConfig = new Menu("Configuration");
                MenuItem miSetWarna = new MenuItem("Set Warna", new ImageView(new Image("/resources/images/themes-16.png")));
                MenuItem miEditFileBFC = new MenuItem("Edit File BFC");
            miConfig.getItems().addAll(miSetWarna, miEditFileBFC);
            MenuItem miExit = new MenuItem("Exit", new ImageView(new Image("/resources/images/exit-16.png")));
        
        menuFile.getItems().addAll(miCreateDB, miSetDB, miConfig, new SeparatorMenuItem(), miExit);
        
        final Menu menuView = new Menu("View");
        final Menu menuTools = new Menu("Tools");
        final Menu menuAbout = new Menu("About");
        
        menuBar.getMenus().addAll(menuFile, menuView, menuTools, menuAbout);
               
        
        // -- menu icon
        HBox menuIcons = new HBox();
        
        final ImageButton ibCreateDB = new ImageButton("/resources/images/createDB-yellow-48.png");
            ibCreateDB.setTooltip(new Tooltip("Create Database Bridex"));
            ibCreateDB.setOnAction(e -> {
                TextInputDialog dlg = new TextInputDialog("Create Database");
                dlg.setTitle("Create Database");
                dlg.getDialogPane().setContentText("Database name?");
                //show dialog
                dlg.initOwner(Main.getPrimaryStage());
                dlg.getDialogPane().setHeaderText("Create Database");
                dlg.getDialogPane().setGraphic(new ImageView(new Image(getClass().getResource("/resources/images/createDB-yellow-48.png").toExternalForm())));
                dlg.showAndWait().ifPresent(present ->{
                    Database.createDatabase(present);
                    notificationBuilder
                            .title("Create Database")
                            .text(Database.message())                            
                            .owner(Main.getPrimaryStage())
                            .showInformation();
                });
            });
        final ImageButton ibCreateBMF = new ImageButton("/resources/images/createBMF-yellow-48.png");
            ibCreateBMF.setTooltip(new Tooltip("Create Bridex Masking File"));
            ibCreateBMF.setOnAction(e ->{
                BMF bmf = new BMF();
                //create the dialog Stage
                Stage bmfStage = new Stage();
                bmfStage.setTitle("BMF");
                bmfStage.getIcons().add(new Image("/resources/images/createBMF-yellow-48.png"));
                bmfStage.initModality(Modality.WINDOW_MODAL);
                bmfStage.initOwner(Main.getPrimaryStage());
                bmfStage.setMinHeight(600);
                bmfStage.setMinWidth(1000);
                
                bmf.setStage(bmfStage);
                Scene scene = new Scene((Parent) bmf.getPanel());
                //add css
                scene.getStylesheets().add(getClass().getResource("/resources/styles/basic.css").toExternalForm());

                bmfStage.setScene(scene);
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                bmfStage.setWidth(screenBounds.getWidth() * 0.85);
                bmfStage.setHeight(screenBounds.getHeight() * 0.85);

                //show the dialog and wait until the user closes it
                bmfStage.showAndWait();                
            });
            
        final ImageButton ibImportCR = new ImageButton("/resources/images/importCR-yellow-48.png");
            ibImportCR.setTooltip(new Tooltip("Import CR"));
        final ImageButton ibExtract = new ImageButton("/resources/images/extract-yellow-48.png");
            ibExtract.setTooltip(new Tooltip("Extract CR"));
        final ImageButton ibRedo = new ImageButton("/resources/images/redo-yellow-48.png");
            ibRedo.setTooltip(new Tooltip("Redo"));
        final ImageButton ibEditField = new ImageButton("/resources/images/edit-yellow-48.png");
            ibEditField.setTooltip(new Tooltip("Edit Field On Active Table"));
        final ImageButton ibSaveCopy = new ImageButton("/resources/images/save-yellow-48.png");
            ibSaveCopy.setTooltip(new Tooltip("Save Active Table As..."));
        final ImageButton ibAppend = new ImageButton("/resources/images/append-yellow-48.png");
            ibAppend.setTooltip(new Tooltip("Append Table"));
        final ImageButton ibJoin = new ImageButton("/resources/images/join-yellow-48.png");
            ibJoin.setTooltip(new Tooltip("Join Tables"));
        final ImageButton ibGrouping = new ImageButton("/resources/images/group-yellow-48.png");
            ibGrouping.setTooltip(new Tooltip("Group In Table"));
        final ImageButton ibDuplicate = new ImageButton("/resources/images/duplicate-yellow-48.png");
            ibDuplicate.setTooltip(new Tooltip("Duplicate In Table"));
        final ImageButton ibTimeSeries = new ImageButton("/resources/images/bar-chart-yellow-48.png");
            ibTimeSeries.setTooltip(new Tooltip("Time Series Tables"));
        final ImageButton ibExport = new ImageButton("/resources/images/export-yellow-48.png");
            ibExport.setTooltip(new Tooltip("Export Active Table"));
        final ImageButton ibScriptGen = new ImageButton("/resources/images/gear-yellow-48.png");
            ibScriptGen.setTooltip(new Tooltip("ScriptGen"));
        
        menuIcons.getChildren().addAll(ibCreateDB, ibCreateBMF, ibImportCR, new Separator(Orientation.VERTICAL), 
                ibExtract, ibRedo, ibEditField, ibSaveCopy, new Separator(Orientation.VERTICAL), ibAppend, ibJoin, 
                ibGrouping, ibDuplicate, ibTimeSeries, new Separator(Orientation.VERTICAL), ibExport, ibScriptGen);
        
        vbox.getChildren().addAll(menuBar, menuIcons); 
        
        // notificationPane
        notificationPane = new NotificationPane();
        
        String imagePath = Main.class.getResource("/resources/images/notification-pane-warning.png").toExternalForm();
        ImageView image = new ImageView(imagePath);
        notificationPane.setGraphic(image);

        notificationPane.getActions().addAll(new Action("Sync", ae -> {
                // do sync

                // then hide...
                notificationPane.hide();
        }));
        notificationPane.setContent(vbox);
        notificationPane.getStyleClass().add(NotificationPane.STYLE_CLASS_DARK);
        
        // set to root layout
        AnchorPane.setTopAnchor(notificationPane, 0.0);
        AnchorPane.setRightAnchor(notificationPane, 0.0);
        AnchorPane.setLeftAnchor(notificationPane, 0.0);
        AnchorPane.setBottomAnchor(notificationPane, 0.0);
        
        topbarPane.getChildren().add(notificationPane);        
    }
    
    public void setAnchorPaneBottomBar(){
        bottombarPane = new StatusBar();
        bottombarPane.setText("Keterangan : ");
        bottombarPane.setStyle("-fx-font : 12px 'Arial'; -fx-padding : 2 10 2 10;  -fx-pref-height : 12");
                
        //bottombarPane.getRightItems().add(new ProgressIndicator());
        //bottombarPane.getRightItems().add(new Separator(VERTICAL));
        //bottombarPane.getRightItems().add(new ProgressBar());
        //bottombarPane.getRightItems().add(new Separator(VERTICAL));
        Button button = new Button(Integer.toString(1));
        button.setBackground(new Background(new BackgroundFill(Color.YELLOW,
                new CornerRadii(2), new Insets(4))));
        bottombarPane.getRightItems().add(button);
        bottombarPane.getRightItems().add(new Separator(VERTICAL));
    }
    
    // -- utils methods
    private void buildReportTree(String searchText) {
        // rebuild the whole tree (it isn't memory intensive - we only scan
        // classes once at startup)
        itemReport = ReportTreeItem.getTreeItem(reportsMap, searchText);
        itemReport.setExpanded(true);
        
        
        if (searchText != null) {
           // with this newly built and full tree, we filter based on the search text
           //pruneReportTree(itemReport, searchText); 
           
           // FIXME weird bug in TreeView I think
           treeViewReportList.setRoot(null);
           treeViewReportList.setRoot(itemReport);
        }
        
        // and finally we sort the display a little
        sort(itemReport, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
    }
    
    private void sort(TreeItem<String> node, Comparator<TreeItem<String>> comparator) {
        node.getChildren().sort(comparator);
        for (TreeItem<String> child : node.getChildren()) {
            sort(child, comparator);
        }
    }
    
    private void openCR(String tableName) throws SQLException{
        // check if tab has been opened
        ObservableList<Tab> tabs = tabPane.getTabs();
        for(Tab eachTab : tabs){
            if(eachTab.getText().equalsIgnoreCase(tableName)){
                tabPane.getSelectionModel().select(eachTab);
                return;
            }
        }
        
        StackPane centerPane = new StackPane();
        ReportProject x = new ReportProject(tableName);
        
        centerPane.getChildren().setAll(x.getReportView());
        
        Tab newTab = new Tab(tableName);
        newTab.setContent(centerPane);
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().selectLast();        
    }
    
    private void changeToWelcomeTab(WelcomePage wPage) {
        if(null == wPage) {
            wPage = getDefaultWelcomePage();
        }
        welcomeTab = new Tab(wPage.getTitle());
        welcomeTab.setContent(wPage.getContent());
        tabPane.getTabs().setAll(welcomeTab);
    }
    
    private WelcomePage getDefaultWelcomePage() {
        // line 1
        Label welcomeLabel1 = new Label("Welcome to FXSampler!");
        welcomeLabel1.setStyle("-fx-font-size: 2em; -fx-padding: 0 0 0 5;");

        // line 2
        Label welcomeLabel2 = new Label(
                "Explore the available UI controls and other interesting projects "
                + "by clicking on the options to the left.");
        welcomeLabel2.setStyle("-fx-font-size: 1.25em; -fx-padding: 0 0 0 5;");

        WelcomePage wPage = new WelcomePage("Welcome!", new VBox(5, welcomeLabel1, welcomeLabel2));
        return wPage;
    }
}
