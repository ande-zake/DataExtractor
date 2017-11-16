/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.controller;

import com.ande.bridexpro.model.Field;
import com.ande.bridexpro.model.Layer;
import com.ande.bridexpro.model.MaskingFile;
import com.ande.bridexpro.util.ImageButton;
import java.util.ArrayList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;

/**
 *
 * @author Administrator
 */
public class FieldSequence {
    private Stage fsStage;
    private MaskingFile MF;
    
    private StatusBar statusbar;
    private TableView<Field> fieldTable;
    
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    
    public FieldSequence(MaskingFile MF){
        this.MF = MF;
    }
    
    public void setStage(Stage stage){
        this.fsStage = stage;
    }
    
    public Node getPanel(){
        statusbar = new StatusBar();
            statusbar.setText("Use Drag and Drop to Re-Order Field, also use Right Click to Delete");
            statusbar.setStyle("-fx-font : 12px 'Arial'; -fx-padding : 2 10 2 10;  -fx-pref-height : 12");        
        
        createFieldTable();
        
        BorderPane root = new BorderPane();
            root.setBottom(statusbar);
            root.setCenter(fieldTable);
        
        return root;
    }
    
    private void reorderTable(){
        //Just re-order field sequence
        int iterateI = 0;
        for(Field f : fieldTable.getItems()){
            f.setFieldOrdinal(iterateI);
            iterateI++;
        }
    }
    
    private void saveMF(){
        MF.getFields().clear();
        fieldTable.getItems().stream().forEach(fd -> {MF.addField(fd);});
    }
    
    private void createFieldTable(){
        TableColumn IDFieldCol = new TableColumn("ID Field");
            IDFieldCol.setCellValueFactory(new PropertyValueFactory<Field, Integer>("IDField"));
        TableColumn IDLayerCol = new TableColumn("ID Layer");
            IDLayerCol.setCellValueFactory(new PropertyValueFactory<Field, Integer>("IDLayer"));
        TableColumn nameCol = new TableColumn("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<Field, String>("name"));
        TableColumn fieldOrdinalCol = new TableColumn("Field Sequence");
            fieldOrdinalCol.setCellValueFactory(new PropertyValueFactory<Field, Integer>("fieldOrdinal"));
        TableColumn typeCol = new TableColumn("Type Data");
            typeCol.setCellValueFactory(new PropertyValueFactory<Field, String>("type"));
        TableColumn rowPositionCol = new TableColumn("Row Position");
            rowPositionCol.setCellValueFactory(new PropertyValueFactory<Field, Integer>("rowPosition"));
        TableColumn pos1Col = new TableColumn("Pos1");
            pos1Col.setCellValueFactory(new PropertyValueFactory<Field, Integer>("pos1"));
        TableColumn pos2Col = new TableColumn("Pos2");
            pos2Col.setCellValueFactory(new PropertyValueFactory<Field, Integer>("pos2"));
        TableColumn originalPosCol = new TableColumn("Org Pos");
            originalPosCol.setCellValueFactory(new PropertyValueFactory<Field, Integer>("originalPos"));
        TableColumn contentCol = new TableColumn("is Content ?");
            contentCol.setCellValueFactory(new PropertyValueFactory<Field, Boolean>("content"));
        TableColumn dateFormatCol = new TableColumn("Date Format");
            dateFormatCol.setCellValueFactory(new PropertyValueFactory<Field, String>("dateFormat"));
        
        fieldTable = new TableView<>();
        fieldTable.setEditable(false);
        //fieldTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        fieldTable.getColumns().addAll(IDFieldCol, IDLayerCol, nameCol, fieldOrdinalCol, 
                typeCol, rowPositionCol, pos1Col, pos2Col, originalPosCol, contentCol, dateFormatCol);
        ContextMenu cm = new ContextMenu();
                MenuItem mi1 = new MenuItem("Delete");
                mi1.setOnAction(e->{ 
                    fieldTable.getItems().removeAll(fieldTable.getSelectionModel().getSelectedItems());
                    saveMF();
                });
            cm.getItems().add(mi1);
            fieldTable.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t) -> {
                if(t.getButton() == MouseButton.SECONDARY) cm.show(fieldTable , t.getScreenX() , t.getScreenY());
            });
        
        for(Field f : MF.getFields()){
            fieldTable.getItems().add(f);
        }
        
        fieldTable.setRowFactory(tv ->{
            TableRow<Field> row = new TableRow<>();
            
            row.setOnDragDetected(e->{
                if(row.isEmpty()){
                    return;
                }
                
                Integer index = row.getIndex();
                Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                ClipboardContent cc = new ClipboardContent();
                cc.put(SERIALIZED_MIME_TYPE, index);
                db.setContent(cc);
                e.consume();
            });

            row.setOnDragOver(e->{
                Dragboard db = e.getDragboard();
                if(db.hasContent(SERIALIZED_MIME_TYPE)){
                    if(row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue() ){
                        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        e.consume();
                    }
                }
            }); 

            row.setOnDragDropped(e->{
                Dragboard db = e.getDragboard();
                if(db.hasContent(SERIALIZED_MIME_TYPE)){
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    Field draggedField = fieldTable.getItems().remove(draggedIndex);
                    
                    int dropIndex;
                    if(row.isEmpty()){
                        dropIndex = fieldTable.getItems().size();
                    }
                    else{
                        dropIndex = row.getIndex();
                    }
                    fieldTable.getItems().add(dropIndex, draggedField);
                    e.setDropCompleted(true);
                    reorderTable();
                    saveMF();
                    fieldTable.getSelectionModel().select(dropIndex);
                    e.consume();
                }
            });
            
            return row;
        });
    }
}
