/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Yuna
 */
public class runnerViewTask extends javax.swing.JFrame {
    private String[] orderData;  // Store the order data for later use

    private final String runnerEmail = "runner4@mail.com";
    
    private volatile boolean decisionMade = false;
    private volatile boolean taskAccepted = false;

    // Methods to update the decision flags from button actions
    public void setDecision(boolean made, boolean accepted) {
        this.decisionMade = made;
        this.taskAccepted = accepted;
    }

    // Methods to retrieve the decision flags
    public boolean getDecisionMade() {
        return decisionMade;
    }

    public boolean getTaskAccepted() {
        return taskAccepted;
    }

    /**
     * Creates new form runnerViewTask
     */
    public runnerViewTask() {
        initComponents();
        setLocationRelativeTo(null);
        
        //String runnerEmail = User.getSessionEmail();
        getRunnerTask(runnerEmail);
        
        
        if(orderData == null){
            acceptJB.setEnabled(false);
            declineJB.setEnabled(false);
        }
    }
    
    private void getRunnerTask(String runnerEmail) {
        // Get all orders by calling the getAllOrders method from the Order class
        Order order = new Order();
        List<String[]> allOrders = order.getAllOrders();
        System.out.println("Total orders: " + allOrders.size()); // Debug: Check total orders

        for (String[] orderData : allOrders) {
            System.out.println("Checking order: " + Arrays.toString(orderData));  // Debug: Print each order

            // Match only if the order status is "Assigning runner" and the runner's email matches
            if ("Assigning runner".equalsIgnoreCase(orderData[3]) && runnerEmail.equals(orderData[5])) {
                System.out.println("Found matching order: " + Arrays.toString(orderData));  // Debug: Print matched order
                
                this.orderData = orderData;

                // Populate the fields in the JFrame with the order data
                orderIDTF.setText(orderData[0]);

                // Extract the itemIDs (which are in the format [4;1])
                String itemIDString = orderData[2]; // Format [4;1]
                itemIDString = itemIDString.replaceAll("[\\[\\]]", ""); // Remove square brackets
                String[] itemIDs = itemIDString.split(";"); // Split by semicolon

                // Get vendor info for the first item (assuming all items are from the same vendor)
                String firstItemID = itemIDs[0]; // Take the first item ID
                System.out.println("Processing item ID: " + firstItemID);  // Debug: Print first item ID

                Item item = new Item();
                String[] vendorInfo = item.getVendorInfoByItemID(Integer.parseInt(firstItemID.trim()));  // Get vendor info
                // Print vendorInfo array for debugging (this will show the actual contents of the array)
                System.out.println("Vendor Info: " + Arrays.toString(vendorInfo));

                // Check if vendor info is not null and contains expected data
                if (vendorInfo != null && vendorInfo.length > 2) {
                    String vendorName = vendorInfo[1]; // Vendor's name is at index 2 in the array

                    // Display the vendor name in the vendorNameTF text field
                    vendorNameTF.setText(vendorName);
                } else {
                    System.out.println("Vendor info not found for item ID: " + firstItemID);
                    vendorNameTF.setText("Vendor not found");
                }                
                
                // Get customer email and retrieve their address
                String customerEmail = orderData[4];
                Customer customer = new Customer(customerEmail);
                String customerAddress = customer.getCustomerAddress(customerEmail); // This method retrieves the customer address
                addressTA.setText(customerAddress);
                deFeeTF.setText(orderData[6]);

                // Enable the Accept and Decline buttons
                acceptJB.setEnabled(true);
                declineJB.setEnabled(true);

                break; // Exit after populating the first matching order
            }
        }
    }

    private void clearTaskDetails() {
        orderIDTF.setText("");
        vendorNameTF.setText("");
        addressTA.setText("");
        deFeeTF.setText("");

        acceptJB.setEnabled(false);
        declineJB.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        backJB = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        declineJB = new javax.swing.JButton();
        acceptJB = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        orderIDTF = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        vendorNameTF = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        addressTA = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        deFeeTF = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taskJT = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        orderIDTF2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        statusJCB = new javax.swing.JComboBox<>();
        updateJB = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cusNameTF2 = new javax.swing.JTextField();
        contactNoTF = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        ItemTA = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        addressTA2 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Task");

        backJB.setText("<  Main Menu");
        backJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(backJB)
                .addGap(280, 280, 280)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(backJB))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Task Reception Area", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 18))); // NOI18N

        declineJB.setText("Decline");
        declineJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                declineJBActionPerformed(evt);
            }
        });

        acceptJB.setText("Accept");
        acceptJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptJBActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel2.setText("Order ID:");

        orderIDTF.setEditable(false);
        orderIDTF.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        orderIDTF.setFocusable(false);

        jLabel3.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel3.setText("Store Name:");

        vendorNameTF.setEditable(false);
        vendorNameTF.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        vendorNameTF.setFocusable(false);

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel4.setText("Address:");

        addressTA.setEditable(false);
        addressTA.setColumns(20);
        addressTA.setRows(5);
        addressTA.setFocusable(false);
        addressTA.setPreferredSize(new java.awt.Dimension(300, 100));
        jScrollPane2.setViewportView(addressTA);

        jLabel7.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel7.setText("Delivery Fee:");

        deFeeTF.setEditable(false);
        deFeeTF.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        deFeeTF.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(vendorNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(642, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(orderIDTF, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(106, 106, 106)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(deFeeTF, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(acceptJB, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(declineJB, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(orderIDTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(deFeeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(vendorNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(acceptJB)
                        .addComponent(declineJB)))
                .addGap(14, 14, 14))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Current Task", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 18))); // NOI18N

        taskJT.setAutoCreateColumnsFromModel(false);
        taskJT.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        taskJT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Order ID.", "Name", "Item", "Address", "Contact No.", "Delivery Fee", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        taskJT.setName(""); // NOI18N
        taskJT.setRowHeight(25);
        taskJT.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        taskJT.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        taskJT.setShowGrid(true);
        jScrollPane1.setViewportView(taskJT);
        taskJT.getAccessibleContext().setAccessibleDescription("");

        jLabel5.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel5.setText("Oder ID:");

        orderIDTF2.setEditable(false);
        orderIDTF2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        orderIDTF2.setFocusable(false);
        orderIDTF2.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel6.setText("Oder Status:");

        statusJCB.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        statusJCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pick Up", "Completed" }));

        updateJB.setText("Update");

        jLabel8.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel8.setText("Customer Name:");

        jLabel9.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel9.setText("Item:");

        jLabel10.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel10.setText("Contact No. :");

        jLabel11.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel11.setText("Address:");

        cusNameTF2.setEditable(false);
        cusNameTF2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cusNameTF2.setFocusable(false);

        contactNoTF.setEditable(false);
        contactNoTF.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        contactNoTF.setFocusable(false);

        ItemTA.setEditable(false);
        ItemTA.setColumns(20);
        ItemTA.setLineWrap(true);
        ItemTA.setFocusable(false);
        jScrollPane3.setViewportView(ItemTA);

        addressTA2.setEditable(false);
        addressTA2.setColumns(20);
        addressTA2.setRows(5);
        addressTA2.setFocusable(false);
        jScrollPane4.setViewportView(addressTA2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(orderIDTF2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(296, 296, 296)
                                .addComponent(jLabel9))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(statusJCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(updateJB, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cusNameTF2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(contactNoTF, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(orderIDTF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(cusNameTF2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(contactNoTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateJB)
                    .addComponent(statusJCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backJBActionPerformed
        dispose();
        new runnerMain().setVisible(true);
    }//GEN-LAST:event_backJBActionPerformed

    private void acceptJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptJBActionPerformed
        String orderId = orderIDTF.getText();
        //String runnerEmail = User.getSessionEmail();
        
        setDecision(true, true); // Decision made and task accepted
        new Order().updateStatus(Integer.parseInt(orderId), "Ordered", "runner");
        new Runner().updateRunnerStatus(runnerEmail, "unavailable");
        JOptionPane.showMessageDialog(null, "Task accepted!");
        
        clearTaskDetails();
    }//GEN-LAST:event_acceptJBActionPerformed

    private void declineJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_declineJBActionPerformed
        //String orderId = orderIDTF.getText();
        // Ensure orderData is available before performing action
        if (orderData != null) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to reject this task?", "Reject Task", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Order().assignOrderToRunner(orderData); // Assign order to the next available runner
                setDecision(true, false); // Decision made and task rejected
                JOptionPane.showMessageDialog(null, "Task declined!");
                
                clearTaskDetails();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No order data found.");
        }
    }//GEN-LAST:event_declineJBActionPerformed

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
            java.util.logging.Logger.getLogger(runnerViewTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(runnerViewTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(runnerViewTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(runnerViewTask.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new runnerViewTask().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea ItemTA;
    private javax.swing.JButton acceptJB;
    private javax.swing.JTextArea addressTA;
    private javax.swing.JTextArea addressTA2;
    private javax.swing.JButton backJB;
    private javax.swing.JTextField contactNoTF;
    private javax.swing.JTextField cusNameTF2;
    private javax.swing.JTextField deFeeTF;
    private javax.swing.JButton declineJB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField orderIDTF;
    private javax.swing.JTextField orderIDTF2;
    private javax.swing.JComboBox<String> statusJCB;
    private javax.swing.JTable taskJT;
    private javax.swing.JButton updateJB;
    private javax.swing.JTextField vendorNameTF;
    // End of variables declaration//GEN-END:variables
}