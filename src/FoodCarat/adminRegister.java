/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author mastu
 */
public class adminRegister extends javax.swing.JFrame {
    private String role;
    /**
     * Creates new form adminRegister
     */
    public adminRegister() {
        this("customer");  // Default to "customer" role
    }
    public adminRegister(String role) {
        this.role=role;
        initComponents();
        addPlaceholders();
        customizeForm();
        Lrole.setText(role);
    }

private void customizeForm() {
    javax.swing.JComponent[] customerComponents = {
        Laddress, addresstxta, jScrollPane1, LotherInfo
    };

    javax.swing.JComponent[] vendorComponents = {
        Lshop, shoptxt
    };

    javax.swing.JComponent[] runnerComponents = {
        Lplatnum, platnumtxt, Lcartype, cartypecbx, LotherInfo
    };


    Admin.customizeForm(role, customerComponents, vendorComponents, runnerComponents);
}

private void clearFields() {
    Admin.clearFields(emailtxt, usernametxt, agetxt, phonetxt, passwordtxt, platnumtxt, addresstxta, shoptxt
    );
    Admin.clearComboBoxes(gendercbx);
}

    
public class PlaceholderManager {
    public static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(new Color(204, 204, 204)); // Gray color

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(new Color(204, 204, 204));
                }
            }
        });
    }

    public static void addPlaceholder(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char) 0); // Disable hiding characters
        passwordField.setText(placeholder);
        passwordField.setForeground(new Color(204, 204, 204));

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•'); // Enable hiding characters
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char) 0); // Disable hiding characters
                    passwordField.setText(placeholder);
                    passwordField.setForeground(new Color(204, 204, 204));
                }
            }
        });
    }
}

private void addPlaceholders() {
PlaceholderManager.addPlaceholder(usernametxt, "Enter your full name");
PlaceholderManager.addPlaceholder(emailtxt, "Enter a valid email address");
PlaceholderManager.addPlaceholder(phonetxt, "XXX-XXXXXXX");
PlaceholderManager.addPlaceholder(shoptxt, "Enter your shop name");
PlaceholderManager.addPlaceholder(platnumtxt, "eg. 0110051 UiOVqjEe");
PlaceholderManager.addPlaceholder(passwordtxt, "At least 6 character including 1 special character"); // this use password text field
}

// Method to check if the email is already registered
private boolean isEmailRegistered(String email) {
    String filename = role + ".txt";
    File file = new File(filename);
    if (file.exists()) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String existingEmail = parts[0];
                if (existingEmail.equals(email)) {
                    return true; // Email already exists
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error checking email: " + e.getMessage());
        }
    }
    return false; // Email is not registered
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        Lemail = new javax.swing.JLabel();
        emailtxt = new javax.swing.JTextField();
        Lusername = new javax.swing.JLabel();
        usernametxt = new javax.swing.JTextField();
        bRegister = new javax.swing.JButton();
        bBack = new javax.swing.JButton();
        Lrole = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Registration -");

        Lemail.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lemail.setText("Email:");

        emailtxt.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        emailtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailtxtActionPerformed(evt);
            }
        });

        Lusername.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lusername.setText("User Name: ");

        usernametxt.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        usernametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernametxtActionPerformed(evt);
            }
        });

        bRegister.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bRegister.setText("Register");
        bRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegisterActionPerformed(evt);
            }
        });

        bBack.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bBack.setText("Back");
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        Lrole.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        Lrole.setText("xxxx");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(172, 172, 172)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(bRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(232, 232, 232)
                                .addComponent(bBack, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Lemail)
                                    .addComponent(Lusername))
                                .addGap(106, 106, 106)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(emailtxt)
                                    .addComponent(usernametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(jLabel1)
                        .addGap(59, 59, 59)
                        .addComponent(Lrole)))
                .addContainerGap(186, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Lrole))
                .addGap(77, 77, 77)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lusername)
                    .addComponent(usernametxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Lemail)
                    .addComponent(emailtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(bBack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bRegister))
                .addGap(46, 46, 46))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void emailtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailtxtActionPerformed

    private void usernametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernametxtActionPerformed

    private void bRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegisterActionPerformed
   // Check if the email is already registered
    if (isEmailRegistered(emailtxt.getText().trim())) {
        JOptionPane.showMessageDialog(null, "This email is already registered.");
        emailtxt.requestFocus();
        Admin.clearFields(emailtxt);
        return;
    }
    if (!Admin.validateInputs(role, emailtxt, usernametxt, agetxt, phonetxt, platnumtxt, shoptxt, addresstxta)) {
        return; // Exit if validation fails
    }
    String placeholder = "At least 6 character including 1 special character";
    String password = String.valueOf(passwordtxt.getPassword()).trim();
    if (password.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Password cannot be empty.");
        passwordtxt.requestFocus();
        return;
    }
    if (password.length() < 6) {
        JOptionPane.showMessageDialog(null, "Password must be at least 6 characters.");
        passwordtxt.requestFocus();
        return;
    }
    if (!password.matches("^(?=.*[!@#$%^&*]).{6,}$")) {
        JOptionPane.showMessageDialog(null, "Password must include at least one special character (!@#$%^&*).");
        passwordtxt.requestFocus();
        return;
    }

        try{
            String filename = role + ".txt";
            File file = new File(filename);

            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String lastLine = null, currentLine;
                    while ((currentLine = br.readLine()) != null) {
                        lastLine = currentLine;
                    }
                    if (lastLine != null) {
                        String[] parts = lastLine.split(";");
                    }
                }
            }
            
            FileWriter fw = new FileWriter(filename,true);
            StringBuilder info = new StringBuilder();
            info.append(emailtxt.getText()).append(";")
                .append(usernametxt.getText()).append(";")
                .append(gendercbx.getSelectedItem()).append(";")
                .append(agetxt.getText()).append(";")
                .append(phonetxt.getText()).append(";")
                .append(new String(passwordtxt.getPassword())).append(";");
            
            if("customer".equals(role)){
                info.append(addresstxta.getText()).append(";");
            }else if("vendor".equals(role)) {
                info.append(shoptxt.getText()).append(";");
            } else if ("runner".equals(role)) {
                info.append(platnumtxt.getText()).append(";")
                    .append(cartypecbx.getSelectedItem()).append(";");
            }
            
            fw.write(info.append("\n").toString());
            fw.close();
            
            JOptionPane.showMessageDialog(null, "Successfully registered as " + role + "!");
            clearFields();
            }catch(IOException e){
                JOptionPane.showMessageDialog(null, "Error:" + e.getMessage());
            }
    }//GEN-LAST:event_bRegisterActionPerformed

    private void bBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBackActionPerformed
        this.dispose();
        new adminMain().setVisible(true);
    }//GEN-LAST:event_bBackActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(adminRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(adminRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(adminRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(adminRegister.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adminRegister().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Lemail;
    private javax.swing.JLabel Lrole;
    private javax.swing.JLabel Lusername;
    private javax.swing.JButton bBack;
    private javax.swing.JButton bRegister;
    private javax.swing.JTextField emailtxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField usernametxt;
    // End of variables declaration//GEN-END:variables
}
