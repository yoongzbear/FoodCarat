/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mastu
 */
public class vendorNotification extends javax.swing.JFrame {

    private String email = User.getSessionEmail();
    Vendor vendor = new Vendor(email);

    /**
     * Creates new form vendorNotification
     */
    public vendorNotification() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186,85,211)); //setting background color of frame
        setLocationRelativeTo(null);
        
        newOrderChkBox.setSelected(true);
        orderStatusChkBox.setSelected(true);
        reviewChkBox.setSelected(true);
        itemChkBox.setSelected(true);
        withdrawChkBox.setSelected(true);
        
        String[] filters = {"new", "status", "review", "item", "withdraw"};
        displayActivities(filters);
        
        notificationTableListener();
    }
    
    //display all activities    
    public void displayActivities(String[] filters) {
        DefaultTableModel model = (DefaultTableModel) notificationTable.getModel();
        model.setRowCount(0);
        
        //get current date in yyyy-MM-dd
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        
        Order orders = new Order();
        
        for (String filter : filters) {
            if (filter.equalsIgnoreCase("new")) {
                //New order - Display for the same day only, display the oldest order with status pending accept and date = current date
                List<String[]> newOrder = orders.getOrderByStatus(email, "pending accept");
                for (String[] order : newOrder) {
                    try {
                        LocalDate orderDate = LocalDate.parse(order[9].trim(), dateFormat); 
                        if (orderDate.isEqual(currentDate)) {
                            int orderID = Integer.parseInt(order[0].trim());
                            String message = "A new order is waiting for your approval. Please check as soon as possible. (Order ID: " + orderID + ")";
                            model.addRow(new Object[]{"New Order", message});
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format for order ID " + order[0] + ": " + e.getMessage());
                    }
                }
            } else if (filter.equalsIgnoreCase("status")) {
                //Order status - Display for same day only 
                List<String[]> allOrders = orders.getAllOrders();
                //same day, status != cancelled, pending accept, assigning runner, completed
                for (String[] order : allOrders) {
                    try {
                        LocalDate orderDate = LocalDate.parse(order[9].trim(), dateFormat);
                        String orderStatus = order[3].trim();
                        int orderID = Integer.parseInt(order[0].trim());
                        if (orderDate.isEqual(currentDate) && !orderStatus.equalsIgnoreCase("cancelled") && !orderStatus.equalsIgnoreCase("assigning runner") && !orderStatus.equalsIgnoreCase("pending accept")) {
                            String message = "An order has changed its status to \"" + orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase() + "\" (Order ID: " + orderID + ")";
                            model.addRow(new Object[]{"Order Status", message});
                        } 
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format for order ID " + order[0] + ": " + e.getMessage());
                    }
                }
                
            } else if (filter.equalsIgnoreCase("review")) {
                //order review
                Review reviews = new Review();
                List<String[]> vendorReview = reviews.getAllReviews(email, "vendor");
                List<String[]> orderReview = reviews.getAllReviews(email, "order");
                
                for (String[] review : vendorReview) {
                    try {
                        LocalDate reviewDate = LocalDate.parse(review[5].trim(), dateFormat);
                        int reviewID = Integer.parseInt(review[0].trim());
                        
                        if (reviewDate.isEqual(currentDate)) {
                            String message = "You received a new review as a vendor! " + " (Review ID: " + reviewID + ")";
                            model.addRow(new Object[]{"Vendor Review", message});
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format for review ID " + review[0] + ": " + e.getMessage());
                    }
                }
                
                for (String[] review : orderReview) {
                    try {
                        LocalDate reviewDate = LocalDate.parse(review[5].trim(), dateFormat);
                        int reviewID = Integer.parseInt(review[0].trim());
                        
                        if (reviewDate.isEqual(currentDate)) {
                            String message = "You received a new review for an order! " + " (Review ID: " + reviewID + ")";
                            model.addRow(new Object[]{"Order Review", message});
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format for review ID " + review[0] + ": " + e.getMessage());
                    }
                }
                
            } else if (filter.equalsIgnoreCase("item")) {
                //Item - deleted by vendor, item added (latest item by vendor)
                Item items = new Item();
                List<String[]> itemNotify = items.getAllItems(email, false);
                List<String[]> newItems = new ArrayList<>();
                List<String[]> deletedItems = new ArrayList<>();
                for (String[] item : itemNotify) {
                    String itemStatus = item[6].trim();
                    if (itemStatus.equalsIgnoreCase("available")) {
                        newItems.add(item);
                    } else if (itemStatus.equalsIgnoreCase("deleted by vendor") || itemStatus.equalsIgnoreCase("deleted by manager")) {
                        deletedItems.add(item);
                    }
                }

                //sort items based on latest to oldest
                newItems.sort((a, b) -> Integer.compare(Integer.parseInt(b[0]), Integer.parseInt(a[0])));
                deletedItems.sort((a, b) -> Integer.compare(Integer.parseInt(b[0]), Integer.parseInt(a[0])));

                for (String[] addedItem : newItems) {
                    String message = "New " + addedItem[2] + " item added: \"" + addedItem[1] + "\" (Item ID: " + addedItem[0] + ")";
                    model.addRow(new Object[]{"Item Added", message});
                }

                // Add deleted items to the table
                for (String[] deletedItem : deletedItems) {
                    String message = "";
                    if (deletedItem[6].equalsIgnoreCase("deleted by vendor")) { 
                        message = "Item \"" + deletedItem[1] + "\" was deleted by you (Item ID: " + deletedItem[0] + ")";
                    } else if (deletedItem[6].equalsIgnoreCase("deleted by manager")) {
                        message = "Item \"" + deletedItem[1] + "\" was deleted by FoodCarat Manager (Item ID: " + deletedItem[0] + ")";
                    }
                    model.addRow(new Object[]{"Item Deleted", message});
                }
            } else if (filter.equalsIgnoreCase("withdraw")) {
                //show withdrawal transactions
                DecimalFormat df = new DecimalFormat("0.00");
                List<String[]> transaction = vendor.getWithdrawalTransaction();
                for (String[] data : transaction) {
                    String message = "";
                    try {
                        //convert amount to number only
                        double amount = Math.abs(Double.parseDouble(data[3]));
                        message = "RM" + df.format(amount) + " has been withdrawn from your account (Transaction ID: " + data[0] + ")";
                        model.addRow(new Object[]{"Withdrawal", message});
                    } catch (NumberFormatException e) {                        
                        JOptionPane.showMessageDialog(null, "Failed in converting the amount.", "Alert", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }

    public void displayDetails(int id, String detailType) {
        //call panel based on type then populate with info
        if (detailType.equalsIgnoreCase("New Order")) {
            vendorCurrentOrder currentOrderGUI = new vendorCurrentOrder();
            currentOrderGUI.setVisible(true);
            dispose();
        } else if (detailType.equalsIgnoreCase("Order Status")) {
            vendorCurrentOrder currentOrderGUI = new vendorCurrentOrder();
            currentOrderGUI.displayOrderDetails(id);
            currentOrderGUI.setVisible(true);
            dispose();
        } else if (detailType.equalsIgnoreCase("Vendor Review")) {
            vendorReview vendorReviewGUI = new vendorReview();
            vendorReviewGUI.displaySelectedReview(id);
            vendorReviewGUI.setVisible(true);
            dispose();
        } else if (detailType.equalsIgnoreCase("Order Review")) {
            vendorOrderHistory orderReviewGUI = new vendorOrderHistory();
            //get order ID from the review 
            String[] review = new Review().getReview(id);
            int orderID = Integer.parseInt(review[1]);
            orderReviewGUI.displayVendorOrder(orderID);
            orderReviewGUI.setVisible(true);
            dispose();
        } else if (detailType.equalsIgnoreCase("Item Added")) {
            vendorMenu vendorMenuGUI = new vendorMenu();
            vendorMenuGUI.displayItems(id);
            vendorMenuGUI.setVisible(true);
            dispose();
        } else if (detailType.equalsIgnoreCase("Item Deleted")) {
            //display vendorItemDeleted page without disposing 
            vendorItemDeleted itemDeletedGUI = new vendorItemDeleted(id);
            itemDeletedGUI.setVisible(true);
        } else if (detailType.equalsIgnoreCase("Withdrawal")) {
            //display receipt - adminCusReceipt
            String[] details = vendor.getWithdrawalTransaction(id);
            adminCusReceipt receiptGUI = new adminCusReceipt(id, details[1], User.getSessionName(), Double.parseDouble(details[2]), Double.parseDouble(details[3]), details[4], details[5]);
            receiptGUI.setVisible(true);
        }
    }
    
    private void notificationTableListener() { //display respecitve GUI classes
        notificationTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure the event is the final one
                    int selectedRow = notificationTable.getSelectedRow();
                    if (selectedRow != -1) { // Check if a row is actually selected
                        //get ID, type and message
                        String type = notificationTable.getModel().getValueAt(selectedRow, 0).toString(); //type column   
                        int id = 0;
                        String message = notificationTable.getModel().getValueAt(selectedRow, 1).toString(); //message column, will change to 1

                        if (type.equalsIgnoreCase("New Order")) {
                            int startIndex = message.indexOf("(Order ID:") + "(Order ID:".length();
                            int endIndex = message.indexOf(")", startIndex);
                            String orderID = message.substring(startIndex, endIndex).trim();
                            id = Integer.parseInt(orderID);
                        } else if (type.equalsIgnoreCase("Order Status")) {
                            int startIndex = message.indexOf("(Order ID:") + "(Order ID:".length();
                            int endIndex = message.indexOf(")", startIndex);
                            String orderID = message.substring(startIndex, endIndex).trim();
                            id = Integer.parseInt(orderID);
                        } else if (type.equalsIgnoreCase("Vendor Review")) {
                            int startIndex = message.indexOf("(Review ID:") + "(Review ID:".length();
                            int endIndex = message.indexOf(")", startIndex);
                            String reviewID = message.substring(startIndex, endIndex).trim();
                            id = Integer.parseInt(reviewID);
                        } else if (type.equalsIgnoreCase("Order Review")) {
                            int startIndex = message.indexOf("(Review ID:") + "(Review ID:".length();
                            int endIndex = message.indexOf(")", startIndex);
                            String reviewID = message.substring(startIndex, endIndex).trim();
                            id = Integer.parseInt(reviewID);
                        } else if (type.equalsIgnoreCase("Item Added")) {
                            int startIndex = message.indexOf("(Item ID:") + "(Item ID:".length();
                            int endIndex = message.indexOf(")", startIndex);
                            String itemID = message.substring(startIndex, endIndex).trim();
                            id = Integer.parseInt(itemID);
                        } else if (type.equalsIgnoreCase("Item Deleted")) {
                            int startIndex = message.indexOf("(Item ID:") + "(Item ID:".length();
                            int endIndex = message.indexOf(")", startIndex);
                            String itemID = message.substring(startIndex, endIndex).trim();
                            id = Integer.parseInt(itemID);
                        } else if (type.equalsIgnoreCase("Withdrawal")) {
                            int startIndex = message.indexOf("(Transaction ID:") + "(Transaction ID:".length();
                            int endIndex = message.indexOf(")", startIndex);
                            String itemID = message.substring(startIndex, endIndex).trim();
                            id = Integer.parseInt(itemID);
                        }

                        displayDetails(id, type);
                    }
                }
            }
        });
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
        menuBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        notificationTable = new javax.swing.JTable();
        filterBtn = new javax.swing.JButton();
        newOrderChkBox = new javax.swing.JCheckBox();
        orderStatusChkBox = new javax.swing.JCheckBox();
        reviewChkBox = new javax.swing.JCheckBox();
        itemChkBox = new javax.swing.JCheckBox();
        withdrawChkBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Vendor Notification");

        menuBtn.setText("Main Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        notificationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Type", "Activity"
            }
        ));
        notificationTable.setRowHeight(35);
        jScrollPane1.setViewportView(notificationTable);
        if (notificationTable.getColumnModel().getColumnCount() > 0) {
            notificationTable.getColumnModel().getColumn(0).setResizable(false);
            notificationTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            notificationTable.getColumnModel().getColumn(1).setResizable(false);
            notificationTable.getColumnModel().getColumn(1).setPreferredWidth(500);
        }

        filterBtn.setText("Filter");
        filterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBtnActionPerformed(evt);
            }
        });

        newOrderChkBox.setText("New Order");

        orderStatusChkBox.setText("Order Status");

        reviewChkBox.setText("Review");

        itemChkBox.setText("Item");

        withdrawChkBox.setText("Withdrawal");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(151, 151, 151)
                        .addComponent(menuBtn))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 827, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(newOrderChkBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(orderStatusChkBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(reviewChkBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(itemChkBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(withdrawChkBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(filterBtn))))
                .addGap(31, 31, 31))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menuBtn)
                    .addComponent(jLabel1))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newOrderChkBox)
                    .addComponent(reviewChkBox)
                    .addComponent(filterBtn)
                    .addComponent(orderStatusChkBox)
                    .addComponent(itemChkBox)
                    .addComponent(withdrawChkBox))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void filterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtnActionPerformed
        String[] selectedFilter = new String[5];
        int index = 0;
        
        if (newOrderChkBox.isSelected()) {
            selectedFilter[index++] = "new";
        }
        if (orderStatusChkBox.isSelected()) {
            selectedFilter[index++] = "status";
        } 
        if (reviewChkBox.isSelected()) {
            selectedFilter[index++] = "review";
        } 
        if (itemChkBox.isSelected()) {
            selectedFilter[index++] = "item";
        }         
        if (withdrawChkBox.isSelected()) {
            selectedFilter[index++] = "withdraw";
        } 
        selectedFilter = Arrays.copyOf(selectedFilter, index); //adjust the size of array        
        displayActivities(selectedFilter); //call filter
    }//GEN-LAST:event_filterBtnActionPerformed

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
            java.util.logging.Logger.getLogger(vendorNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(vendorNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(vendorNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(vendorNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new vendorNotification().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton filterBtn;
    private javax.swing.JCheckBox itemChkBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton menuBtn;
    private javax.swing.JCheckBox newOrderChkBox;
    private javax.swing.JTable notificationTable;
    private javax.swing.JCheckBox orderStatusChkBox;
    private javax.swing.JCheckBox reviewChkBox;
    private javax.swing.JCheckBox withdrawChkBox;
    // End of variables declaration//GEN-END:variables
}
