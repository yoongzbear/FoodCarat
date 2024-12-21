/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class admin {
    private String adminName;
    private String adminID;
    private String password;
    
public admin(String adminName, String adminID) {
        this.adminName = adminName;
        this.adminID = adminID;
        //can use this when they wanna perform some action like top up need them to enter the password
    }

//used in registration and update user informaition
public static void customizeForm(String role, 
                                 javax.swing.JComponent[] runnerComponents, 
                                 javax.swing.JComponent[] customerComponents, 
                                 javax.swing.JComponent[] vendorComponents) {
    // Hide all components initially
    for (javax.swing.JComponent component : concatArrays(customerComponents, vendorComponents,runnerComponents)) {
        component.setVisible(false);
    }
    // Adjust visibility based on the role
    switch (role) {
        case "customer":
            for (javax.swing.JComponent component : customerComponents) {
                component.setVisible(true); 
            }
            break;
        case "vendor":
            for (javax.swing.JComponent component : vendorComponents) {
                component.setVisible(true); 
            }
            break;
        case "runner":
            for (javax.swing.JComponent component : runnerComponents) {
                component.setVisible(true); // Show runner-specific components
            }
            break;
    }
}

// Utility method to concatenate multiple arrays of components
private static javax.swing.JComponent[] concatArrays(javax.swing.JComponent[]... arrays) {
    return java.util.Arrays.stream(arrays).flatMap(java.util.Arrays::stream).toArray(javax.swing.JComponent[]::new);
}

public static void clearFields(javax.swing.text.JTextComponent... components) {
    for (javax.swing.text.JTextComponent component : components) {
        component.setText("");
    }
}

public static void clearComboBoxes(javax.swing.JComboBox... comboBoxes) {
    for (javax.swing.JComboBox comboBox : comboBoxes) {
        comboBox.setSelectedIndex(0);
    }
}

//used in registration and update user informaition
public static boolean validateInputs(
    String role, 
    javax.swing.JTextField emailtxt, 
    javax.swing.JTextField nametxt, 
    javax.swing.JTextField agetxt, 
    javax.swing.JTextField phonetxt, 
    javax.swing.JTextField platnumtxt, 
    javax.swing.JTextField shoptxt, 
    javax.swing.JTextArea addresstxta
) {

// Common validation logic
if (emailtxt.getText().trim().isEmpty() || !emailtxt.getText().contains("@")) {
    JOptionPane.showMessageDialog(null, "Please enter a valid email.");
    return false;
}
if (nametxt.getText().trim().isEmpty() || nametxt.getText().equals("Enter your full name")) {
    JOptionPane.showMessageDialog(null, "Name cannot be empty.");
    return false;
}
if (agetxt.getText().trim().isEmpty()) {
    JOptionPane.showMessageDialog(null, "Age cannot be empty.");
    return false;
}
try {
    int age = Integer.parseInt(agetxt.getText().trim());
    if (age <= 0) {
        JOptionPane.showMessageDialog(null, "Age must be a positive number.");
        return false;
    }
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(null, "Age must be a valid number.");
    return false;
}
if (phonetxt.getText().trim().isEmpty() || phonetxt.getText().equals("XXX-XXXXXXX")) {
    JOptionPane.showMessageDialog(null, "Phone number cannot be empty.");
    return false;
}

String phonePattern = "\\d{3}-\\d{7}"; // Format: XXX-XXXXXXX
if (!phonetxt.getText().matches(phonePattern)) {
    JOptionPane.showMessageDialog(null, "Invalid phone number format. Use XXX-XXXXXXX.");
    return false;
}

// Role-specific validation
switch (role) {
    case "customer":
        if (addresstxta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Address cannot be empty for customers.");
            return false;
        }
        break;
    case "vendor":
        if (shoptxt.getText().trim().isEmpty() || shoptxt.getText().equals("Enter your shop name")) {
            JOptionPane.showMessageDialog(null, "Shop name cannot be empty for vendors.");
            return false;
        }
        break;
    case "runner":
        if (platnumtxt.getText().trim().isEmpty() || platnumtxt.getText().equals("eg. 0110051 UiOVqjEe")) {
            JOptionPane.showMessageDialog(null, "Plate number cannot be empty for runners.");
            return false;
        }
        break;
}
return true; // All validations passed
}
}
