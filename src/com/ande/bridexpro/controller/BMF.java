/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.controller;

import com.ande.bridexpro.model.CriticalReport;
import com.ande.bridexpro.model.Field;
import com.ande.bridexpro.model.Layer;
import com.ande.bridexpro.model.MaskingFile;
import com.ande.bridexpro.util.Database;
import com.ande.bridexpro.util.ImageButton;
import com.ande.bridexpro.util.StyleInfo;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.MasterDetailPane;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.fxmisc.richtext.InlineStyleTextArea;
import org.fxmisc.richtext.StyleSpans;

/**
 *
 * @author Administrator
 */
public class BMF {
    private BorderPane root;
    private Stage bmfStage;
    private StatusBar statusbar;
    MasterDetailPane rootContent;
    private MasterDetailPane content;
    private InlineStyleTextArea<StyleInfo> textAreaCR;
    private InlineStyleTextArea<StyleInfo> textAreaLayer;
    private InlineStyleTextArea<StyleInfo> textAreaTrap;
    private TableView<Layer> layerTable;
    private GridPane fieldGrid;
    private TabPane tabPaneProperties;
    private TextField nameFieldTF;
    private Label pos1Label;
    private Label pos2Label;
    private Label seqFieldLabel;    
    private Label rowFieldlabel;
    private Label idFieldLabel;
    private Label idLayerlabel;
    private Label originalPosFieldLabel;
    private ComboBox<String> dataType;
    private ComboBox<String> isContent;
    private ComboBox<String> dateFormat;
    private Label propertyLabel;    
    private final ValidationSupport validationSupport = new ValidationSupport();
    private final Color colorMark = Color.LIGHTGREY;
    private final Color colorAktif = Color.LIGHTGREEN;
    
    private MaskingFile MF;
       
    private Notifications notificationBuilder;
    
    public BMF(){
        initTextAreaTrap();
        initTextAreaSelectedLayer();
        initTextAreaCR();
        
        notificationBuilder = Notifications.create()
            .hideAfter(Duration.seconds(5))
            .position(Pos.BOTTOM_RIGHT);
    }
    
    public void initialize(){
        
    }
    
    public void setStage(Stage stage){
        this.bmfStage = stage;
    }
    
    protected void setMaskingFile(MaskingFile MF){
        this.MF = MF;
    }
    
    public Node getPanel(){
        AnchorPane topbar = topbar();
        statusbar = new StatusBar();
            statusbar.setText("");
            statusbar.setStyle("-fx-font : 12px 'Arial'; -fx-padding : 2 10 2 10;  -fx-pref-height : 12");        
        setContent();
            
        root = new BorderPane();
            root.setTop(topbar);
            root.setBottom(statusbar);
            root.setCenter(rootContent);
        
        return root;
    }
    
    //--- private method
    private AnchorPane topbar(){
        AnchorPane topbar = new AnchorPane();
        
        VBox vbox = new VBox();
                
        // -- menu bar
        MenuBar menuBar = new MenuBar();
        
        final Menu menuFile = new Menu("File");
            MenuItem miOpenCR = new MenuItem("Open CR", new ImageView(new Image("/resources/images/bmf/CR-16.png")));
                miOpenCR.setOnAction((ActionEvent t) -> {
                    openCR();
                });
            MenuItem miOpenBMF = new MenuItem("Open BMF", new ImageView(new Image("/resources/images/bmf/bmf-16.png")));
            MenuItem miSaveBMF = new MenuItem("Save BMF", new ImageView(new Image("/resources/images/bmf/save-bmf-16.png")));
            MenuItem miSaveBMFAs = new MenuItem("Save BMF As...");
            MenuItem miCloseCR = new MenuItem("Close CR");
            MenuItem miCloseTemplate = new MenuItem("Close Template");
            MenuItem miPreviewTable = new MenuItem("Preview Table", new ImageView(new Image("/resources/images/bmf/preview-16.png")));
            MenuItem miExit = new MenuItem("Exit", new ImageView(new Image("/resources/images/bmf/close-16.png")));
        
        menuFile.getItems().addAll(miOpenCR, miOpenBMF, new SeparatorMenuItem(), miSaveBMF, miSaveBMFAs, new SeparatorMenuItem(), 
                new SeparatorMenuItem(), miCloseCR, miCloseTemplate, new SeparatorMenuItem(), miPreviewTable, miExit);
        
        final Menu menuLayer = new Menu("Layer");
        final Menu menuView = new Menu("View");
        
        menuBar.getMenus().addAll(menuFile, menuLayer, menuView);
               
        
        // -- menu icon
        HBox menuIcons = new HBox();
        
        final ImageButton ibOpenCR = new ImageButton("/resources/images/bmf/CR-32.png");
            ibOpenCR.setTooltip(new Tooltip("Open CR"));
            ibOpenCR.setOnAction(e -> {
                openCR();
            });
        final ImageButton ibOpenBMF = new ImageButton("/resources/images/bmf/bmf-32.png");
            ibOpenBMF.setTooltip(new Tooltip("Open BMF"));
            ibOpenBMF.setOnAction(e ->{
                
            });
            
        final ImageButton ibSaveBMF = new ImageButton("/resources/images/bmf/save-bmf-32.png");
            ibSaveBMF.setTooltip(new Tooltip("Save BMF"));
        final ImageButton ibLayer = new ImageButton("/resources/images/bmf/layer-32.png");
            ibLayer.setTooltip(new Tooltip("Create Layer Standar"));
            ibLayer.setOnAction(e->{
                createLayerStandar();
            });
        final ImageButton ibExcludeLayer = new ImageButton("/resources/images/bmf/exclude-layer-32.png");
            ibExcludeLayer.setTooltip(new Tooltip("Create Exclude Layer"));
            ibExcludeLayer.setOnAction(e->{
                
            });
        final ImageButton ibCekTrapLayer = new ImageButton("/resources/images/bmf/check-32.png");
            ibCekTrapLayer.setTooltip(new Tooltip("Cek Trap To Layer"));
            ibCekTrapLayer.setOnAction(e->{
                // change block CR area that same with trap must after refresh value on layerTable
                macthTrapWithCR();
            });
        final ImageButton ibSaveLayer = new ImageButton("/resources/images/bmf/save-layer-32.png");
            ibSaveLayer.setTooltip(new Tooltip("Save Layer"));
            ibSaveLayer.setOnAction(e->{
                if(layerTable.getItems().isEmpty()){
                    notificationBuilder
                            .title("Layer")
                            .text("Layer is Empty. Cannot to save Anything in Layer.")                            
                            .owner(bmfStage)
                            .showWarning();
                    return;
                }
                refreshValueOnLayerTable();
                content.setShowDetailNode(false);
            }); 
        final ImageButton ibDeleteLayer = new ImageButton("/resources/images/bmf/trash-32.png");
            ibDeleteLayer.setTooltip(new Tooltip("Delete Layer"));
            ibDeleteLayer.setOnAction(e->{
                deleteSelectedLayer();
            }); 
        final ImageButton ibLetter = new ImageButton("/resources/images/bmf/letter-32.png");
            ibLetter.setTooltip(new Tooltip("Letter"));
            ibLetter.setOnAction(e->{
                if(textAreaTrap.getText().isEmpty()){
                    notificationBuilder
                            .title("Trap")
                            .text("Layer is Not Active")                            
                            .owner(bmfStage)
                            .showWarning();
                    return;
                }
                textAreaTrap.replaceSelection(String.valueOf(Layer.LETTER_TRAP_ORI));
                refreshValueOnLayerTable();
            });
        final ImageButton ibNumber = new ImageButton("/resources/images/bmf/number-32.png");
            ibNumber.setTooltip(new Tooltip("Number"));
            ibNumber.setOnAction(e->{
                if(textAreaTrap.getText().isEmpty()){
                    notificationBuilder
                            .title("Trap")
                            .text("Layer is Not Active")                            
                            .owner(bmfStage)
                            .showWarning();
                    return;
                }
                textAreaTrap.replaceSelection(String.valueOf(Layer.NUMBER_TRAP_ORI));
                refreshValueOnLayerTable();
            });          
        final ImageButton ibSeqField = new ImageButton("/resources/images/bmf/list-32.png");
            ibSeqField.setTooltip(new Tooltip("Field Sequence"));
            ibSeqField.setOnAction((ActionEvent e) -> {
                FieldSequence fs = new FieldSequence(MF);
                //create the dialog Stage
                Stage fsStage = new Stage();
                fsStage.setTitle("Field Sequence");
                fsStage.getIcons().add(new Image("/resources/images/bmf/list-32.png"));
                fsStage.initModality(Modality.WINDOW_MODAL);
                fsStage.initOwner(bmfStage);
                fsStage.setMinHeight(200);
                fsStage.setMinWidth(500);
                
                fs.setStage(fsStage);
                Scene scene = new Scene((Parent) fs.getPanel());
                //add css
                scene.getStylesheets().add(getClass().getResource("/resources/styles/basic.css").toExternalForm());
                
                fsStage.setScene(scene);
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                fsStage.setWidth(screenBounds.getWidth() * 0.60);
                fsStage.setHeight(screenBounds.getHeight() * 0.60);
                
                //show the dialog and wait until the user closes it
                fsStage.showAndWait();
            });
        final ImageButton ibPreview = new ImageButton("/resources/images/bmf/preview-32.png");
            ibPreview.setTooltip(new Tooltip("Preview"));
        final ImageButton ibClose = new ImageButton("/resources/images/bmf/close-32.png");
            ibClose.setTooltip(new Tooltip("Close"));
            ibClose.setOnAction(e->{
                
            });
        
        menuIcons.getChildren().addAll(ibOpenCR, ibOpenBMF, ibSaveBMF, new Separator(Orientation.VERTICAL), 
                ibLayer, ibExcludeLayer, ibCekTrapLayer, ibSaveLayer, ibDeleteLayer, new Separator(Orientation.VERTICAL), 
                ibLetter, ibNumber, ibSeqField, new Separator(Orientation.VERTICAL), ibPreview, ibClose);
        
        vbox.getChildren().addAll(menuBar, menuIcons);
        
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);
        
        topbar.getChildren().add(vbox);
        
        return topbar;
    }
        
    private void setContent(){
        VBox detailContent = new VBox();
            detailContent.getChildren().addAll(textAreaTrap, textAreaLayer);
        content = new MasterDetailPane(Side.TOP); 
            content.setAnimated(true);
            content.setShowDetailNode(false);
            content.setMasterNode(textAreaCR);
            content.setDetailNode(detailContent);
           
        TableColumn idColumn = new TableColumn("ID");
            idColumn.setCellValueFactory(new PropertyValueFactory<Layer, Integer>("IDLayer"));
        TableColumn nameColumn = new TableColumn("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<Layer, String>("layerName"));
        TableColumn sumRowColumn = new TableColumn("Sum Rows");
            sumRowColumn.setCellValueFactory(new PropertyValueFactory<Layer, Integer>("countOfLines"));
        TableColumn trapColumn = new TableColumn("Trap");
            trapColumn.setCellValueFactory(new PropertyValueFactory<Layer, String>("trap"));
        TableColumn oriTrapColumn = new TableColumn("Ori Trap");     
            oriTrapColumn.setCellValueFactory(new PropertyValueFactory<Layer, String>("trapOri"));
        layerTable = new TableView<Layer>();
            layerTable.setEditable(false);
            layerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            layerTable.getColumns().addAll(idColumn, nameColumn, sumRowColumn, trapColumn, oriTrapColumn);
            layerTable.getColumns().forEach(this::addTooltipToColumnCells);
            ContextMenu cm = new ContextMenu();
                MenuItem mi1 = new MenuItem("Delete");
                mi1.setOnAction(e->{ deleteSelectedLayer(); });
            cm.getItems().add(mi1);
            layerTable.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t) -> {
                if(t.getButton() == MouseButton.SECONDARY) cm.show(layerTable , t.getScreenX() , t.getScreenY());
            });
        Tab layerTab = new Tab("Layer"); layerTab.setContent(layerTable);
        Tab fieldTab = new Tab("Field");
        createFieldGrid();  // just init gridField
        tabPaneProperties = new TabPane();
            tabPaneProperties.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
            tabPaneProperties.getTabs().addAll(layerTab, fieldTab);
            tabPaneProperties.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        propertyLabel = new Label("Properties");
            propertyLabel.setStyle("-fx-font-size : 15px; -fx-font-color : white; -fx-background-color : #08092b;");
        VBox detailRootContent = new VBox();
            detailRootContent.getChildren().addAll(propertyLabel, tabPaneProperties);
        rootContent = new MasterDetailPane(Side.RIGHT);
            rootContent.setAnimated(true);
            rootContent.setShowDetailNode(false);
            rootContent.setMasterNode(content);
            rootContent.setDetailNode(detailRootContent);
        
    }
    
    private void initTextAreaTrap(){
        textAreaTrap = new InlineStyleTextArea<StyleInfo>(
                    StyleInfo.EMPTY.updateTextColor(Color.BLACK),
                    style -> style.toCss());
        textAreaTrap.getStyleClass().add("text-area");
        textAreaTrap.setPrefHeight(70); textAreaTrap.setMaxHeight(70);
        textAreaTrap.setStyle("-fx-display-caret: false;"); 
        textAreaTrap.setOnKeyReleased((KeyEvent event) -> {
            statusbar.setText("[Trap] Digit Position - "+textAreaTrap.getCaretPosition()+" from "+textAreaTrap.getLength());
            if(event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.LEFT)){
                textAreaTrap.selectRange(textAreaTrap.getCaretPosition(), textAreaTrap.getCaretPosition()-1);
            }
            else if(event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.RIGHT)){
                textAreaTrap.selectRange(textAreaTrap.getCaretPosition()+1, textAreaTrap.getCaretPosition());
            }
            else{          
                textAreaTrap.selectRange(textAreaTrap.getCaretPosition()+1, textAreaTrap.getCaretPosition());
                refreshValueOnLayerTable();
            }
        });
        textAreaTrap.setOnMouseClicked((MouseEvent event) -> {
            statusbar.setText("[Trap] Digit Position - "+textAreaTrap.getCaretPosition()+" from "+textAreaTrap.getLength());
            textAreaTrap.selectRange(textAreaTrap.getCaretPosition()+1, textAreaTrap.getCaretPosition());
        });
        
    }
    
    private void initTextAreaSelectedLayer(){
        textAreaLayer = new InlineStyleTextArea<StyleInfo>(
                    StyleInfo.EMPTY.updateTextColor(Color.BLACK),
                    style -> style.toCss());
        textAreaLayer.getStyleClass().add("text-area");
        textAreaLayer.setEditable(false);
        // context menu
        final ContextMenu cm = new ContextMenu();
            final MenuItem item1 = new MenuItem("Create Field");
            item1.setOnAction(e->createField());
            final MenuItem item2 = new MenuItem("Edit Field");
            item2.setOnAction(e->editField(textAreaLayer.getCaretPosition()));
            final MenuItem item3 = new MenuItem("Delete Field");
            item3.setOnAction(e->deleteField(textAreaLayer.getCaretPosition()));
        cm.getItems().addAll(item1, item2, item3);
        textAreaLayer.setContextMenu(cm);
        
        textAreaLayer.setOnKeyReleased((KeyEvent event) -> {
            statusbar.setText("[Layer] Digit Position - "+textAreaLayer.getCaretPosition()+" from "+textAreaLayer.getLength());
        });
        textAreaLayer.setOnMouseClicked((MouseEvent event) -> {
            statusbar.setText("[Layer] Digit Position - "+textAreaLayer.getCaretPosition()+" from "+textAreaLayer.getLength());
        });
    }
    
    private void initTextAreaCR(){
        textAreaCR = new InlineStyleTextArea<StyleInfo>(
                    StyleInfo.EMPTY.updateTextColor(Color.BLACK),
                    style -> style.toCss());
        textAreaCR.setEditable(false);
        textAreaCR.getStyleClass().add("text-area");
        textAreaCR.setOnKeyReleased((KeyEvent event) -> {
            statusbar.setText("[CR] Digit Position - "+textAreaCR.getCaretPosition()+" from "+textAreaCR.getLength());
        });
        textAreaCR.setOnMouseClicked((MouseEvent event) -> {
            statusbar.setText("[CR] Digit Position - "+textAreaCR.getCaretPosition()+" from "+textAreaCR.getLength());
        });
    }
        
    private void openCR(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CR File (.000)", "*.000");
        fileChooser.getExtensionFilters().add(extFilter);
        
        File file = fileChooser.showOpenDialog(bmfStage);
        
        if(!file.isFile()){ // gak jalan
            return; 
        }
        
        CriticalReport cr = new CriticalReport(file);
        
        bmfStage.setTitle("BMF - "+ cr.getReportName());
        textAreaCR.replaceText(cr.getText().toString());
        
        MF = new MaskingFile(cr.getReportName());
    }
    
    private void createLayerStandar(){
        rootContent.setShowDetailNode(true);
        content.setShowDetailNode(true);
                
        String selectedText =  textAreaCR.getSelectedText();
        textAreaLayer.setDisable(false);
        textAreaLayer.replaceText(selectedText);
        
        textAreaTrap.setDisable(false);
        String[] firstLineSelectedText = selectedText.split("\n");            
        textAreaTrap.replaceText(setTrap(firstLineSelectedText[0].length()).toString());
        
        Layer LY = new Layer(MF.getIteratorIDLayer(), "Layer_"+MF.getLayers().size(), CriticalReport.getLinesFromString(selectedText), "", "");
        MF.addLayer(LY);
        
        layerTable.getItems().add(LY);
        layerTable.getSelectionModel().selectLast();
        
        tabPaneProperties.getSelectionModel().selectFirst();
    }
    
    private StringBuilder setTrap(int l){
        StringBuilder sb = new StringBuilder();
        
        for(int i=0; i<l; i++){
            sb.append("`");
        }
        return sb;
    }
    
    private void createFieldGrid(){
        /*
        *seqFieldLabel
        *seqFieldLabel 
        *dataType
        *rowFieldLabel
        *pos1Label
        *pos2Label
        *originalPosFieldLabel
        *isContent
        *dateFormat
        *idFieldLabel
        *idLayerLabel
        */
        
        seqFieldLabel = new Label("Sequence of Field");    
        nameFieldTF = TextFields.createClearableTextField();
        dataType = new ComboBox<String>(); dataType.getItems().addAll(Database.DATATYPE);
            validationSupport.registerValidator(dataType, Validator.createEmptyValidator( "Selection required"));
        rowFieldlabel = new Label("Sum of Rows");
        pos1Label = new Label("Pos1");
        pos2Label = new Label("Pos2");
        originalPosFieldLabel = new Label("Original Position");
        isContent = new ComboBox<String>(); isContent.getItems().addAll("Yes", "No");
            validationSupport.registerValidator(isContent, Validator.createEmptyValidator( "Selection required"));
        dateFormat = new ComboBox<String>(); dateFormat.getItems().addAll(Database.DATEFORMAT);
            validationSupport.registerValidator(dateFormat, Validator.createEmptyValidator( "Selection required"));
        idFieldLabel = new Label("ID Field");
        idLayerlabel = new Label("ID Layer");
        
        fieldGrid = new GridPane();
            fieldGrid.setHgap(10); fieldGrid.setVgap(10);
            fieldGrid.setPadding(new Insets(5, 2, 5, 2));
            fieldGrid.add(new Label("Field Sequence"), 0, 0);fieldGrid.add(seqFieldLabel, 1, 0);
            fieldGrid.add(new Label("Name"), 0, 1);fieldGrid.add(nameFieldTF, 1, 1);
            fieldGrid.add(new Label("Type Data"), 0, 2);fieldGrid.add(dataType, 1, 2);
            fieldGrid.add(new Label("Row"), 0, 3);fieldGrid.add(rowFieldlabel, 1, 3);
            fieldGrid.add(new Label("POS 1"), 0, 4);fieldGrid.add(pos1Label, 1, 4);
            fieldGrid.add(new Label("POS 2"), 0, 5);fieldGrid.add(pos2Label, 1, 5);
            fieldGrid.add(new Label("Orig Pos"), 0, 6);fieldGrid.add(originalPosFieldLabel, 1, 6);
            fieldGrid.add(new Label("is Content ?"), 0, 7);fieldGrid.add(isContent, 1, 7);
            fieldGrid.add(new Label("Date Format"), 0, 8);fieldGrid.add(dateFormat, 1, 8);
            fieldGrid.add(new Label("ID Field"), 0, 9);fieldGrid.add(idFieldLabel, 1, 9);
            fieldGrid.add(new Label("ID Layer"), 0, 10);fieldGrid.add(idLayerlabel, 1, 10);     
    }
    
    private void setToFieldTab(Field field){
        
        seqFieldLabel.setText(String.valueOf(field.getFieldOrdinal()));
        nameFieldTF.setText(field.getName());
        dataType.setValue(field.getType());
        rowFieldlabel.setText(String.valueOf(field.getRowPosition()));
        pos1Label.setText(String.valueOf(field.getPos1()));
        pos2Label.setText(String.valueOf(field.getPos2()));
        originalPosFieldLabel.setText(String.valueOf(field.getOriginalPos()));
        if(field.getContentStatus()) isContent.setValue("Yes");
            else isContent.setValue("No");        
        dateFormat.setValue(field.getDateFormat());
        idFieldLabel.setText(String.valueOf(field.getIDField()));
        idLayerlabel.setText(String.valueOf(field.getIDLayer()));
        
        tabPaneProperties.getSelectionModel().selectLast();
        tabPaneProperties.getSelectionModel().getSelectedItem().setContent(fieldGrid);
        
        
    }
    
    private void closeFieldTab(){
        seqFieldLabel.setText(null);
        nameFieldTF.setText(null);
        dataType.setValue(null);
        rowFieldlabel.setText(null);
        pos1Label.setText(null);
        pos2Label.setText(null);
        originalPosFieldLabel.setText(null);
        isContent.setValue(null);       
        dateFormat.setValue(null);
        idFieldLabel.setText(null);
        idLayerlabel.setText(null);
        
        tabPaneProperties.getSelectionModel().getSelectedItem().setContent(null);
    }
    
    private String stringToTrap(String trapOri){
        String result = trapOri;
        
        int i = result.length()-1;
        while (i >= 0 && result.charAt(i) == '`') {
            i--;
        }
        result = result.substring(0,i+1);
        if(result.length() == 0) return "^.";
        
        String trap = "^";
        for(int j=0; j<result.length(); j++){
            if(result.charAt(j) == '`'){
                trap+=".";
            }
            else if(result.charAt(j) == ' '){
                trap+="\\s";
            }
            else if(result.charAt(j) == Layer.LETTER_TRAP_ORI){
                trap+="[a-zA-Z]";
            }
            else if(result.charAt(j) == Layer.NUMBER_TRAP_ORI){
                trap+="\\d";
            }
            else{
                trap+=result.charAt(j);
            }
        }
        
        return trap;
    }
    
    private <T> void addTooltipToColumnCells(TableColumn<Layer, T> column){
        Callback<TableColumn<Layer, T>, TableCell<Layer, T>> existingCellFactory = column.getCellFactory();
        
        column.setCellFactory(c->{
            TableCell<Layer, T> cell = existingCellFactory.call(c);
            Tooltip tt = new Tooltip();
            tt.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tt);
            return cell;
        });
    }
    
    private void refreshValueOnLayerTable(){
        // change value cell of trap
        layerTable.getSelectionModel().getSelectedItem().setTrap(stringToTrap(textAreaTrap.getText())); // regex
        layerTable.getSelectionModel().getSelectedItem().setTrapOri(textAreaTrap.getText());
    }
    
    private void macthTrapWithCR(){
        clearBackgroundColorLayer(textAreaCR);
        
        String[] lines = textAreaCR.getText().split("\\n");
        int lengthRow = lines[0].length();
        for(int i=0; i<lines.length; i++){
            for(Layer layer : layerTable.getItems()){
                Pattern pattern = Pattern.compile(layer.getTrap()); 
                Matcher matcher = pattern.matcher(lines[i]);
                if(matcher.find()){
                    // block lines                    
                    int startLine = i*lengthRow+i; // this is the rules to get start position each lines
                    int endLine = startLine + (lengthRow*layer.getCountOfLines())+layer.getCountOfLines()-1; // this is the rules to get end position
                    
                    IndexRange selection = new IndexRange(startLine, endLine);
                    setBackgroundColorLayer(textAreaCR, selection, colorMark);
                    
                    i += layer.getCountOfLines()-1;
                    break;
                }
            }
        }
    }
    
    private void clearBackgroundColorLayer(InlineStyleTextArea<StyleInfo> area){
        IndexRange selection = new IndexRange(0, area.getLength());
        StyleSpans<StyleInfo> styles = area.getStyleSpans(selection);
        StyleSpans<StyleInfo> newStyles = styles.mapStyles(style -> style.updateWith(StyleInfo.backgroundColor(Color.WHITESMOKE)));
        area.setStyleSpans(selection.getStart(), newStyles);
    }
    
    private void setBackgroundColorLayer(InlineStyleTextArea<StyleInfo> area, IndexRange selection, Color color) {
        if(selection.getLength() != 0) {
            StyleSpans<StyleInfo> styles = area.getStyleSpans(selection);
            StyleSpans<StyleInfo> newStyles = styles.mapStyles(style -> style.updateWith(StyleInfo.backgroundColor(color)));
            area.setStyleSpans(selection.getStart(), newStyles);
        }
    }
    
    private void createField(){
        int idLayer = layerTable.getSelectionModel().getSelectedItem().getIDLayer();
        int idField = MF.getIteratorIDField();
        String nm = "Field_"+MF.getIteratorIDField();
        int seqField = MF.getFields().size(); 
            String[] layers = textAreaLayer.getText().split("\\n");
            int lengthRow = layers[0].length()+1;
        int rowPosition = (int) Math.ceil(textAreaLayer.getSelection().getStart() / lengthRow);
        int pos1 = textAreaLayer.getSelection().getStart() - (lengthRow*rowPosition); //start from 0
        int pos2 = textAreaLayer.getSelection().getEnd() - (lengthRow*rowPosition); //start from 0
        int orgPos = textAreaLayer.getSelection().getStart();
        
        Field field = new Field(idField, nm, seqField, rowPosition, pos1, pos2, orgPos, false, idLayer);
        MF.addField(field);
        
        setToFieldTab(field);                
        highlightTextAreaLayer(idLayer, idField);
    }
    
    private void editField(int caretPosition){
        int idLayer = layerTable.getSelectionModel().getSelectedItem().getIDLayer();
        Field field = MF.getField(idLayer, caretPosition);
        
        setToFieldTab(field);        
        highlightTextAreaLayer(idLayer, field.getIDField());
    }
    
    private void deleteField(int caretPosition){
        int idLayer = layerTable.getSelectionModel().getSelectedItem().getIDLayer();
        Field field = MF.getField(idLayer, caretPosition);
        MF.deleteField(field.getIDField());
        highlightTextAreaLayer(idLayer, null);
        closeFieldTab();
    }
    
    private void deleteSelectedLayer(){
        if(layerTable.getItems().isEmpty()){
            notificationBuilder
                    .title("Layer")
                    .text("Layer is Empty. Cannot to delete Anything in Layer.")                            
                    .owner(bmfStage)
                    .showWarning();
            return;
        }
        else if(layerTable.getSelectionModel().isEmpty()){
            notificationBuilder
                    .title("Layer")
                    .text("Select Layer First before Delete it")                            
                    .owner(bmfStage)
                    .showWarning();
            return;
        }

        //MF.deleteLayer(idLayer);
        //MF.deleteField(idField);
        layerTable.getItems().removeAll(layerTable.getSelectionModel().getSelectedItem());



        if(layerTable.getItems().isEmpty()){ // check again if empty
            content.setShowDetailNode(false);
            rootContent.setShowDetailNode(false);
            return;
        }
    }
    
    private void highlightTextAreaLayer(int IDLayer, Integer IDField){ // idField that active
        clearBackgroundColorLayer(textAreaLayer);
        if(IDField == null){
            for(Field f : MF.getFields()){
                if(f.getIDLayer() == IDLayer){ //mark
                    IndexRange selection = new IndexRange(f.getOriginalPos(), f.getOriginalPos()+(f.getPos2()-f.getPos1()));
                    setBackgroundColorLayer(textAreaLayer, selection, colorMark);
                }
            }
        }
        else{
            for(Field f : MF.getFields()){
                if(f.getIDLayer() == IDLayer && f.getIDField() == IDField){ // aktif
                    IndexRange selection = new IndexRange(f.getOriginalPos(), f.getOriginalPos()+(f.getPos2()-f.getPos1()));
                    setBackgroundColorLayer(textAreaLayer, selection, colorAktif);
                }
                else if(f.getIDLayer() == IDLayer && f.getIDField() != IDField){ //mark
                    IndexRange selection = new IndexRange(f.getOriginalPos(), f.getOriginalPos()+(f.getPos2()-f.getPos1()));
                    setBackgroundColorLayer(textAreaLayer, selection, colorMark);
                }
            }
        }
    }
    
}
