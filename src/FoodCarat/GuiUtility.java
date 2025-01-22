/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author mastu
 */
public class GuiUtility {    
    // Set the input not > max length
    public static DocumentFilter createLengthFilter(int maxLength) {
        return new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                if (fb.getDocument().getLength() - length + text.length() <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }
    
    //set placeholder for search box
    public static void setPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);  

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //if the text field contains the placeholder, clear it when the clicked
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);  
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //if the text field is empty, show the placeholder again
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    //clear text fields, labels, and combo box
    public static void clearFields(java.awt.Component... components) {
        for (java.awt.Component component : components) {
            if (component instanceof javax.swing.text.JTextComponent) {
                ((javax.swing.text.JTextComponent) component).setText(""); 
            } else if (component instanceof javax.swing.JComboBox) {
                ((javax.swing.JComboBox<?>) component).setSelectedIndex(-1); 
            } else if (component instanceof javax.swing.JLabel) {
                ((javax.swing.JLabel) component).setText(""); 
            }
        }
    }
}
