/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved  * 
 */
package com.ande.bridexpro.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Administrator
 */
public class ReportTreeItem {
        
    /*
    * PACKAGES_ITEM -> String Reports, Boolean if it has been creates
    */
    private static HashMap<String, Boolean> PACKAGES_ITEM;
    private static final String PACKAGES_OTHER = "OTHERS";
    
    public static TreeItem<String> getTreeItem(ArrayList<String> reports){
        return getTreeItem(reports, null);
    }
    
    public static TreeItem<String> getTreeItem(ArrayList<String> reports, String searchText){
        TreeItem<String> rootItem = new TreeItem<>("REPORTS");        
        ArrayList<TreeItem<String>> packagesReportOnTreeView = new ArrayList<>();
        
        if(reports.isEmpty()){
            return rootItem;
        }
        
        // Initialize PACKAGES_ITEM
        PACKAGES_ITEM = new HashMap<>();
        PACKAGES_ITEM.put("LW000", Boolean.FALSE);
        PACKAGES_ITEM.put("LW111", Boolean.FALSE);
        
        for(String x : reports){
            if((searchText != null && !searchText.trim().isEmpty()) && ! x.toLowerCase().contains(searchText.toLowerCase())){
                continue;
            }
            boolean includePackages = false; // 
            TreeItem<String> rpt = new TreeItem<>(x);
            for(Map.Entry<String, Boolean> entry : PACKAGES_ITEM.entrySet()){
                // if table contain in a PACKAGES_ITEM
                if(x.toLowerCase().contains(entry.getKey().toLowerCase())){
                    if(Objects.equals(entry.getValue(), Boolean.FALSE)){ // packages has not been created
                       //create the packagesReportOnTreeView 
                        TreeItem<String> pckg = new TreeItem<>(entry.getKey().toUpperCase());
                        pckg.getChildren().add(rpt);
                        
                        packagesReportOnTreeView.add(pckg);
                        
                        // set PACKAGES_ITEM entry to TRUE
                        PACKAGES_ITEM.replace(entry.getKey(), Boolean.FALSE, Boolean.TRUE);
                    }
                    else{ // packages has been created
                        // find the package and insert report to that
                        for(TreeItem<String> eachPckg : packagesReportOnTreeView){
                            if(eachPckg.getValue().equalsIgnoreCase(entry.getKey())){
                                eachPckg.getChildren().add(rpt);
                                break;
                            }
                        }
                    }
                    includePackages = true;
                    break;
                }   
            }
            if(includePackages == false){
                boolean isTherePckgOther = false;
                // find the package OTHER
                for(TreeItem<String> eachPckgs : packagesReportOnTreeView){
                    if(eachPckgs.getValue().equalsIgnoreCase(PACKAGES_OTHER)){
                        eachPckgs.getChildren().add(rpt);
                        isTherePckgOther = true;
                        break;
                    }
                }
                if(isTherePckgOther == false){ // if packages OTHER has been created
                    // create package OTHER
                    TreeItem<String> pckg = new TreeItem<>(PACKAGES_OTHER);
                    pckg.getChildren().add(rpt);

                    packagesReportOnTreeView.add(pckg);
                }
            }
        }
        
        rootItem.getChildren().addAll(packagesReportOnTreeView);
        
        return rootItem;
    }
    
}
