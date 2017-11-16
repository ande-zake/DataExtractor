/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ande.bridexpro.util;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import static javax.swing.text.JTextComponent.addKeymap;
import static javax.swing.text.JTextComponent.loadKeymap;
import static javax.swing.text.JTextComponent.removeKeymap;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;

/**
 *
 * @author Administrator
 */
public class OverwritableTextArea extends JTextArea {
    public OverwritableTextArea() {
    this(null, null, 0, 0);
  }

  public OverwritableTextArea(Document doc){
      this(doc, null, 0, 0);
  }  
    
  public OverwritableTextArea(String text) {
    this(null, text, 0, 0);
  }

  public OverwritableTextArea(int rows, int columns) {
    this(null, null, rows, columns);
  }

  public OverwritableTextArea(String text, int rows, int columns) {
    this(null, text, rows, columns);
  }

  public OverwritableTextArea(Document doc, String text, int rows, int columns) {
    super(doc, text, rows, columns);
    overwriteCaret = new OverwriteCaret();
    super.setCaret(overwriting ? overwriteCaret : insertCaret);
  }

  @Override
  public void setKeymap(Keymap map) {
    if (map == null) {
      super.setKeymap(null);
      sharedKeymap = null;
      return;
    }

    if (getKeymap() == null) {
      if (sharedKeymap == null) {
        // Switch keymaps. Add extra bindings.
        removeKeymap(keymapName);
        sharedKeymap = addKeymap(keymapName, map);
        loadKeymap(sharedKeymap, bindings, defaultActions);
      }
      map = sharedKeymap;
    }
    super.setKeymap(map);
  }

  @Override
  public void replaceSelection(String content) {
    Document doc = getDocument();
    if (doc != null) {
      // If we are not overwriting, just do the
      // usual insert. Also, if there is a selection,
      // just overwrite that (and that only).
      if (overwriting == true && getSelectionStart() == getSelectionEnd()) {

        // Overwrite and no selection. Remove
        // the stretch that we will overwrite,
        // then use the usual code to insert the
        // new text.
        int insertPosition = getCaretPosition();
        int overwriteLength = doc.getLength() - insertPosition;
        int length = content.length();

        if (overwriteLength > length) {
          overwriteLength = length;
        }

        // Remove the range being overwritten
        try {
          doc.remove(insertPosition, overwriteLength);
        } catch (BadLocationException e) {
          // Won't happen
        }
      }
    }

    super.replaceSelection(content);
  }

  // Change the global overwriting mode
  public static void setOverwriting(boolean overwriting) {
    OverwritableTextArea.overwriting = overwriting;
  }

  public static boolean isOverwriting() {
    return overwriting;
  }

  // Configuration of the insert caret
  @Override
  public void setCaret(Caret caret) {
    insertCaret = caret;
  }

  // Allow configuration of a new
  // overwrite caret.
  public void setOverwriteCaret(Caret caret) {
    overwriteCaret = caret;
  }

  public Caret getOverwriteCaret() {
    return overwriteCaret;
  }

  // Caret switching
  @Override
  public void processFocusEvent(FocusEvent evt) {
    if (evt.getID() == FocusEvent.FOCUS_GAINED) {
      selectCaret();
    }
    super.processFocusEvent(evt);
  }

  protected void selectCaret() {
    // Select the appropriate caret for the
    // current overwrite mode.
    Caret newCaret = overwriting ? overwriteCaret : insertCaret;

    if (newCaret != getCaret()) {
      Caret caret = getCaret();
      int mark = caret.getMark();
      int dot = caret.getDot();
      caret.setVisible(false);

      super.setCaret(newCaret);

      newCaret.setDot(mark);
      newCaret.moveDot(dot);
      newCaret.setVisible(true);
    }
  }

  protected Caret overwriteCaret;

  protected Caret insertCaret;

  protected static boolean overwriting = true;

  public static final String toggleOverwriteAction = "toggle-overwrite";

  protected static Keymap sharedKeymap;

  protected static final String keymapName = "OverwriteMap";

  protected static final Action[] defaultActions = { new ToggleOverwriteAction() };

  protected static JTextComponent.KeyBinding[] bindings = { new JTextComponent.KeyBinding(
      KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0),
      toggleOverwriteAction) };

  // Insert/overwrite toggling action
  public static class ToggleOverwriteAction extends TextAction {
    ToggleOverwriteAction() {
      super(toggleOverwriteAction);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
      OverwritableTextArea.setOverwriting(!OverwritableTextArea
          .isOverwriting());
      JTextComponent target = getFocusedComponent();
      if (target instanceof OverwritableTextArea) {
        OverwritableTextArea field = (OverwritableTextArea) target;
        field.selectCaret();
      }
    }
  }

  public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception evt) {}
  
    JFrame f = new JFrame("Overwrite test");

    OverwritableTextArea tf = new OverwritableTextArea(1,10);
    tf.setWrapStyleWord(true);
    tf.setLineWrap(true);
    tf.setText("''''''''''");
    f.getContentPane().add(tf, "North");
    //tf = new OverwritableTextArea(5, 10);
    //tf.setText("''''''''''");
    //f.getContentPane().add(tf, "South");

    f.pack();
    f.setVisible(true);
  }
}

class OverwriteCaret extends DefaultCaret {
  @Override
  protected synchronized void damage(Rectangle r) {
    if (r != null) {
      try {
        JTextComponent comp = getComponent();
        TextUI mapper = comp.getUI();
        Rectangle r2 = mapper.modelToView(comp, getDot() + 1);
        int width = r2.x - r.x;
        if (width == 0) {
          width = MIN_WIDTH;
        }
        comp.repaint(r.x, r.y, width, r.height);

        // Swing 1.1 beta 2 compat
        this.x = r.x;
        this.y = r.y;
        this.width = width;
        this.height = r.height;
      } catch (BadLocationException e) {
      }
    }
  }

  public void paint(Graphics g) {
    if (isVisible()) {
      try {
        JTextComponent comp = getComponent();
        TextUI mapper = comp.getUI();
        Rectangle r1 = mapper.modelToView(comp, getDot());
        Rectangle r2 = mapper.modelToView(comp, getDot() + 1);
        g = g.create();
        g.setColor(comp.getForeground());
        g.setXORMode(comp.getBackground());
        int width = r2.x - r1.x;
        if (width == 0) {
          width = MIN_WIDTH;
        }
        g.fillRect(r1.x, r1.y, width, r1.height);
        g.dispose();
      } catch (BadLocationException e) {
      }
    }
  }

  protected static final int MIN_WIDTH = 8;
}

