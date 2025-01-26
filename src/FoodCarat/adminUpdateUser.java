/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class adminUpdateUser extends javax.swing.JFrame {
    private String role;
    /**
     * Creates new form adminUpdateUser
     */
    public adminUpdateUser() {
        
    }
    public adminUpdateUser(String role){
        initComponents();
        setLocationRelativeTo(null);
        this.role = role;
        Lrole.setText(role);        
        emailtxt.setEditable(false); 
        customizeForm();
        getContentPane().setBackground(new Color(255, 255, 204));
    }
    
    private void clearFields() {
        GuiUtility.clearFields(emailtxt, nametxt, userbirthtxt, phonetxt, platnumtxt, addresstxta, cuisinecbx);
    }
    
    private void customizeForm() {
        javax.swing.JComponent[] customerComponents = {
            Laddress, addresstxta, jScrollPane1, LotherInfo
        };
        javax.swing.JComponent[] vendorComponents = {
            Lshop, cuisinecbx
        };
        javax.swing.JComponent[] runnerComponents = {
            Lplatnum, platnumtxt, LotherInfo
        };
        this.customizeForm(role, customerComponents, vendorComponents, runnerComponents);
    }
    
    public static void customizeForm(String role, 
                                     javax.swing.JComponent[] customerComponents, 
                                     javax.swing.JComponent[] vendorComponents, 
                                     javax.swing.JComponent[] runnerComponents) {
        // Concatenate all component arrays into a single array
        javax.swing.JComponent[] allComponents = 
            java.util.Arrays.stream(new javax.swing.JComponent[][]{customerComponents, vendorComponents, runnerComponents})
                            .flatMap(java.util.Arrays::stream)
                            .toArray(javax.swing.JComponent[]::new);

        // Hide all components initially
        for (javax.swing.JComponent component : allComponents) {
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
                    component.setVisible(true);
                }
                break;
        }
    }
    //set data after performing search at update user
    public void setUserData(String email, String name, String userBirth, String contactNumber, String roleSpecificData) {
        emailtxt.setText(email);
        nametxt.setText(name);
        userbirthtxt.setText(userBirth);
        phonetxt.setText(contactNumber);
        platnumtxt.setText(roleSpecificData);
        addresstxta.setText(roleSpecificData);
        cuisinecbx.setSelectedItem(roleSpecificData);
    }
    
    //validate fields before performing update and delete
    public static boolean validateFields(String email, String name, String birthDate, String phone, 
                                         String role, String additionalField) {
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Email field cannot be empty!");
            return false;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name field cannot be empty!");
            return false;
        }
        if (birthDate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Birth date cannot be empty!");
            return false;
        } else if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", birthDate)) {
            JOptionPane.showMessageDialog(null, "Please enter a valid birth date in the format YYYY-MM-DD!");
            return false;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Phone number cannot be empty!");
            return false;
        } else if (!Pattern.matches("^\\d{3}-\\d{7}$", phone)) {
            JOptionPane.showMessageDialog(null, "Phone number must be in the format xxx-xxxxxxx!");
            return false;
        }
        if ("customer".equals(role) && additionalField.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Address field cannot be empty for customer!");
            return false;
        } else if ("vendor".equals(role) && additionalField.equals("Please select")) {
            JOptionPane.showMessageDialog(null, "Cuisine field cannot be empty for vendor!");
            return false;
        } else if ("runner".equals(role) && additionalField.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plat number field cannot be empty for runner!");
            return false;
        }
        return true;
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
        Lsearch = new javax.swing.JLabel();
        searchtxt = new javax.swing.JTextField();
        Lemail = new javax.swing.JLabel();
        Lname = new javax.swing.JLabel();
        Lgender = new javax.swing.JLabel();
        Lphone = new javax.swing.JLabel();
        Lshop = new javax.swing.JLabel();
        LotherInfo = new javax.swing.JLabel();
        Lplatnum = new javax.swing.JLabel();
        platnumtxt = new javax.swing.JTextField();
        Laddress = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        addresstxta = new javax.swing.JTextArea();
        emailtxt = new javax.swing.JTextField();
        nametxt = new javax.swing.JTextField();
        phonetxt = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        bUpdate = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bBack = new javax.swing.JButton();
        Lrole = new javax.swing.JLabel();
        userbirthtxt = new javax.swing.JTextField();
        cuisinecbx = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Update User Information -");

        Lsearch.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lsearch.setText("Search:");

        searchtxt.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        searchtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchtxtActionPerformed(evt);
            }
        });

        Lemail.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lemail.setText("Email:");

        Lname.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lname.setText("Name:");

        Lgender.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lgender.setText("User Birth:");

        Lphone.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lphone.setText("Phone Number:");

        Lshop.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lshop.setText("Cuisine:");

        LotherInfo.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        LotherInfo.setText("Other Information");

        Lplatnum.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Lplatnum.setText("Plate number: ");

        platnumtxt.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N

        Laddress.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        Laddress.setText("Address:");

        addresstxta.setColumns(20);
        addresstxta.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        addresstxta.setRows(5);
        jScrollPane1.setViewportView(addresstxta);

        emailtxt.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N

        nametxt.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        nametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nametxtActionPerformed(evt);
            }
        });

        phonetxt.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N

        bSearch.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bSearch.setText("Search");
        bSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSearchActionPerformed(evt);
            }
        });

        bUpdate.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bUpdate.setText("Update");
        bUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUpdateActionPerformed(evt);
            }
        });

        bDelete.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bDelete.setText("Delete");
        bDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteActionPerformed(evt);
            }
        });

        bBack.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bBack.setText("Main Menu");
        bBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        Lrole.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        Lrole.setText("xxxxxx");

        cuisinecbx.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        cuisinecbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please select", "Food", "Beverages", "Restaurant", "Dessert", "Cafe", "Western", "Malaysian", "Chinese", "Asian", "Korean", "Fast Food", "BBQ", "French", "Italian", "Thai", "Mexican", "Japanese", "Indian", "Vietnamese", "Vegetarian" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Lsearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                        .addComponent(searchtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(bSearch)
                        .addGap(102, 102, 102))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(Lemail)
                                .addComponent(Lname))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Lshop)
                                    .addComponent(Laddress))))
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(emailtxt)
                                        .addComponent(nametxt, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                                    .addGap(43, 43, 43)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(Lphone)
                                        .addComponent(Lgender))
                                    .addGap(59, 59, 59)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(userbirthtxt, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                        .addComponent(phonetxt)))
                                .addComponent(jScrollPane1))
                            .addComponent(cuisinecbx, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(Lplatnum)
                        .addGap(21, 21, 21)
                        .addComponent(platnumtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(277, 277, 277))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(LotherInfo)
                        .addGap(341, 341, 341))))
            .addGroup(layout.createSequentialGroup()
                .addGap(178, 178, 178)
                .addComponent(jLabel1)
                .addGap(36, 36, 36)
                .addComponent(Lrole)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bBack, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88)
                .addComponent(bDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(355, 355, 355))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(bBack))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(Lrole))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lsearch)
                    .addComponent(searchtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bSearch))
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Lemail)
                            .addComponent(emailtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Lname)
                            .addComponent(nametxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(phonetxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lphone))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Lgender)
                            .addComponent(userbirthtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lshop)
                    .addComponent(cuisinecbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(LotherInfo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lplatnum)
                    .addComponent(platnumtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Laddress))
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bDelete)
                    .addComponent(bUpdate))
                .addGap(41, 41, 41))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchtxtActionPerformed

    private void nametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nametxtActionPerformed

    private void bUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bUpdateActionPerformed
        // Retrieve the form field values
        String email = emailtxt.getText().trim();
        String name = nametxt.getText().trim();
        String birthDate = userbirthtxt.getText().trim();
        String phone = phonetxt.getText().trim();
        String additionalField = "";

        // Get the additional field value based on the role
        if ("customer".equals(role)) {
            additionalField = addresstxta.getText().trim();
            // Replace all commas with semicolons
            additionalField = additionalField.replace(",", ";");    
            // Wrap the modified text in square brackets
            additionalField = "[" + additionalField + "]";
        } else if ("vendor".equals(role)) {
            additionalField = (String) cuisinecbx.getSelectedItem();
        } else if ("runner".equals(role)) {
            additionalField = platnumtxt.getText().trim();
        }
        Admin admin = new Admin();
        if (validateFields(email, name, birthDate, phone, role, additionalField)) {
           // Update user.txt
           boolean userUpdated = admin.updateFile("user.txt",
                   email,
                   new String[]{name, birthDate, phone},
                   new int[]{1, 4, 5});

           // Update role-specific file
           boolean roleUpdated = admin.updateFile(role + ".txt",
                   email,
                   new String[]{additionalField},
                   new int[]{1});

           if (userUpdated && roleUpdated) {
               JOptionPane.showMessageDialog(null, "Record updated successfully!");
               clearFields();
           } else if (!userUpdated) {
               JOptionPane.showMessageDialog(null, "Error updating user.txt!");
           } else {
               JOptionPane.showMessageDialog(null, "Error updating " + role + ".txt!");
           }
       }
    }//GEN-LAST:event_bUpdateActionPerformed

    private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
        String email = emailtxt.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please search an email before perform delete");
            return;
        }
        
        Admin admin = new Admin();
        int confirmation = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete this record for email: " + email + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            // Delete from user.txt
            boolean userDeleted = admin.removeInfoUserFile("user.txt", email);

            // Delete from role-specific file
            boolean roleDeleted = admin.deleteFromFile(role + ".txt", email);

            // Handle deletion results
            if (userDeleted && roleDeleted) {
                JOptionPane.showMessageDialog(null, "Record deleted successfully!");
                clearFields();
            } else if (!userDeleted) {
                JOptionPane.showMessageDialog(null, "Error deleting from user.txt!");
            } else {
                JOptionPane.showMessageDialog(null, "Error deleting from " + role + ".txt!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_bDeleteActionPerformed

    private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
        String searchEmail = searchtxt.getText();
        clearFields();
        if (searchEmail != null && !searchEmail.isEmpty()) {
            Admin admin = new Admin();
            admin.searchUser(searchEmail,role, this); // Pass the current instance of admin update
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
        }
    }//GEN-LAST:event_bSearchActionPerformed

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
            java.util.logging.Logger.getLogger(adminUpdateUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(adminUpdateUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(adminUpdateUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(adminUpdateUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adminUpdateUser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Laddress;
    private javax.swing.JLabel Lemail;
    private javax.swing.JLabel Lgender;
    private javax.swing.JLabel Lname;
    private javax.swing.JLabel LotherInfo;
    private javax.swing.JLabel Lphone;
    private javax.swing.JLabel Lplatnum;
    private javax.swing.JLabel Lrole;
    private javax.swing.JLabel Lsearch;
    private javax.swing.JLabel Lshop;
    private javax.swing.JTextArea addresstxta;
    private javax.swing.JButton bBack;
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bSearch;
    private javax.swing.JButton bUpdate;
    private javax.swing.JComboBox<String> cuisinecbx;
    private javax.swing.JTextField emailtxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nametxt;
    private javax.swing.JTextField phonetxt;
    private javax.swing.JTextField platnumtxt;
    private javax.swing.JTextField searchtxt;
    private javax.swing.JTextField userbirthtxt;
    // End of variables declaration//GEN-END:variables
}
