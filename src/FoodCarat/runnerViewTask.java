/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yuna
 */
public class runnerViewTask extends javax.swing.JFrame {
    private String[] orderData;
    private String runnerEmail = User.getSessionEmail();

    public runnerViewTask() {
        initComponents();
        setLocationRelativeTo(null);
        
        getRunnerTask(runnerEmail);
        
        if(orderData == null){
            acceptJB.setEnabled(false);
            declineJB.setEnabled(false);
        }
        
        displayCurrentTask(runnerEmail);
        
        updateJB.setEnabled(false);

        // Add a listener to the table to handle row selection
        currentTaskJT.getSelectionModel().addListSelectionListener(event -> {
            // Ignore updates when the table is still adjusting the selection
            if (!event.getValueIsAdjusting() && currentTaskJT.getSelectedRow() != -1) {
                // Get the selected row index
                int selectedRow = currentTaskJT.getSelectedRow();

                String orderID = currentTaskJT.getValueAt(selectedRow, 0).toString();
                String vendorName = currentTaskJT.getValueAt(selectedRow, 1).toString();
                String customerName = currentTaskJT.getValueAt(selectedRow, 2).toString();
                
                String items = currentTaskJT.getValueAt(selectedRow, 3).toString();
                String[] itemEntries = items.split(",");

                DefaultTableModel model = (DefaultTableModel) itemJT.getModel();
                model.setRowCount(0);

                // Iterate over each item and quantity
                for (String entry : itemEntries) {
                    String[] itemParts = entry.split("\\[");
                    if (itemParts.length == 2) {
                        String itemName = itemParts[0].trim();
                        String quantity = itemParts[1].replace("]", "").trim();

                        model.addRow(new Object[]{itemName, quantity});
                    }
                }
                
                String address = currentTaskJT.getValueAt(selectedRow, 4).toString();
                String contactNo = currentTaskJT.getValueAt(selectedRow, 5).toString();
                String deliveryFee = currentTaskJT.getValueAt(selectedRow, 6).toString();
                String status = currentTaskJT.getValueAt(selectedRow, 7).toString();

                orderIDTF2.setText(orderID);
                vendorNameTF2.setText(vendorName);
                cusNameTF2.setText(customerName);
                addressTA2.setText(address);
                contactNoTF.setText(contactNo);
                deFeeTF2.setText(deliveryFee);
                statusJCB.setSelectedItem(status);
            }
        });
    }
    
    // Get new task
    private void getRunnerTask(String runnerEmail) {
        Order order = new Order();
        List<String[]> allOrders = order.getAllOrders();

        for (String[] orderData : allOrders) {
            if ("assigning runner".equalsIgnoreCase(orderData[3]) && runnerEmail.equals(orderData[5])) {
                
                this.orderData = orderData;

                orderIDTF.setText(orderData[0]);

                // Extract the itemIDs
                String itemIDString = orderData[2];
                itemIDString = itemIDString.replaceAll("[\\[\\]]", "");
                String[] itemIDs = itemIDString.split(";");

                // Get vendor info for the first item
                String firstItemID = itemIDs[0];

                Item item = new Item();
                String[] vendorInfo = item.getVendorInfoByItemID(Integer.parseInt(firstItemID.trim()));
                String vendorName = vendorInfo[1];
                vendorNameTF.setText(vendorName);
                
                // Get customer email and retrieve their address
                String customerEmail = orderData[4];
                Customer customer = new Customer(customerEmail);
                String customerAddress = customer.getCustomerAddress(customerEmail);
                addressTA.setText(customerAddress);
                deFeeTF.setText(orderData[6]);

                acceptJB.setEnabled(true);
                declineJB.setEnabled(true);

                break;
            }
        }
    }

    // For Task reception area
    private void clearTaskDetails() {
        GuiUtility.clearFields(orderIDTF, vendorNameTF, itemJT, addressTA, deFeeTF);

        acceptJB.setEnabled(false);
        declineJB.setEnabled(false);
    }
    
    // Display Current Task in table
    private void displayCurrentTask(String email) {
        Order order = new Order();
        List<String[]> allOrders = order.getAllOrders();

        // Get the table model
        DefaultTableModel model = (DefaultTableModel) currentTaskJT.getModel();

        // Clear the existing table data
        model.setRowCount(0);

        for (String[] orderData : allOrders) {
            if (email.equals(orderData[5]) && !orderData[3].equalsIgnoreCase("assigning runner") && !orderData[3].equalsIgnoreCase("completed")) {

                String orderId = orderData[0];
                
                Item item = new Item();
                String itemIDString = orderData[2].replaceAll("[\\[\\]]", "");
                String[] itemDetails = itemIDString.split("\\|");
                StringBuilder formattedItems = new StringBuilder();

                for (int i = 0; i < itemDetails.length; i++) {
                    String[] parts = itemDetails[i].split(";");
                    int itemID = Integer.parseInt(parts[0]);
                    int quantity = Integer.parseInt(parts[1]);

                    // Get item name from Item class
                    String[] itemData = item.itemData(itemID);
                    String itemName = itemData != null && itemData.length > 1 ? itemData[1] : "Unknown Item";

                    // Format item and quantity
                    if (i > 0) {
                        formattedItems.append(", ");
                    }
                    formattedItems.append(itemName).append("[").append(quantity).append("]");
                }

                String items = formattedItems.toString(); // Combine all items into a single string

                String[] vendorInfo = item.getVendorInfoByItemID(Integer.parseInt(itemDetails[0].split(";")[0].trim()));
                String vendorName = vendorInfo[1];
            
                String[] customerInfo = new User().getUserInfo(orderData[4]);
                String customerName = customerInfo[1];

                String address = new Customer().getCustomerAddress(orderData[4]);
                String contactNumber = customerInfo[5];
                
                String deliveryFee = orderData[7];
                String rawStatus = orderData[3]; // Get the raw status from data
                String status;
                // Map raw status to user-friendly display status
                switch (rawStatus) {
                    case "picked up by runner":
                        status = "Picked Up";
                        break;
                    case "completed":
                        status = "Completed";
                        break;
                    case "ready":
                        status = "Ready";
                        break;
                    default:
                        // Fallback to default capitalization logic
                        status = rawStatus.substring(0, 1).toUpperCase() + orderData[3].substring(1).toLowerCase();
                        break;
                }

                // Add row to the table
                model.addRow(new Object[]{
                        orderId, vendorName, customerName, items, address, contactNumber, deliveryFee, status
                });
            }
        }
        
        currentTaskJT.getSelectionModel().addListSelectionListener(event -> {
            if(!event.getValueIsAdjusting()){
                int selectedRow = currentTaskJT.getSelectedRow();
                if(selectedRow != -1){
                    String currentStatus = model.getValueAt(selectedRow, 7).toString();

                    statusAndUpdate(currentStatus);
                }
            }
        });
    }
    
    // Set the model for the statusJCB based on the current status
    private void statusAndUpdate(String currentStatus) {
        statusJCB.removeAllItems();
        statusJCB.addItem(currentStatus);

        switch (currentStatus.toLowerCase()) {
            case "ready":
                statusJCB.addItem("Picked Up");
                updateJB.setEnabled(true);
                break;

            case "picked up":
                statusJCB.addItem("Completed");
                updateJB.setEnabled(true);
                break;

            default:
                updateJB.setEnabled(false);
                break;
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        backJB = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        orderIDTF = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        vendorNameTF = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        addressTA = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        deFeeTF = new javax.swing.JTextField();
        acceptJB = new javax.swing.JButton();
        declineJB = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        currentTaskJT = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        orderIDTF2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cusNameTF2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        contactNoTF = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        addressTA2 = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        vendorNameTF2 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        deFeeTF2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        statusJCB = new javax.swing.JComboBox<>();
        updateJB = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        itemJT = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

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
        addressTA.setLineWrap(true);
        addressTA.setRows(5);
        addressTA.setToolTipText("");
        addressTA.setFocusable(false);
        addressTA.setPreferredSize(new java.awt.Dimension(300, 100));
        jScrollPane2.setViewportView(addressTA);

        jLabel7.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel7.setText("Delivery Fee:");

        deFeeTF.setEditable(false);
        deFeeTF.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        deFeeTF.setFocusable(false);

        acceptJB.setText("Accept");
        acceptJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptJBActionPerformed(evt);
            }
        });

        declineJB.setText("Decline");
        declineJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                declineJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(orderIDTF, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(137, 137, 137)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deFeeTF, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vendorNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(orderIDTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(deFeeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(vendorNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(35, 35, 35))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(acceptJB)
                        .addComponent(declineJB)))
                .addGap(14, 14, 14))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Current Task", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 18))); // NOI18N

        currentTaskJT.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        currentTaskJT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Order ID.", "Vendor Name", "Name", "Item(s)", "Address", "Contact No.", "Delivery Fee", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        currentTaskJT.setName(""); // NOI18N
        currentTaskJT.setOpaque(false);
        currentTaskJT.setRowHeight(25);
        currentTaskJT.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        currentTaskJT.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        currentTaskJT.setShowGrid(true);
        jScrollPane1.setViewportView(currentTaskJT);
        if (currentTaskJT.getColumnModel().getColumnCount() > 0) {
            currentTaskJT.getColumnModel().getColumn(0).setPreferredWidth(5);
            currentTaskJT.getColumnModel().getColumn(1).setPreferredWidth(40);
            currentTaskJT.getColumnModel().getColumn(2).setPreferredWidth(40);
            currentTaskJT.getColumnModel().getColumn(3).setPreferredWidth(130);
            currentTaskJT.getColumnModel().getColumn(4).setPreferredWidth(180);
            currentTaskJT.getColumnModel().getColumn(5).setPreferredWidth(50);
            currentTaskJT.getColumnModel().getColumn(6).setPreferredWidth(30);
            currentTaskJT.getColumnModel().getColumn(7).setPreferredWidth(15);
        }
        currentTaskJT.getAccessibleContext().setAccessibleName("");
        currentTaskJT.getAccessibleContext().setAccessibleDescription("");

        jLabel5.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel5.setText("Oder ID:");

        orderIDTF2.setEditable(false);
        orderIDTF2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        orderIDTF2.setFocusable(false);
        orderIDTF2.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel8.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel8.setText("Customer Name:");

        cusNameTF2.setEditable(false);
        cusNameTF2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cusNameTF2.setFocusable(false);

        jLabel10.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel10.setText("Contact No. :");

        contactNoTF.setEditable(false);
        contactNoTF.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel11.setText("Address:");

        addressTA2.setEditable(false);
        addressTA2.setColumns(20);
        addressTA2.setLineWrap(true);
        addressTA2.setRows(5);
        jScrollPane4.setViewportView(addressTA2);

        jLabel13.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel13.setText("Vendor Name:");

        vendorNameTF2.setEditable(false);
        vendorNameTF2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        vendorNameTF2.setFocusable(false);

        jLabel12.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel12.setText("Delivery Fee:");

        deFeeTF2.setEditable(false);
        deFeeTF2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        deFeeTF2.setFocusable(false);

        jLabel9.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel9.setText("Item:");

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel6.setText("Oder Status:");

        statusJCB.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        statusJCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Row" }));

        updateJB.setText("Update");
        updateJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateJBActionPerformed(evt);
            }
        });

        itemJT.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        itemJT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item(s)", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        itemJT.setRequestFocusEnabled(false);
        itemJT.setRowHeight(25);
        itemJT.setRowSelectionAllowed(false);
        itemJT.setShowGrid(true);
        jScrollPane3.setViewportView(itemJT);
        if (itemJT.getColumnModel().getColumnCount() > 0) {
            itemJT.getColumnModel().getColumn(0).setPreferredWidth(200);
            itemJT.getColumnModel().getColumn(1).setPreferredWidth(10);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel12)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel5)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(orderIDTF2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel9)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel6)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(statusJCB, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(updateJB, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel10)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(contactNoTF, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel8)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cusNameTF2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(173, 173, 173)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vendorNameTF2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(74, 74, 74)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(deFeeTF2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(214, 214, 214))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(orderIDTF2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(cusNameTF2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(contactNoTF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deFeeTF2)
                    .addComponent(vendorNameTF2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addGap(39, 39, 39)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateJB)
                    .addComponent(statusJCB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backJBActionPerformed
        dispose();
        new runnerMain().setVisible(true);
    }//GEN-LAST:event_backJBActionPerformed

    private void acceptJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptJBActionPerformed
        String orderId = orderIDTF.getText();
        
        new Order().updateStatus(Integer.parseInt(orderId), "Ordered", "runner");
        new Runner().updateRunnerStatus(runnerEmail, "unavailable");
        JOptionPane.showMessageDialog(null, "Task accepted!");
        
        clearTaskDetails();
        displayCurrentTask(runnerEmail);
    }//GEN-LAST:event_acceptJBActionPerformed

    private void declineJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_declineJBActionPerformed
        if (orderData != null) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to reject this task?", "Reject Task", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Order().assignOrderToRunner(orderData, runnerEmail); // Assign order to the next available runner
                JOptionPane.showMessageDialog(null, "Task declined!");
                
                clearTaskDetails();
            }
        }
    }//GEN-LAST:event_declineJBActionPerformed

    private void updateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateJBActionPerformed
        int selectedRow = currentTaskJT.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = Integer.parseInt(currentTaskJT.getValueAt(selectedRow, 0).toString());
            String currentStatus = currentTaskJT.getValueAt(selectedRow, 7).toString();

            String newStatus = (String) statusJCB.getSelectedItem();

            if (newStatus != null && !newStatus.equalsIgnoreCase(currentStatus)) {
                String fileStatus;
                    switch (newStatus) {
                        case "Picked Up":
                            fileStatus = "picked up by runner";
                            break;
                        case "Completed":
                            fileStatus = "completed";
                            break;
                        default:
                            fileStatus = newStatus.toLowerCase(); // Use as-is for other statuses
                            break;
                    }
                // Update status
                Order order = new Order();
                order.updateStatus(orderId, fileStatus, "runner");

                JOptionPane.showMessageDialog(null, "Status Updated!");
                
                if ("completed".equals(fileStatus)){
                    new Runner().updateRunnerStatus(runnerEmail, "available");
                }

                // Refresh and Reset
                displayCurrentTask(runnerEmail);
                GuiUtility.clearFields(orderIDTF2, cusNameTF2, vendorNameTF2, itemJT, addressTA2, contactNoTF, deFeeTF2);
                statusJCB.removeAllItems();
                statusJCB.addItem("Select Row");
                updateJB.setEnabled(false);
            }
        }
    }//GEN-LAST:event_updateJBActionPerformed

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
    private javax.swing.JButton acceptJB;
    private javax.swing.JTextArea addressTA;
    private javax.swing.JTextArea addressTA2;
    private javax.swing.JButton backJB;
    private javax.swing.JTextField contactNoTF;
    private javax.swing.JTable currentTaskJT;
    private javax.swing.JTextField cusNameTF2;
    private javax.swing.JTextField deFeeTF;
    private javax.swing.JTextField deFeeTF2;
    private javax.swing.JButton declineJB;
    private javax.swing.JTable itemJT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JButton updateJB;
    private javax.swing.JTextField vendorNameTF;
    private javax.swing.JTextField vendorNameTF2;
    // End of variables declaration//GEN-END:variables
}