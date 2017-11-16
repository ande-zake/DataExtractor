/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved  * 
 */
package com.ande.bridexpro.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Ande
 */
public class ReportTree {
    private TreeNode root;
    private int count = 0;
    
    public ReportTree(Report rootReport){
        root = new TreeNode(null, null, rootReport);
    }
    public TreeNode getRoot() {
        return root;
    }
    
    public Object size() {
        return count;
    }
    
    public void addReport(String[] packages, Report report) {
        if (packages.length == 0) {
            root.addReport(report);
            return;
        }
        
        TreeNode n = root;
        for (String packageName : packages) {
            if (n.containsChild(packageName)) {
                n = n.getChild(packageName);
            } else {
                TreeNode newNode = new TreeNode(packageName);
                n.addNode(newNode);
                n = newNode;
            }
        }
        
        if (n.packageName.equals(packages[packages.length - 1])) {
            n.addReport(report);
            count++;
        }
    }
    
    @Override 
    public String toString() {
        return root.toString();
    }
    
    // ---- class TreeNode
    public static class TreeNode{
        private final Report report;
        private final String packageName;
        
        private final TreeNode parent;
        private List<TreeNode> childrens;
        
        public TreeNode() {
            this(null, null, null);
        }
        
        public TreeNode(String packageName) {
            this(null, packageName, null);
        }
        
        public TreeNode(TreeNode parent, String packageName, Report report) {
            this.childrens = new ArrayList<>();
            this.report = report;
            this.parent = parent;
            this.packageName = packageName;
        }
        
        public boolean containsChild(String packageName) {
            if (packageName == null) return false;
            
            for (TreeNode n : childrens) {
                if (packageName.equals(n.packageName)) {
                    return true;
                }
            }
            return false;
        }
        
        public TreeNode getChild(String packageName) {
            if (packageName == null) return null;
            
            for (TreeNode n : childrens) {
                if (packageName.equals(n.packageName)) {
                    return n;
                }
            }
            return null;
        }
        
        public void addReport(Report report) {
            childrens.add(new TreeNode(this, null, report));
        }
        
        public void addNode(TreeNode n) {
            childrens.add(n);
        }
        
        public Report getReport() {
            return report;
        }
        
        public String getPackageName() {
            return packageName;
        }
        
        public TreeItem<Report> createTreeItem() {
            TreeItem<Report> treeItem = null;
            
            if (report != null) {
                treeItem = new TreeItem<>(report);
            } else if (packageName != null) {
                treeItem = new TreeItem<>(new ReportBasic(packageName));
            }
            
            treeItem.setExpanded(true);
            
            // recursively add in children
            for (TreeNode n : childrens) {
                treeItem.getChildren().add(n.createTreeItem());
            }
            
            return treeItem;
        }
        
        @Override public String toString() {
            if (report != null) {
                return " Report [ reportName: " + report.getReportName() + ", children: " + childrens + " ]";
            } else {
                return " Report [ packageName: " + packageName + ", children: " + childrens + " ]";
            }
        }
    }
}
