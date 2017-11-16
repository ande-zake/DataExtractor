/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved  * 
 */
package com.ande.bridexpro.model;

import com.ande.bridexpro.controller.WelcomePage;

/**
 *
 * @author Ande 
 * Project is CR that has been take from database
 */
public class Project {
    
    private final String name;
    private final String basePackage;
    
    //a project has a tree report's names
    private final ReportTree reportTree;
    
    // Root that holds the welcome tab content and title
    private WelcomePage welcomePage;
    
    public Project(String name, String basePackage){
        this.name = name;
        this.basePackage = basePackage;
        this.reportTree = new ReportTree(new ReportBasic(name));
    }
    
    public void addReport(Report report) {
        // buat algoritma menambahkan report
        // contoh :
        // GI405 -> appendGI405, GI405_kodeuker, dsb
        // LW321 -> timeseriesLW321, LW3210, LW321Grup
        // sehingga, penulisan algoritmanya manjadi
        // packages adalah string[] packages, yang berisi paket nama CR seperti package GI405, package LW321, dsb
        // report adalah nama reportnya
        String[] packages = new String[0];
        reportTree.addReport(packages, report);
    }
    
    public ReportTree getReportTree() {
        return reportTree;
    }
    
    public void setWelcomePage(WelcomePage welcomePage) {
        if(null != welcomePage) {
            this.welcomePage = welcomePage;
        }
    }
    
    public WelcomePage getWelcomePage() {
        return this.welcomePage;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Project [ name: ");
        sb.append(name);
        sb.append(", sample count: ");
        sb.append(reportTree.size());
        sb.append(", tree: ");
        sb.append(reportTree);
        sb.append(" ]");
        
        return sb.toString();
    }
}
