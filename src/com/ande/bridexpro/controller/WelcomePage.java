/*
 * Copyright (c) 2017, Bridex Pro
 * All rights reserved  * 
 */

package com.ande.bridexpro.controller;

import javafx.scene.Node;

public class WelcomePage {
    private String title;
    private Node content;
    
    public WelcomePage(String title, Node content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
    }
}
