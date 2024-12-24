/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class customerOrderHistory extends javax.swing.JFrame {

    /**
     * Creates new form customerOrderHistory
     */
    public customerOrderHistory() {
        initComponents();
        populateTable();
        addTableListener();
    }
    
    private Map<String, List<String[]>> orderDetailsMap = new HashMap<>();
    
    private void addTableListener() {
        tOrderHistory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tOrderHistory.getSelectedRow();
                    if (selectedRow != -1) {
                        try {
                            //Get the selected row data
                            String orderID = (String) tOrderHistory.getValueAt(selectedRow, 0);
                            String orderType = (String) tOrderHistory.getValueAt(selectedRow, 1);
                            String totalPrice = (String) tOrderHistory.getValueAt(selectedRow, 3);
                            String vendorName = (String) tOrderHistory.getValueAt(selectedRow, 4);
                            String orderStatus = (String) tOrderHistory.getValueAt(selectedRow, 5);
                            
                            //Get feedback
                            Order order = new Order(Integer.parseInt(orderID));
                            String orderFeedback = order.getOrderFeedback();
                            
                            //Set the data to text fields
                            lOrderType.setText(orderType);
                            ltotalPrice.setText(totalPrice);
                            lVendorName.setText(vendorName);
                            lOrderStatus.setText(orderStatus);
                            
                            //Feedback
                            if (orderFeedback != null && !orderFeedback.isEmpty()) {
                                //Feedback exists, set the text area to read-only and display feedback
                                taFeedback.setText(orderFeedback);
                                taFeedback.setEditable(false);
                                bFeedback.setVisible(false);
                            } else {
                                //No feedback, allow the user to enter feedback
                                taFeedback.setText("");
                                taFeedback.setEditable(true);
                                bFeedback.setVisible(true);
                            }
                            
                            //For the order items
                            DefaultTableModel model = (DefaultTableModel) tbOrderItem.getModel();
                            DecimalFormat df = new DecimalFormat("0.00");
                            model.setRowCount(0);
                            
                            //Split the order list by semicolon (;) to get individual order items
                            List<String[]> orderItemDetails = orderDetailsMap.get(orderID);
                            
                            //Loop through each item in the order and process it
                            for (String[] item : orderItemDetails) {
                                String itemName = item[0];
                                String itemQuantity = item[1];
                                String itemPrice = item[2];
                                model.addRow(new Object[]{itemName, itemQuantity, "RM" + df.format(Double.parseDouble(itemPrice))});
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(customerOrderHistory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }

    private void populateTable() {
        DefaultTableModel model = new DefaultTableModel();
        DecimalFormat df = new DecimalFormat("0.00");
        model.setRowCount(0);
        model.setColumnCount(0);
        //Header
        model.addColumn("Order ID");
        model.addColumn("Order Type");
        model.addColumn("Order Item");
        model.addColumn("Total Price");
        model.addColumn("Vendor Name");
        model.addColumn("Order Status");
        model.addColumn("Cancel Reason");

        try (BufferedReader reader = new BufferedReader(new FileReader("customerOrder.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                String rUser = record[5];
                if (rUser.equals("customerEmail")){ //UserSession.getCustomerEmail()
                    String rOrderType = record[1];
                    String rOrderList = record[2].replace("[", "").replace("]", "");
                    String rOrderStatus = record[3];
                    String rCancelReason = record[6]; 
                    
                    //Split the order items by semicolon
                    String[] orderItems = rOrderList.split("\\|");
                    StringBuilder orderItemsConcatenated = new StringBuilder();
                    double totalPrice = 0.0; 
                    
                    List<String[]> orderItemDetails = new ArrayList<>();

                    //Loop through each item in the order and concatenate them with a comma
                    for (int i = 0; i < orderItems.length; i++) {
                        String[] itemDetails = orderItems[i].split(";");
                        String rOrderItem = itemDetails[0]; 
                        String rItemQuantity = itemDetails[1];
                        String rItemPrice = itemDetails[2]; //Need to change based on the vendor ori price
                        
                        orderItemDetails.add(new String[]{rOrderItem, rItemQuantity, rItemPrice});

                        //Update total price
                        totalPrice += Double.parseDouble(itemDetails[2]);

                        //Append item to the StringBuilder with a comma
                        if (i > 0) {
                            orderItemsConcatenated.append(", "); 
                        }
                        orderItemsConcatenated.append(rOrderItem);
                    }
                    
                    
                    //Get the final concatenated string
                    String allOrderItems = orderItemsConcatenated.toString();
                    
                    String orderID = record[0]; 
                    orderDetailsMap.put(orderID, orderItemDetails);

                    //Add the booking details to the model
                    model.addRow(new Object[]{orderID, rOrderType, allOrderItems, "RM" + df.format(totalPrice), "Vendor Name", rOrderStatus, rCancelReason});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Set the model to the table
        tOrderHistory.setModel(model);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        bNotif = new javax.swing.JButton();
        bBack = new javax.swing.JButton();
        bCheckPoint = new javax.swing.JButton();
        bComplaintFC = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tOrderHistory = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lVendorName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taFeedback = new javax.swing.JTextArea();
        bAction = new javax.swing.JButton();
        bLogout = new javax.swing.JButton();
        ltotalPrice = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbOrderItem = new javax.swing.JTable();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        bFeedback = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        lOrderStatus = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lOrderType = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bNotif.setText("Notification");
        bNotif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNotifActionPerformed(evt);
            }
        });

        bBack.setText("Back to Main Page");
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        bCheckPoint.setText("Points / Credit Balance");
        bCheckPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCheckPointActionPerformed(evt);
            }
        });

        bComplaintFC.setText("FoodCourt Feedback");
        bComplaintFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bComplaintFCActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("FoodCarat Food Court");

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel2.setText("Order History");

        tOrderHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tOrderHistory.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tOrderHistory);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Order Item in one line -> Order Details");

        jLabel4.setText("Vendor Name :");

        lVendorName.setText("vendorName");

        jLabel6.setText("Ordered Item(s) :");

        jLabel7.setText("Total Price :");

        jLabel8.setText("Feedback :");

        taFeedback.setColumns(20);
        taFeedback.setRows(5);
        jScrollPane2.setViewportView(taFeedback);

        bAction.setText("Reorder");

        bLogout.setText("Logout");
        bLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLogoutActionPerformed(evt);
            }
        });

        ltotalPrice.setText("totalPrice");

        tbOrderItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order Item", "Quantity", "Price"
            }
        ));
        jScrollPane4.setViewportView(tbOrderItem);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Daily");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Weekly");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Monthly");

        bFeedback.setText("Save Feedback");
        bFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFeedbackActionPerformed(evt);
            }
        });

        jLabel5.setText("Order Status:");

        lOrderStatus.setText("orderStatus");

        jLabel9.setText("Order Type:");

        lOrderType.setText("orderType");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bNotif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bCheckPoint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bComplaintFC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(174, 174, 174)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addGap(111, 111, 111)
                                            .addComponent(bFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(bAction, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(49, 49, 49)
                                        .addComponent(lOrderType))))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ltotalPrice)
                                            .addComponent(lVendorName, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jRadioButton1)
                                    .addGap(33, 33, 33)
                                    .addComponent(jRadioButton2)
                                    .addGap(21, 21, 21)
                                    .addComponent(jRadioButton3))))
                        .addGap(27, 27, 27))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bNotif, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bCheckPoint, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bComplaintFC, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bBack, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButton1)
                            .addComponent(jRadioButton2)
                            .addComponent(jRadioButton3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lVendorName)
                            .addComponent(jLabel9)
                            .addComponent(lOrderType))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(ltotalPrice))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lOrderStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(bAction)
                                    .addComponent(bFeedback)))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBackActionPerformed
        this.dispose();
        customerMain frame = new customerMain();
        frame.setVisible(true);
    }//GEN-LAST:event_bBackActionPerformed

    private void bLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLogoutActionPerformed
        int choice = JOptionPane.showConfirmDialog(null, "Are you sure to logout?");
        if (choice == JOptionPane.YES_OPTION){
            this.dispose();
            userLogin frame = new userLogin();
            frame.setVisible(true);
        }
    }//GEN-LAST:event_bLogoutActionPerformed

    private void bFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFeedbackActionPerformed
        if (taFeedback != null && !taFeedback.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to save the feedback? No changes are allowed after saving.");
            if (confirm == JOptionPane.YES_OPTION){
                int selectedRow = tOrderHistory.getSelectedRow();
            
                if (selectedRow != -1) {
                    String orderIDString = (String) tOrderHistory.getValueAt(selectedRow, 0);
                    int orderID = Integer.parseInt(orderIDString); 
                    String feedback = taFeedback.getText();

                    Order order = new Order(orderID, feedback);
                    order.saveOrderFeedback();
                    taFeedback.setEditable(false);
                    bFeedback.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "No order selected.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please insert the feedback before saving it.");
        }
    }//GEN-LAST:event_bFeedbackActionPerformed

    private void bNotifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNotifActionPerformed
        this.dispose();
        customerNotification frame = new customerNotification();
        frame.setVisible(true);
    }//GEN-LAST:event_bNotifActionPerformed

    private void bCheckPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCheckPointActionPerformed
        this.dispose();
        customerBalance frame = new customerBalance();
        frame.setVisible(true);
    }//GEN-LAST:event_bCheckPointActionPerformed

    private void bComplaintFCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bComplaintFCActionPerformed
        this.dispose();
        customerFCFeedback frame = new customerFCFeedback();
        frame.setVisible(true);
    }//GEN-LAST:event_bComplaintFCActionPerformed

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
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new customerOrderHistory().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAction;
    private javax.swing.JButton bBack;
    private javax.swing.JButton bCheckPoint;
    private javax.swing.JButton bComplaintFC;
    private javax.swing.JButton bFeedback;
    private javax.swing.JButton bLogout;
    private javax.swing.JButton bNotif;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lOrderStatus;
    private javax.swing.JLabel lOrderType;
    private javax.swing.JLabel lVendorName;
    private javax.swing.JLabel ltotalPrice;
    private javax.swing.JTable tOrderHistory;
    private javax.swing.JTextArea taFeedback;
    private javax.swing.JTable tbOrderItem;
    // End of variables declaration//GEN-END:variables
}
