/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved  * 
 */
package com.ande.bridexpro.model;

import com.ande.bridexpro.util.Database;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

/**
 *
 * @author Administrator
 * 
 * This Class is Report that takes from database
 */
public class ReportProject implements Report{

    // name get from name table in database
    private String name;
    private String description;
    
    private SpreadsheetView reportView;
        
    private ArrayList<ObservableList<SpreadsheetCell>> rows;
    private final int COUNT_ROW;
    private final int COUNT_COLUMN;
    
    public ReportProject(String tableName) throws SQLException{
        
        this(tableName, "");
    }
    
    public ReportProject(String tableName, String desc) throws SQLException{
        
        // must be first for setting rows
        ResultSet rs = Database.readTable(tableName);
        this.setRows(rs);
        
        this.name = tableName;
        this.description = desc;
        COUNT_ROW = rows.size();
        COUNT_COLUMN = rs.getMetaData().getColumnCount();
        setReportView();
    }
    
    @Override
    public String getReportName() {
        return name;
    }

    @Override
    public String getReportDescription() {
        return description;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setDescription(String desc){
        this.description = desc;
    }
    
    public SpreadsheetView getReportView(){
        return reportView;
    }
    
    public int getRowCount(){
        return COUNT_ROW;
    }
    
    public int getColumnCount(){
        return COUNT_COLUMN;
    }
    
    public static ArrayList<String> getAllReports(String dbName) throws SQLException{
        ArrayList<String> result = new ArrayList<>();
        ResultSet rs = Database.getAllTables(dbName);
        if(rs != null){
            while(rs.next()){
                result.add(rs.getString(3)); // column 3 is a table's name
            }
        }
        return result;
    }
    
    // -- private method
    private void setRows(ResultSet resultSet) throws SQLException{
        rows = new ArrayList<>();
        ResultSetMetaData rsmd = resultSet.getMetaData();
        
        // fill the column Name
        final ObservableList<SpreadsheetCell> rowHeader = FXCollections.observableArrayList();
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(0, i, 1, 1, rsmd.getColumnName(i+1));
            cell.getStyleClass().add("header_row");
            rowHeader.add(cell);
        }
        rows.add(rowHeader);
        
        int iterateRow = 1;
        while(resultSet.next()){
            // iterate row            
            final ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
            
            /*
            * Column in MYSQL start from 1
            * Column in Spreadsheet start from 0
            */
            for (int iterateColumn = 0; iterateColumn < rsmd.getColumnCount(); iterateColumn++) {
                // -- fill up data
                // if INTEGER
                if(rsmd.getColumnType(iterateColumn+1) == Types.INTEGER){
                    row.add(SpreadsheetCellType.INTEGER.createCell(iterateRow, iterateColumn, 1, 1, resultSet.getInt(iterateColumn+1)));
                }
                // if VARCHAR
                else if(rsmd.getColumnType(iterateColumn+1) == Types.VARCHAR){
                    row.add(SpreadsheetCellType.STRING.createCell(iterateRow, iterateColumn, 1, 1, resultSet.getString(iterateColumn+1)));
                }
                // if DOUBLE
                else if(rsmd.getColumnType(iterateColumn+1) == Types.DOUBLE){
                    row.add(SpreadsheetCellType.DOUBLE.createCell(iterateRow, iterateColumn, 1, 1, resultSet.getDouble(iterateColumn+1)));
                }
                // if DATE
                else{
                    row.add(SpreadsheetCellType.DATE.createCell(iterateRow, iterateColumn, 1, 1, resultSet.getDate(iterateColumn+1).toLocalDate()));
                }
            }
            rows.add(row);
            iterateRow++;
        }
    }
    
    private void setReportView(){
        GridBase grid = new GridBase(COUNT_ROW, COUNT_COLUMN);
        grid.setRows(rows);
        reportView = new SpreadsheetView(grid);
        
        reportView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        reportView.setEditable(false);
        reportView.getStylesheets().add(getClass().getResource("/styles/spreadsheet.css").toExternalForm());
        
    }
}
