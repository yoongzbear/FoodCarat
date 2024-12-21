/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        this("customer");
    }
    public adminUpdateUser(String role){
        initComponents();
        emailtxt.setEditable(false);
        this.role = role;
        customizeForm();
        Lrole.setText(role);
    }
private void customizeForm() {
    javax.swing.JComponent[] customerComponents = {
        Laddress, addresstxta, jScrollPane1,LotherInfo
    };
    javax.swing.JComponent[] vendorComponents = {
        Lshop, shoptxt
    };
    javax.swing.JComponent[] runnerComponents = {
        Lplatnum, platnumtxt, Lcartype, cartypecbx,LotherInfo
    };

    admin.customizeForm(role, customerComponents, vendorComponents, runnerComponents);
}

private void clearFields() {
    admin.clearFields(
        emailtxt, nametxt, agetxt, phonetxt, platnumtxt, addresstxta, shoptxt
    );
    admin.clearComboBoxes(gendercbx);
}
   
private void performSearch() {
    String searchQuery = searchtxt.getText().trim();
    String fileName = role + ".txt";

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String line;
        boolean found = false;

        // Clear the table and fields before populating
        clearFields();

        while ((line = br.readLine()) != null) {
            String[] data = line.split(";"); // Assuming CSV format
            if (data[0].equalsIgnoreCase(searchQuery)) { // Assuming email is the 2nd column
                found = true;

                // Populate fields
                emailtxt.setText(data[0]);
                nametxt.setText(data[1]);
                gendercbx.setSelectedItem(data[2]);
                agetxt.setText(data[3]);
                phonetxt.setText(data[4]);

                if ("customer".equals(role)) {
                    addresstxta.setText(data[6]);    
                } else if ("vendor".equals(role)) {
                    shoptxt.setText(data[6]);
                } else if("runner".equals(role)){
                    platnumtxt.setText(data[6]);
                    cartypecbx.setSelectedItem(data[7]);
                }
                break;
            }
        }

        if (!found) {
            javax.swing.JOptionPane.showMessageDialog(this, "No matching record found!");
        }

    } catch (IOException ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
    }
}


private void performUpdate() {
    String fileName = role + ".txt"; // e.g., "runner.txt", "vendor.txt", or "user.txt"
    String email = emailtxt.getText().trim();

    if (email.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Email field cannot be empty!");
        return;
    }

    List<String> fileContent = new ArrayList<>();
    boolean recordUpdated = false;

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");

            if (data[0].equalsIgnoreCase(email)) {
                // Update the record with new information from the fields
                data[1] = nametxt.getText().trim();
                data[2] = (String) gendercbx.getSelectedItem();
                data[3] = agetxt.getText().trim();
                data[4] = phonetxt.getText().trim();

                if ("customer".equals(role)) {
                    data[6] = addresstxta.getText().trim();
                } else if ("vendor".equals(role)) {
                    data[6] = shoptxt.getText().trim();
                } else if ("runner".equals(role)) {
                    data[6] = platnumtxt.getText().trim();
                    data[7] = (String) cartypecbx.getSelectedItem();
                }

                // Reconstruct the updated line
                line = String.join(";", data);
                recordUpdated = true;
            }

            fileContent.add(line); // Add the (updated or unchanged) line to memory
        }
    } catch (IOException ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
        return;
    }

    if (!recordUpdated) {
        javax.swing.JOptionPane.showMessageDialog(this, "No matching record found to update!");
        return;
    }

    // Write the updated content back to the file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
        for (String line : fileContent) {
            bw.write(line);
            bw.newLine();
        }
    } catch (IOException ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage());
        return;
    }

    javax.swing.JOptionPane.showMessageDialog(this, "Record updated successfully!");
    clearFields();
}


private void performDelete() {
    String fileName = role + ".txt";
    String email = emailtxt.getText().trim();

    if (email.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Email field cannot be empty!");
        return;
    }

    // Confirmation dialog
    int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to delete this record?", 
        "Confirm Deletion", 
        javax.swing.JOptionPane.YES_NO_OPTION);

    if (confirm != javax.swing.JOptionPane.YES_OPTION) {
        return; // Cancel deletion if the user selects "No"
    }

    List<String> fileContent = new ArrayList<>();
    boolean recordDeleted = false;

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(";");

            if (data[0].equalsIgnoreCase(email)) {
                recordDeleted = true; // Mark the record for deletion
                continue; // Skip adding this line to the file content
            }

            fileContent.add(line); // Add other lines to memory
        }
    } catch (IOException ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
        return;
    }

    if (!recordDeleted) {
        javax.swing.JOptionPane.showMessageDialog(this, "No matching record found to delete!");
        return;
    }

    // Write the updated content back to the file
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
        for (String line : fileContent) {
            bw.write(line);
            bw.newLine();
        }
    } catch (IOException ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage());
        return;
    }

    javax.swing.JOptionPane.showMessageDialog(this, "Record deleted successfully!");
    clearFields();
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
        Lage = new javax.swing.JLabel();
        Lphone = new javax.swing.JLabel();
        Lshop = new javax.swing.JLabel();
        LotherInfo = new javax.swing.JLabel();
        Lplatnum = new javax.swing.JLabel();
        Lcartype = new javax.swing.JLabel();
        platnumtxt = new javax.swing.JTextField();
        cartypecbx = new javax.swing.JComboBox<>();
        Laddress = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        addresstxta = new javax.swing.JTextArea();
        emailtxt = new javax.swing.JTextField();
        nametxt = new javax.swing.JTextField();
        agetxt = new javax.swing.JTextField();
        phonetxt = new javax.swing.JTextField();
        shoptxt = new javax.swing.JTextField();
        bSearch = new javax.swing.JButton();
        bUpdate = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();
        bBack = new javax.swing.JButton();
        gendercbx = new javax.swing.JComboBox<>();
        Lrole = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Update User Information -");

        Lsearch.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lsearch.setText("Search:");

        searchtxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        searchtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchtxtActionPerformed(evt);
            }
        });

        Lemail.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lemail.setText("Email:");

        Lname.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lname.setText("Name:");

        Lgender.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lgender.setText("Gender:");

        Lage.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lage.setText("Age:");

        Lphone.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lphone.setText("Phone Number:");

        Lshop.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lshop.setText("Shop Name:");

        LotherInfo.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        LotherInfo.setText("Other Information");

        Lplatnum.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lplatnum.setText("Plate number: ");

        Lcartype.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Lcartype.setText("Vehicle Type:");

        platnumtxt.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N

        cartypecbx.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        cartypecbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Car", "Motor" }));

        Laddress.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        Laddress.setText("Address:");

        addresstxta.setColumns(20);
        addresstxta.setRows(5);
        jScrollPane1.setViewportView(addresstxta);

        emailtxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N

        nametxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        nametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nametxtActionPerformed(evt);
            }
        });

        agetxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N

        phonetxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N

        shoptxt.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N

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
        bBack.setText("Back");
        bBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        gendercbx.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        gendercbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        Lrole.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        Lrole.setText("xxxxxx");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(261, 261, 261)
                        .addComponent(bUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(126, 126, 126)
                        .addComponent(bDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118)
                        .addComponent(bBack, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Lplatnum)
                            .addComponent(Laddress)
                            .addComponent(Lgender)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(Lemail)
                                .addComponent(Lname))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(Lsearch)))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(searchtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(emailtxt)
                                            .addComponent(nametxt)
                                            .addComponent(gendercbx, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(54, 54, 54)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Lage)
                                            .addComponent(Lphone)
                                            .addComponent(Lshop)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(LotherInfo)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(platnumtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(62, 62, 62)
                                            .addComponent(Lcartype))))
                                .addGap(59, 59, 59)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cartypecbx, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(phonetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(shoptxt, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(bSearch)
                                        .addComponent(agetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addComponent(jLabel1)
                        .addGap(36, 36, 36)
                        .addComponent(Lrole)))
                .addContainerGap(110, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Lrole))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Lsearch)
                    .addComponent(searchtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bSearch))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Lemail)
                            .addComponent(emailtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Lname)
                            .addComponent(nametxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Lgender)
                            .addComponent(gendercbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(Lage)
                                .addGap(15, 15, 15))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(agetxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(phonetxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lphone))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(shoptxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lshop))))
                .addGap(51, 51, 51)
                .addComponent(LotherInfo)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cartypecbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Lcartype)
                    .addComponent(Lplatnum)
                    .addComponent(platnumtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Laddress))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bBack)
                    .addComponent(bDelete)
                    .addComponent(bUpdate))
                .addGap(36, 36, 36))
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
        if (!admin.validateInputs(
                role, emailtxt, nametxt, agetxt, phonetxt, platnumtxt, shoptxt, addresstxta)) {
            return; // Exit if validation fails
        }
        performUpdate();
    }//GEN-LAST:event_bUpdateActionPerformed

    private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
       performDelete();
    }//GEN-LAST:event_bDeleteActionPerformed

    private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
        performSearch();
    }//GEN-LAST:event_bSearchActionPerformed

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
    private javax.swing.JLabel Lage;
    private javax.swing.JLabel Lcartype;
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
    private javax.swing.JTextField agetxt;
    private javax.swing.JButton bBack;
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bSearch;
    private javax.swing.JButton bUpdate;
    private javax.swing.JComboBox<String> cartypecbx;
    private javax.swing.JTextField emailtxt;
    private javax.swing.JComboBox<String> gendercbx;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nametxt;
    private javax.swing.JTextField phonetxt;
    private javax.swing.JTextField platnumtxt;
    private javax.swing.JTextField searchtxt;
    private javax.swing.JTextField shoptxt;
    // End of variables declaration//GEN-END:variables
}
