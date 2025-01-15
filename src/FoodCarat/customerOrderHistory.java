/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package FoodCarat;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        bFeedback.setVisible(false);
        lRunnerNameTitle.setVisible(false);
        lRunnerName.setVisible(false);
        lRunnerRating.setVisible(false);
        cbRunnerRating.setVisible(false);
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
                            String cancelReason = (String) tOrderHistory.getValueAt(selectedRow, 6);
                            //Get feedback
                            Review review = new Review(Integer.parseInt(orderID));
                            String feedback = review.getFeedback();
                            String[] feedbackParts = feedback.split(",");
                            String orderID2 = feedbackParts[0];
                            String orderFeedback = feedbackParts[1];
                            String vendorRating = feedbackParts[2];
                            String vendorFeedback = feedbackParts[3];
                            String runnerRating = feedbackParts[4];
                            //Set the data to text fields
                            lOrderType.setText(orderType);
                            ltotalPrice.setText(totalPrice);
                            lVendorName.setText(vendorName);
                            lOrderStatus.setText(orderStatus);
                            boolean validFeedback = true;
                            //Check feedback based on the reviewType
                            if ("null".equals(feedbackParts[1]) || "null".equals(feedbackParts[2]) || "null".equals(feedbackParts[3])) {
                                validFeedback = false;
                            }
                            if ("Delivery".equals(orderType.trim())) {
                                // Set visibility of components related to the runner
                                lRunnerNameTitle.setVisible(true);
                                lRunnerName.setVisible(true);
                                lRunnerRating.setVisible(true);
                                cbRunnerRating.setVisible(true);
                            } else {
                                // Set visibility of components when not a "Delivery" order
                                lRunnerNameTitle.setVisible(false);
                                lRunnerName.setVisible(false);
                                lRunnerRating.setVisible(false);
                                cbRunnerRating.setVisible(false);
                            }
                            //Feedback
                            if (validFeedback) {
                                //Feedback exists, set the text area to read-only and display feedback
                                taOrderFeedback.setText(orderFeedback);
                                taOrderFeedback.setEditable(false);
                                cbVendorRating.setSelectedItem(vendorRating + " 🌟");
                                cbVendorRating.setEditable(false);
                                
                                taVendorFeedback.setText(vendorFeedback);
                                taVendorFeedback.setEditable(false);
                                cbRunnerRating.setSelectedItem(runnerRating + " 🌟");
                                cbRunnerRating.setEditable(false);
                                bFeedback.setVisible(false);
                            } else if (!validFeedback && "Completed".equals(orderStatus)){
                                //No feedback, allow the user to enter feedback
                                taOrderFeedback.setText("");
                                taOrderFeedback.setEditable(true);
                                cbVendorRating.setEditable(true);
                                
                                //Clear the selection (or reset it to a default choice, if desired)
                                cbVendorRating.setSelectedItem("Please Rate");
                                taVendorFeedback.setText("");
                                taVendorFeedback.setEditable(true);
                                cbRunnerRating.setEditable(true);
                                cbRunnerRating.setSelectedItem("Please Rate");
                                bFeedback.setEnabled(true);
                                bFeedback.setVisible(true);
                            } else {
                                bFeedback.setEnabled(false);
                            }
                            //For the order items
                            DefaultTableModel model = (DefaultTableModel) tbOrderItem.getModel();
                            DecimalFormat df = new DecimalFormat("0.00");
                            model.setRowCount(0);
                            //Split the order list by semicolon (;) to get individual order items
                            List<String[]> orderItemDetails = orderDetailsMap.get(orderID);
                            //Loop through each item in the order and process it
                            for (String[] item : orderItemDetails) {
                                //rOrderItemID, itemName, rItemQuantity, itemPrice
                                String itemID = item[0];
                                String itemName = item[1];
                                String itemQuantity = item[2];
                                String itemPrice = item[3];
                                
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

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/customerOrder.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                String rUser = record[4];
                if (rUser.equals(User.getSessionEmail())){
                    String rOrderType = record[1];
                    String rOrderList = record[2].replace("[", "").replace("]", "");
                    String rOrderStatus = record[3];
                    String rVendorEmail = null;
                    String rCancelReason = record[6]; 
                    
                    //Split the order items by semicolon
                    String[] orderItems = rOrderList.split("\\|");
                    StringBuilder orderItemsConcatenated = new StringBuilder();
                    double totalPrice = 0.0; 
                    
                    List<String[]> orderItemDetails = new ArrayList<>();
                    
                    //Loop through each item in the order and concatenate them with a comma
                    for (int i = 0; i < orderItems.length; i++) {
                        String[] itemDetails = orderItems[i].split(";");
                        String rOrderItemID = itemDetails[0]; 
                        String rItemQuantity = itemDetails[1];
                        //String rItemPrice = itemDetails[2]; //Need to change based on the vendor ori price
                        
                        Item item1 = new Item();
                        String[] itemInfo = item1.itemData(rOrderItemID);
                        String itemID = itemInfo[0];
                        String itemName = itemInfo[1];  
                        String itemPrice = itemInfo[3];
                        String itemImgPath = itemInfo[4];
                        
                        rVendorEmail = item1.getVendorNameByItemID(Integer.parseInt(itemID));
                        
                        orderItemDetails.add(new String[]{rOrderItemID, itemName, rItemQuantity, itemPrice});

                        //Update total price
                        totalPrice = totalPrice + Double.parseDouble(itemPrice) * Integer.parseInt(rItemQuantity);

                        //Append item to the StringBuilder with a comma
                        if (i > 0) {
                            orderItemsConcatenated.append(", "); 
                        }
                        orderItemsConcatenated.append(itemName);
                    }
                    
                    
                    //Get the final concatenated string
                    String allOrderItems = orderItemsConcatenated.toString();
                    
                    String orderID = record[0]; 
                    orderDetailsMap.put(orderID, orderItemDetails);

                    //Add the booking details to the model
                    model.addRow(new Object[]{orderID, rOrderType, allOrderItems, "RM" + df.format(totalPrice), rVendorEmail, rOrderStatus, rCancelReason});
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
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        bBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tOrderHistory = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        lVenFeedbackSection = new javax.swing.JLabel();
        lOrderType = new javax.swing.JLabel();
        lVenRateTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lOrderFeedbackSection = new javax.swing.JLabel();
        cbVendorRating = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        lVendorName = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taVendorFeedback = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lOrderFeedbackTitle = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taOrderFeedback = new javax.swing.JTextArea();
        bAction = new javax.swing.JButton();
        ltotalPrice = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbOrderItem = new javax.swing.JTable();
        lVenFeedbackTitle = new javax.swing.JLabel();
        lRunnerNameTitle = new javax.swing.JLabel();
        bFeedback = new javax.swing.JButton();
        lRunnerName = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lRunnerRating = new javax.swing.JLabel();
        lOrderStatus = new javax.swing.JLabel();
        cbRunnerRating = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        bBack.setText("Back to Main Page");
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("FoodCarat Food Court");

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel2.setText("Order History");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Daily");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Weekly");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Monthly");

        tOrderHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tOrderHistory.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tOrderHistory);

        jLabel9.setText("Order Type:");

        lVenFeedbackSection.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lVenFeedbackSection.setText("Vendor Feedback Section");

        lOrderType.setText("orderType");

        lVenRateTitle.setText("Rating:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Order Details");

        lOrderFeedbackSection.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lOrderFeedbackSection.setText("Order Feedback Section");

        cbVendorRating.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please Rate", "1 🌟", "2 🌟", "3 🌟", "4 🌟", "5 🌟" }));

        jLabel4.setText("Vendor Name :");

        lVendorName.setText("vendorName");

        taVendorFeedback.setColumns(20);
        taVendorFeedback.setRows(5);
        jScrollPane3.setViewportView(taVendorFeedback);

        jLabel6.setText("Ordered Item(s) :");

        jLabel7.setText("Total Price Paid:");

        lOrderFeedbackTitle.setText("Order Feedback :");

        taOrderFeedback.setColumns(20);
        taOrderFeedback.setRows(5);
        jScrollPane2.setViewportView(taOrderFeedback);

        bAction.setText("Reorder");

        ltotalPrice.setText("totalPrice");

        tbOrderItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order Item", "Quantity", "Price"
            }
        ));
        tbOrderItem.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tbOrderItem);

        lVenFeedbackTitle.setText("Feedback:");

        lRunnerNameTitle.setText("Runner Name:");

        bFeedback.setText("Save Feedback");
        bFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFeedbackActionPerformed(evt);
            }
        });

        lRunnerName.setText("runnerName");

        jLabel5.setText("Order Status:");

        lRunnerRating.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lRunnerRating.setText("Runner Rating:");

        lOrderStatus.setText("orderStatus");

        cbRunnerRating.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please Rate", "1 🌟", "2 🌟", "3 🌟", "4 🌟", "5 🌟" }));

        jLabel13.setText("Used Points:");

        jLabel15.setText("usedPoint");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jRadioButton1)
                            .addGap(18, 18, 18)
                            .addComponent(jRadioButton2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jRadioButton3))
                        .addComponent(jLabel3)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(bAction, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lVendorName, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel9)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ltotalPrice)
                                            .addComponent(lOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lOrderType)
                                            .addComponent(lRunnerName)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addGap(38, 38, 38)))
                                .addComponent(lRunnerNameTitle)
                                .addComponent(jLabel13)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lOrderFeedbackSection, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lOrderFeedbackTitle, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lVenFeedbackSection, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(lVenRateTitle)
                                    .addGap(18, 18, 18)
                                    .addComponent(cbVendorRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lVenFeedbackTitle, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lRunnerRating)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbRunnerRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(bFeedback, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(123, 123, 123))
                    .addComponent(bBack))
                .addGap(104, 104, 104))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lVendorName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel15))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(ltotalPrice))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lOrderStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(lOrderType))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lRunnerNameTitle)
                            .addComponent(lRunnerName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bAction))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lOrderFeedbackSection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lOrderFeedbackTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lVenFeedbackSection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lVenRateTitle)
                    .addComponent(cbVendorRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lVenFeedbackTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lRunnerRating)
                    .addComponent(cbRunnerRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bFeedback)
                .addGap(205, 205, 205))
        );

        jScrollPane5.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 746, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1055, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBackActionPerformed
        this.dispose();
        customerMain frame = new customerMain();
        frame.setVisible(true);
    }//GEN-LAST:event_bBackActionPerformed

    private void bFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFeedbackActionPerformed
        if (taOrderFeedback != null && !taOrderFeedback.getText().isEmpty() &&
            taVendorFeedback != null && !taVendorFeedback.getText().isEmpty() && 
            !cbVendorRating.getSelectedItem().equals("Please Rate")) {
            
            String runnerRating = null;
            if (cbRunnerRating.isVisible()) {
                if (cbRunnerRating.getSelectedItem() != null && cbRunnerRating.getSelectedItem().equals("Please Rate")) {
                    JOptionPane.showMessageDialog(null, "Please provide a rating for the runner.");
                    return; 
                }
            }

            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to save the feedback? No changes are allowed after saving.");
            if (confirm == JOptionPane.YES_OPTION) {
                int selectedRow = tOrderHistory.getSelectedRow();

                if (selectedRow != -1) {
                    String orderIDString = (String) tOrderHistory.getValueAt(selectedRow, 0);
                    System.out.println("OrderID: " + orderIDString);
                    int orderID = Integer.parseInt(orderIDString); 
                    String orderFeedback = taOrderFeedback.getText();
                    String selectedVendorRating = (String) cbVendorRating.getSelectedItem();
                    String vendorRating = selectedVendorRating.split(" ")[0];
                    String vendorFeedback = taVendorFeedback.getText();

                    Review review = new Review(orderID, orderFeedback, vendorRating, vendorFeedback, runnerRating);
                    review.saveOrderFeedback(); 

                    taOrderFeedback.setEditable(false);
                    bFeedback.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "No order selected.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Check: Please insert the feedback before saving it.");
        }
    }//GEN-LAST:event_bFeedbackActionPerformed

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
    private javax.swing.JButton bFeedback;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbRunnerRating;
    private javax.swing.JComboBox<String> cbVendorRating;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lOrderFeedbackSection;
    private javax.swing.JLabel lOrderFeedbackTitle;
    private javax.swing.JLabel lOrderStatus;
    private javax.swing.JLabel lOrderType;
    private javax.swing.JLabel lRunnerName;
    private javax.swing.JLabel lRunnerNameTitle;
    private javax.swing.JLabel lRunnerRating;
    private javax.swing.JLabel lVenFeedbackSection;
    private javax.swing.JLabel lVenFeedbackTitle;
    private javax.swing.JLabel lVenRateTitle;
    private javax.swing.JLabel lVendorName;
    private javax.swing.JLabel ltotalPrice;
    private javax.swing.JTable tOrderHistory;
    private javax.swing.JTextArea taOrderFeedback;
    private javax.swing.JTextArea taVendorFeedback;
    private javax.swing.JTable tbOrderItem;
    // End of variables declaration//GEN-END:variables
}
