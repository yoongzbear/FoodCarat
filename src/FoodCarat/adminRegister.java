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


    admin.customizeForm(role, customerComponents, vendorComponents, runnerComponents);
}

private void clearFields() {
    admin.clearFields(
        emailtxt, nametxt, agetxt, phonetxt, passwordtxt, platnumtxt, addresstxta, shoptxt
    );
    admin.clearComboBoxes(gendercbx);
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
                    textField.setForeground(Color.BLACK); // Normal color
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(new Color(204, 204, 204)); // Gray color
                }
            }
        });
    }

    public static void addPlaceholder(JPasswordField passwordField, String placeholder) {
        passwordField.setEchoChar((char) 0); // Disable hiding characters
        passwordField.setText(placeholder);
        passwordField.setForeground(new Color(204, 204, 204)); // Gray color

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK); // Normal color
                    passwordField.setEchoChar('•'); // Enable hiding characters
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char) 0); // Disable hiding characters
                    passwordField.setText(placeholder);
                    passwordField.setForeground(new Color(204, 204, 204)); // Gray color
                }
            }
        });
    }
}

private void addPlaceholders() {
PlaceholderManager.addPlaceholder(nametxt, "Enter your full name");
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        emailtxt = new javax.swing.JTextField();
        passwordtxt = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        nametxt = new javax.swing.JTextField();
        gendercbx = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        agetxt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        phonetxt = new javax.swing.JTextField();
        Lshop = new javax.swing.JLabel();
        shoptxt = new javax.swing.JTextField();
        LotherInfo = new javax.swing.JLabel();
        Lplatnum = new javax.swing.JLabel();
        platnumtxt = new javax.swing.JTextField();
        cartypecbx = new javax.swing.JComboBox<>();
        Lcartype = new javax.swing.JLabel();
        Laddress = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        addresstxta = new javax.swing.JTextArea();
        bRegister = new javax.swing.JButton();
        bClear = new javax.swing.JButton();
        bBack = new javax.swing.JButton();
        Lrole = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Registration -");

        jLabel3.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        jLabel3.setText("Gender:");

        jLabel4.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        jLabel4.setText("Email:");

        jLabel5.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        jLabel5.setText("Password:");

        emailtxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        emailtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailtxtActionPerformed(evt);
            }
        });

        passwordtxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        passwordtxt.setText("jPasswordField1");
        passwordtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordtxtActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        jLabel2.setText("Name: ");

        nametxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        nametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nametxtActionPerformed(evt);
            }
        });

        gendercbx.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        gendercbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        jLabel6.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        jLabel6.setText("Age:");

        agetxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        agetxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agetxtActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        jLabel7.setText("Phone Number:");

        phonetxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N

        Lshop.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lshop.setText("Shop name: ");

        LotherInfo.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        LotherInfo.setText("Other information");

        Lplatnum.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lplatnum.setText("Plate Number:");

        platnumtxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        platnumtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                platnumtxtActionPerformed(evt);
            }
        });

        cartypecbx.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        cartypecbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Car", "Motor" }));
        cartypecbx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cartypecbxActionPerformed(evt);
            }
        });

        Lcartype.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lcartype.setText("Vehicle Type:");

        Laddress.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Laddress.setText("Address: ");

        addresstxta.setColumns(20);
        addresstxta.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        addresstxta.setRows(5);
        jScrollPane1.setViewportView(addresstxta);

        bRegister.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bRegister.setText("Register");
        bRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegisterActionPerformed(evt);
            }
        });

        bClear.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bClear.setText("Clear");
        bClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bClearActionPerformed(evt);
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
                .addGap(58, 58, 58)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(Lshop)
                    .addComponent(Lplatnum)
                    .addComponent(Laddress))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(platnumtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Lcartype)
                        .addGap(108, 108, 108)
                        .addComponent(cartypecbx, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(182, 182, 182))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(gendercbx, 0, 275, Short.MAX_VALUE)
                                    .addComponent(emailtxt)
                                    .addComponent(nametxt)
                                    .addComponent(shoptxt))
                                .addGap(114, 114, 114)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(34, 34, 34)
                                        .addComponent(phonetxt))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel6))
                                        .addGap(83, 83, 83)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(agetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(passwordtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addContainerGap(92, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(LotherInfo)
                        .addGap(364, 364, 364))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(bRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(bClear, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(bBack, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(301, 301, 301))))
            .addGroup(layout.createSequentialGroup()
                .addGap(360, 360, 360)
                .addComponent(jLabel1)
                .addGap(59, 59, 59)
                .addComponent(Lrole)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Lrole))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(agetxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(phonetxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(passwordtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(emailtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6)))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(nametxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(gendercbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lshop)
                    .addComponent(shoptxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addComponent(LotherInfo)
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lplatnum)
                    .addComponent(platnumtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cartypecbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Lcartype))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Laddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(bClear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bBack, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bRegister))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void emailtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailtxtActionPerformed

    private void nametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nametxtActionPerformed

    private void agetxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agetxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_agetxtActionPerformed

    private void passwordtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordtxtActionPerformed

    private void platnumtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_platnumtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_platnumtxtActionPerformed

    private void cartypecbxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cartypecbxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cartypecbxActionPerformed

    private void bRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegisterActionPerformed
   // Check if the email is already registered
    if (isEmailRegistered(emailtxt.getText().trim())) {
        JOptionPane.showMessageDialog(null, "This email is already registered.");
        emailtxt.requestFocus();
        admin.clearFields(emailtxt);
        return;
    }
    if (!admin.validateInputs(
            role, emailtxt, nametxt, agetxt, phonetxt, platnumtxt, shoptxt, addresstxta)) {
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

            // Read the file to find the maximum number
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
            
            FileWriter fw = new FileWriter(filename,true); //true- able to add the data as append instead of replace
            StringBuilder info = new StringBuilder();
            info.append(emailtxt.getText()).append(";")
                .append(nametxt.getText()).append(";")
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

    private void bClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bClearActionPerformed
        clearFields();
    }//GEN-LAST:event_bClearActionPerformed

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
    private javax.swing.JLabel Laddress;
    private javax.swing.JLabel Lcartype;
    private javax.swing.JLabel LotherInfo;
    private javax.swing.JLabel Lplatnum;
    private javax.swing.JLabel Lrole;
    private javax.swing.JLabel Lshop;
    private javax.swing.JTextArea addresstxta;
    private javax.swing.JTextField agetxt;
    private javax.swing.JButton bBack;
    private javax.swing.JButton bClear;
    private javax.swing.JButton bRegister;
    private javax.swing.JComboBox<String> cartypecbx;
    private javax.swing.JTextField emailtxt;
    private javax.swing.JComboBox<String> gendercbx;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nametxt;
    private javax.swing.JPasswordField passwordtxt;
    private javax.swing.JTextField phonetxt;
    private javax.swing.JTextField platnumtxt;
    private javax.swing.JTextField shoptxt;
    // End of variables declaration//GEN-END:variables
}
