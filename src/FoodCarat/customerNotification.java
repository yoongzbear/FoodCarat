/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class customerNotification extends javax.swing.JFrame {

    /**
     * Creates new form customerNotification
     */
    private String email = User.getSessionEmail();

    public customerNotification() {
        initComponents();
        String[] filters = {
            "incomplete",
            "completed",
            "cancelled",
            "reload"
        };
        displayNotif(filters);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new java.awt.Color(180, 200, 234));
    }

    public void displayNotif(String[] filters) {
        DefaultTableModel model = (DefaultTableModel) tbNotif.getModel();
        model.setRowCount(0);
        TableColumn column1 = tbNotif.getColumnModel().getColumn(0);
        column1.setPreferredWidth(100);
        TableColumn column2 = tbNotif.getColumnModel().getColumn(1);
        column2.setPreferredWidth(450);

        Order orders = new Order();
        List<String[]> notifications = new ArrayList<>();

        //loop through filters and populate notifications
        for (String filter : filters) {
            List<String[]> allOrders = orders.getAllOrders();

            //sort orders by date (most recent first)
            Collections.sort(allOrders, (order1, order2) -> {
                String dateStr1 = order1[9];
                String dateStr2 = order2[9];
                dateStr1 = dateStr1.trim();
                dateStr2 = dateStr2.trim();
                LocalDate date1 = LocalDate.parse(dateStr1);
                LocalDate date2 = LocalDate.parse(dateStr2);
                return date2.compareTo(date1);  //latest record first
            });

            if (filter.equalsIgnoreCase("incomplete")) {
                for (String[] orderData : allOrders) {
                    int orderID = Integer.parseInt(orderData[0].trim());
                    String orderStatus = orderData[3];
                    String cusEmail = orderData[4];

                    String title = "";
                    String message = "";

                    if (email.equalsIgnoreCase(cusEmail)) {
                        //check order status and set the appropriate message
                        if ("pending accept".equalsIgnoreCase(orderStatus)) {
                            title = "Order Placed";
                            message = "Order placed! Please wait for vendor to accept. (Order ID: " + orderID + ")";
                        } else if ("assigning runner".equalsIgnoreCase(orderStatus)) {
                            title = "Runner Assigned";
                            message = "Your order has been assigned a runner! Please wait for runner to accept the task. (Order ID: " + orderID + ")";
                        } else if ("ordered".equalsIgnoreCase(orderStatus)) {
                            title = "Order in Queue";
                            message = "Your order will be in the kitchen soon. (Order ID: " + orderID + ")";
                        } else if ("in kitchen".equalsIgnoreCase(orderStatus)) {
                            title = "Order in Prepare";
                            message = "Your order is preparing now! (Order ID: " + orderID + ")";
                        } else if ("ready".equalsIgnoreCase(orderStatus)) {
                            title = "Order is Ready";
                            message = "Your order is ready now! (Order ID: " + orderID + ")";
                        } else if ("picked up by runner".equalsIgnoreCase(orderStatus)) {
                            title = "Order in Delivery";
                            message = "Your order is picked up by the runner! The order will arrive soon. (Order ID: " + orderID + ")";
                        }
                    }

                    if (!message.isEmpty()) {
                        notifications.add(new String[]{title, message, orderData[9]});
                    }
                }
            } else if (filter.equalsIgnoreCase("completed")) {
                //for completed order
                for (String[] orderData : allOrders) {
                    int orderID = Integer.parseInt(orderData[0].trim());
                    String orderStatus = orderData[3];
                    String cusEmail = orderData[4];
                    if ("completed".equalsIgnoreCase(orderStatus) && email.equalsIgnoreCase(cusEmail)) {
                        String message = "Order complete! We’d love your feedback. Thanks for choosing Foodcarat! (Order ID: " + orderID + ")";
                        notifications.add(new String[]{"Order Complete", message, orderData[9]});
                    }
                }
            } else if (filter.equalsIgnoreCase("cancelled")) {
                //for cancelled order
                for (String[] orderData : allOrders) {
                    int orderID = Integer.parseInt(orderData[0].trim());
                    String orderStatus = orderData[3];
                    String cusEmail = orderData[4];
                    String cancelReason = orderData[6];
                    if ("cancelled".equalsIgnoreCase(orderStatus) && email.equalsIgnoreCase(cusEmail)) {
                        String message = "Sorry, your order was cancelled due to: " + cancelReason + ". A refund has been issued. (Order ID: " + orderID + ")";
                        notifications.add(new String[]{"Order Cancelled", message, orderData[9]});
                    }
                }
            } else if (filter.equalsIgnoreCase("reload")) {
                Customer customer = new Customer();
                List<String[]> records = customer.creditRecord(email);
                for (String[] record : records) {
                    int transactionID = Integer.parseInt(record[0].trim());
                    double prevBalance = Double.parseDouble(record[2]);
                    double TopupAmount = Double.parseDouble(record[3]);
                    String dateTimeStr = record[4] + " " + record[5];  // combine date and time parts
                    DecimalFormat df = new DecimalFormat("0.00");
                    String message = "Topped up RM" + df.format(TopupAmount) + " at " + dateTimeStr + ". New balance is RM" + df.format(prevBalance + TopupAmount) + " (Transaction ID: " + transactionID + ")";
                    notifications.add(new String[]{"Top-up Notification", message, dateTimeStr});
                }
            }
        }

        // Sort notifications
        notifications.sort((notif1, notif2) -> {
            String timestamp1 = notif1[2].trim();
            String timestamp2 = notif2[2].trim();

            try {
                //reload notif if the timestamp contains time
                if (timestamp1.contains(" ") && timestamp2.contains(" ")) {
                    // Parse both as LocalDateTime (date and time)
                    LocalDateTime dateTime1 = LocalDateTime.parse(timestamp1, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime dateTime2 = LocalDateTime.parse(timestamp2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    return dateTime2.compareTo(dateTime1);  // Sort by date and time for reload
                } else {
                    //order notif that does not have time, add based on the recent record (did above)
                    LocalDate date1 = LocalDate.parse(timestamp1);
                    LocalDate date2 = LocalDate.parse(timestamp2);
                    return date2.compareTo(date1);
                }
            } catch (DateTimeParseException e) {
                return 0;
            }
        });

        //add notifications to the table
        for (String[] notification : notifications) {
            model.addRow(notification);
        }
    }

    public void displayDetails(int id, String title) {
        //call panel based on type then populate with info
        if (title.equalsIgnoreCase("Runner Assigned")
                || title.equalsIgnoreCase("Order in Queue")
                || title.equalsIgnoreCase("Order in Prepare")
                || title.equalsIgnoreCase("Order is Ready")
                || title.equalsIgnoreCase("Order in Delivery")
                || title.equalsIgnoreCase("Order Complete")
                || title.equalsIgnoreCase("Order Cancelled")) {

            String orderID = String.valueOf(id);

            customerOrderHistory frame = new customerOrderHistory();
            frame.populateTable(orderID);
            frame.setVisible(true);
            dispose();
        } else if (title.equalsIgnoreCase("Order Placed")) {
            Order order = new Order();
            String[] orderInfo = order.getOrder(id);
            int orderID = Integer.parseInt(orderInfo[0]);
            String orderType = orderInfo[1];

            customerReceipt frame = new customerReceipt(orderID, orderType);
            frame.setVisible(true);
            dispose();
        } else if (title.equalsIgnoreCase("Top-up Notification")) {
            Customer customer = new Customer();

            String[] transactInfo = customer.getTopupInfo(id);
            int transactID = Integer.parseInt(transactInfo[0]);
            double prevAmount = Double.parseDouble(transactInfo[2]);
            double topupAmount = Double.parseDouble(transactInfo[3]);
            String transactDate = transactInfo[4];
            String transactTime = transactInfo[5];

            String[] userInfo = customer.getUserInfo(email);
            String userName = userInfo[1];

            adminCusReceipt frame = new adminCusReceipt(transactID, email, userName, prevAmount, topupAmount, transactDate, transactTime);
            frame.setVisible(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbNotif = new javax.swing.JTable();
        bBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbComplete = new javax.swing.JCheckBox();
        cbCancelled = new javax.swing.JCheckBox();
        cbIncomplete = new javax.swing.JCheckBox();
        cbReload = new javax.swing.JCheckBox();
        bFilter = new javax.swing.JButton();
        bClear = new javax.swing.JButton();
        bDetails = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tbNotif.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "Message"
            }
        ));
        tbNotif.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tbNotif);

        bBack.setText("Main Menu");
        bBack.setMaximumSize(new java.awt.Dimension(93, 23));
        bBack.setMinimumSize(new java.awt.Dimension(93, 23));
        bBack.setPreferredSize(new java.awt.Dimension(93, 23));
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("FoodCarat Food Court");

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel2.setText("Notification");

        cbComplete.setText("Completed Order");

        cbCancelled.setText("Cancelled Order");

        cbIncomplete.setText("Incomplete Order");

        cbReload.setText("Credit Reload");

        bFilter.setText("Show");
        bFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFilterActionPerformed(evt);
            }
        });

        bClear.setText("Clear Filter");
        bClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bClearActionPerformed(evt);
            }
        });

        bDetails.setText("View Details");
        bDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDetailsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(145, 145, 145)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap(151, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(bBack, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(bDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(cbComplete)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbCancelled)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbIncomplete)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbReload)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(bClear)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(bFilter))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(bBack, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbComplete)
                    .addComponent(cbCancelled)
                    .addComponent(cbIncomplete)
                    .addComponent(cbReload)
                    .addComponent(bFilter)
                    .addComponent(bClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bDetails)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBackActionPerformed
        //Back to the main page
        this.dispose();
        customerMain frame = new customerMain();
        frame.setVisible(true);
    }//GEN-LAST:event_bBackActionPerformed

    private void bFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFilterActionPerformed
        String[] selectedFilter = new String[5];
        int index = 0;

        if (cbComplete.isSelected()) {
            selectedFilter[index++] = "completed";
        }
        if (cbCancelled.isSelected()) {
            selectedFilter[index++] = "cancelled";
        }
        if (cbIncomplete.isSelected()) {
            selectedFilter[index++] = "incomplete";
        }
        if (cbReload.isSelected()) {
            selectedFilter[index++] = "reload";
        }
        selectedFilter = Arrays.copyOf(selectedFilter, index); //adjust the size of array
        //call filter
        displayNotif(selectedFilter);
    }//GEN-LAST:event_bFilterActionPerformed

    private void bClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bClearActionPerformed
        //deselect all the checkboxes
        cbComplete.setSelected(false);
        cbCancelled.setSelected(false);
        cbIncomplete.setSelected(false);
        cbReload.setSelected(false);

        displayNotif(new String[]{"completed", "cancelled", "incomplete", "reload"});
    }//GEN-LAST:event_bClearActionPerformed

    private void bDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDetailsActionPerformed
        //call respective GUI classes
        int selectedRow = tbNotif.getSelectedRow();
        //validation to choose a row 
        if (selectedRow >= 0) {
            //get ID, type and message
            String title = tbNotif.getModel().getValueAt(selectedRow, 0).toString(); //type column   
            int id = 0;
            String message = tbNotif.getModel().getValueAt(selectedRow, 1).toString(); //message column

            if (title.equalsIgnoreCase("Order Placed")
                    || title.equalsIgnoreCase("Runner Assigned")
                    || title.equalsIgnoreCase("Order in Queue")
                    || title.equalsIgnoreCase("Order in Prepare")
                    || title.equalsIgnoreCase("Order is Ready")
                    || title.equalsIgnoreCase("Order in Delivery")
                    || title.equalsIgnoreCase("Order Complete")
                    || title.equalsIgnoreCase("Order Cancelled")) {
                int startIndex = message.indexOf("(Order ID:") + "(Order ID: ".length();
                int endIndex = message.indexOf(")", startIndex);
                String orderID = message.substring(startIndex, endIndex).trim();
                id = Integer.parseInt(orderID);
            } else if (title.equalsIgnoreCase("Top-up Notification")) {
                int startIndex = message.indexOf("(Transaction ID: ") + "(Transaction ID: ".length();
                int endIndex = message.indexOf(")", startIndex);
                String transactionID = message.substring(startIndex, endIndex).trim();
                id = Integer.parseInt(transactionID);
            }

            displayDetails(id, title);
        } else { //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to view details.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_bDetailsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBack;
    private javax.swing.JButton bClear;
    private javax.swing.JButton bDetails;
    private javax.swing.JButton bFilter;
    private javax.swing.JCheckBox cbCancelled;
    private javax.swing.JCheckBox cbComplete;
    private javax.swing.JCheckBox cbIncomplete;
    private javax.swing.JCheckBox cbReload;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbNotif;
    // End of variables declaration//GEN-END:variables
}
