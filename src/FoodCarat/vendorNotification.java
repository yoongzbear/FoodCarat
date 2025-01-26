/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mastu
 */
public class vendorNotification extends javax.swing.JFrame {

    private String email = "vendor@mail.com";
    private String role = "vendor";
//    private String email = User.getSessionEmail();
//    private String email = User.getSessionRole();
    Vendor vendor = new Vendor(email);

    /**
     * Creates new form vendorNotification
     */
    public vendorNotification() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186,85,211)); //setting background color of frame
        setLocationRelativeTo(null);
        String[] filters = {"new", "status", "review", "item"};
        displayActivities(filters);
    }
    
    //display all activities    
//    Display urgent stuff first 
//    New order > order status > review > item
    public void displayActivities(String[] filters) {
        DefaultTableModel model = (DefaultTableModel) notificationTable.getModel();
        model.setRowCount(0);
        
        //get current date in yyyy-MM-dd
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        System.out.println(currentDate);
        
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
                            model.addRow(new Object[]{"New Order", orderID, message});
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
                            model.addRow(new Object[]{"Order Status", orderID, message});
                        } 
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format for order ID " + order[0] + ": " + e.getMessage());
                    }
                }
                
            } else if (filter.equalsIgnoreCase("review")) {
                //Review - if got review for order from that vendor on the date
                //order review
                Review reviews = new Review();
                List<String[]> vendorReview = reviews.getAllReviews(email, "vendor");
                List<String[]> orderReview = reviews.getAllReviews(email, "order");
                
                System.out.println(orderReview.size());

                for (String[] review : vendorReview) {
                    try {
                        LocalDate reviewDate = LocalDate.parse(review[5].trim(), dateFormat);
                        int reviewID = Integer.parseInt(review[0].trim());
                        
                        if (reviewDate.isEqual(currentDate)) {
                            String message = "You received a new review as a vendor! " + " (Review ID: " + reviewID + ")";
                            model.addRow(new Object[]{"Vendor Review", reviewID, message});
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
                            model.addRow(new Object[]{"Order Review", reviewID, message});
                        }
                    } catch (DateTimeParseException e) {
                        System.err.println("Invalid date format for review ID " + review[0] + ": " + e.getMessage());
                    }
                }
                
            } else if (filter.equalsIgnoreCase("item")) {
                //Item - deleted by vendor, item added (latest item by vendor) - show up to 10 records
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
                    model.addRow(new Object[]{"Item Added", addedItem[0], message});
                }

                // Add deleted items to the table
                for (String[] deletedItem : deletedItems) {
                    String message = "";
                    if (deletedItem[6].equalsIgnoreCase("deleted by vendor")) { 
                        message = "Item \"" + deletedItem[1] + "\" was deleted by you (Item ID: " + deletedItem[0] + ")";
                    } else if (deletedItem[6].equalsIgnoreCase("deleted by manager")) {
                        message = "Item \"" + deletedItem[1] + "\" was deleted by FoodCarat Manager (Item ID: " + deletedItem[0] + ")";
                    }
                    model.addRow(new Object[]{"Item Deleted", deletedItem[0], message});
                }
            }
        }
    }

//    Automatically call the panel and populate the details part

    public void displayDetails() {
        //call panel based on type then populate with info (idk can or not)
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
        viewBtn = new javax.swing.JButton();
        itemChkBox = new javax.swing.JCheckBox();

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
                "Type", "ID", "Activity"
            }
        ));
        notificationTable.setRowHeight(35);
        jScrollPane1.setViewportView(notificationTable);
        if (notificationTable.getColumnModel().getColumnCount() > 0) {
            notificationTable.getColumnModel().getColumn(0).setResizable(false);
            notificationTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            notificationTable.getColumnModel().getColumn(1).setResizable(false);
            notificationTable.getColumnModel().getColumn(1).setPreferredWidth(10);
            notificationTable.getColumnModel().getColumn(2).setResizable(false);
            notificationTable.getColumnModel().getColumn(2).setPreferredWidth(500);
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

        viewBtn.setText("View Details");
        viewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewBtnActionPerformed(evt);
            }
        });

        itemChkBox.setText("Item");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(76, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(191, 191, 191)
                        .addComponent(menuBtn)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(viewBtn)
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
                                    .addGap(18, 18, 18)
                                    .addComponent(filterBtn))))
                        .addGap(46, 46, 46))))
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
                    .addComponent(itemChkBox))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewBtn)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void viewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewBtnActionPerformed
        // TODO add your handling code here:
        //call respective GUI classes hehe
    }//GEN-LAST:event_viewBtnActionPerformed

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
        selectedFilter = Arrays.copyOf(selectedFilter, index); //adjust the size of array
        //call filter
        displayActivities(selectedFilter);
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
    private javax.swing.JButton viewBtn;
    // End of variables declaration//GEN-END:variables
}
