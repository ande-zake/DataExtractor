/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved  * 
 */
package com.ande.bridexpro.model;

/**
 *
 * @author Administrator
 */
public class ReportBasic implements Report{

    private final String name;
    private final String description;
    
    public ReportBasic(String name){
        this(name, "");
    }
    
    public ReportBasic(String name, String description){
        this.name = name;
        this.description = description;
    }
    @Override
    public String getReportName() {
        return name;
    }

    @Override
    public String getReportDescription() {
        return description;
    }
    
}
