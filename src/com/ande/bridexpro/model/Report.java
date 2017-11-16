/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved.
 */
package com.ande.bridexpro.model;

/**
 *
 * @author Ande
 */
public interface Report {
    /**
     * A short, most likely single-word, name to show to the user - e.g. "CheckBox"
     * @return String
     */
    public String getReportName();

    /**
     * A short, multiple sentence description of the report. 
     * @return String
     */
    public String getReportDescription();
               
}
