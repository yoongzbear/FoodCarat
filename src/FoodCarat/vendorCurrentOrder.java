/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mastu
 */
public class vendorCurrentOrder extends javax.swing.JFrame {

    private String email = User.getSessionEmail();

    /**
     * Creates new form vendorCurrentOrder
     */
    public vendorCurrentOrder() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186,85,211)); //setting background color of frame
        setLocationRelativeTo(null);
        
        //display new order and current orders
        displayNewOrder();
        displayCurrentOrder();
        
        GuiUtility.setPlaceholder(searchOrderIDTxt, "Search Order ID"); //set placeholder for search
    }
    
    //display new order
    public void displayNewOrder() {
        String[] newOrderInfo = new Order().getNewOrder(email);
        //if newOrderInfo is null, display "no new orders"
        if (newOrderInfo == null) {
            orderMessageLabel.setText("No new orders.");
            //reset new order section 
            incomingIDLabel.setText("");
            incomingEmailTxt.setText("");
            incomingMethodTxt.setText("");
            incomingTotalPriceTxt.setText("RM0.00");
            DefaultTableModel model = (DefaultTableModel) incomingItemTable.getModel();
            model.setRowCount(0);
        } else {
            //display information in the new order section
            orderMessageLabel.setText("You have a new order, please review it.");
            incomingIDLabel.setText(newOrderInfo[0].trim());
            incomingEmailTxt.setText(newOrderInfo[4].trim());
            incomingMethodTxt.setText(newOrderInfo[1].trim());
            incomingTotalPriceTxt.setText("RM" + newOrderInfo[10].trim());
            
            //display table item
            String orderItems = newOrderInfo[2].trim();
            //remove square brackets and split the items by "|"
            String[] itemDetails = orderItems.replace("[", "").replace("]", "").split("\\|");
            DefaultTableModel model = (DefaultTableModel) incomingItemTable.getModel();
            model.setRowCount(0);

            for (String detail : itemDetails) {
                String[] parts = detail.split(";");
                int itemID = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);

                //retrieve item data through Item class
                String[] itemData = new Item().itemData(itemID);
                if (itemData != null && itemData.length > 3) {
                    String itemName = itemData[1];
                    double price = Double.parseDouble(itemData[3]);

                    model.addRow(new Object[]{itemName, quantity});
                } else {
                    model.addRow(new Object[]{"Unknown item (ID: " + itemID + ")", quantity});
                }
            }
        }
    }
        
    //display all current orders 
    public void displayCurrentOrder() {
        //create lists for orders with ordered, in kitchen, and ready statuses
        Order orders = new Order();
        List<String[]> ordered = orders.getOrderByStatus(email, "ordered");
        List<String[]> inKitchen = orders.getOrderByStatus(email, "in kitchen");
        List<String[]> ready = orders.getOrderByStatus(email, "ready");

        DefaultTableModel model = (DefaultTableModel) currentOrderTable.getModel();
        int index = 1;
        model.setRowCount(0);

        //iterate through each list and add into table model
        for (String[] orderData : ordered) {
            String orderItems = orderData[2].trim();
            String updatedOrderItems = new Item().replaceItemIDsWithNames(orderItems);
            String orderStatus = orderData[3];
            orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
            model.addRow(new Object[]{index++, orderData[0], updatedOrderItems, orderData[1], orderStatus});
        }
        
        for (String[] orderData : inKitchen) {
            String orderItems = orderData[2].trim();
            String updatedOrderItems = new Item().replaceItemIDsWithNames(orderItems);
            String orderStatus = orderData[3];
            orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
            model.addRow(new Object[]{index++, orderData[0], updatedOrderItems, orderData[1], orderStatus});
        }
        
        for (String[] orderData : ready) {
            String orderItems = orderData[2].trim();
            String updatedOrderItems = new Item().replaceItemIDsWithNames(orderItems);
            String orderStatus = orderData[3];
            orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
            model.addRow(new Object[]{index++, orderData[0], updatedOrderItems, orderData[1], orderStatus});
        }        
    }
    
    //display according to status combo box
    public void displayCurrentOrder(String filter) {
        DefaultTableModel model = (DefaultTableModel) currentOrderTable.getModel();
        int index = 1;
        model.setRowCount(0);
        Order orders = new Order();

        if (filter.equalsIgnoreCase("Ordered")) {
            List<String[]> orderList = orders.getOrderByStatus(email, "ordered");
            for (String[] orderData : orderList) {
                String orderItems = orderData[2].trim();
                String updatedOrderItems = new Item().replaceItemIDsWithNames(orderItems); //replace item ID with item names
                String orderStatus = orderData[3];
                orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
                model.addRow(new Object[]{index++, orderData[0], updatedOrderItems, orderData[1], orderStatus});
            }
        } else if (filter.equalsIgnoreCase("In kitchen")) {
            List<String[]> orderList = orders.getOrderByStatus(email, "in kitchen");
            for (String[] orderData : orderList) {
                String orderItems = orderData[2].trim();
                String updatedOrderItems = new Item().replaceItemIDsWithNames(orderItems);
                String orderStatus = orderData[3];
                orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
                model.addRow(new Object[]{index++, orderData[0], updatedOrderItems, orderData[1], orderStatus});
            }
        } else if (filter.equalsIgnoreCase("Ready")) {
            List<String[]> orderList = orders.getOrderByStatus(email, "ready");
            for (String[] orderData : orderList) {
                String orderItems = orderData[2].trim();
                String updatedOrderItems = new Item().replaceItemIDsWithNames(orderItems);
                String orderStatus = orderData[3];
                orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
                model.addRow(new Object[]{index++, orderData[0], updatedOrderItems, orderData[1], orderStatus});
            }
        }
    }
    
    //display searched id in table
    public void displaySearch(int orderID) {
        DefaultTableModel model = (DefaultTableModel) currentOrderTable.getModel();
        int index = 1;
        model.setRowCount(0);
        String[] searchedOrder = new Order().getOrder(orderID);
        if (searchedOrder == null) {
            JOptionPane.showMessageDialog(null, "Order ID cannot be found", "Alert", JOptionPane.WARNING_MESSAGE);
            displayCurrentOrder();
            GuiUtility.setPlaceholder(searchOrderIDTxt, "Search Order ID");
        } else {
            String orderItems = searchedOrder[2].trim();
            String updatedOrderItems = new Item().replaceItemIDsWithNames(orderItems);
            String orderStatus = searchedOrder[3];
            orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
            model.addRow(new Object[]{index++, searchedOrder[0], updatedOrderItems, searchedOrder[1], orderStatus});
        }
    }
    
    //display selected order
    public void displayOrderDetails(int orderID) {
        DecimalFormat df = new DecimalFormat("0.00");
        String[] orderDetails = new Order().getOrder(orderID);
        statusIdTxt.setText(orderDetails[0].trim());
        String orderMethod = orderDetails[1].trim();
        statusMethodTxt.setText(orderMethod);

        //display table item
        String orderItems = orderDetails[2].trim();
        //remove square brackets and split the items by "|"
        String[] itemDetails = orderItems.replace("[", "").replace("]", "").split("\\|");
        DefaultTableModel model = (DefaultTableModel) updateOrderTable.getModel();
        model.setRowCount(0);

        for (String detail : itemDetails) {
            String[] parts = detail.split(";");
            int itemID = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);

            //retrieve item data through Item class
            String[] itemData = new Item().itemData(itemID);
            if (itemData != null && itemData.length > 3) {
                String itemName = itemData[1];
                double price = Double.parseDouble(itemData[3]);

                model.addRow(new Object[]{itemName, quantity});
            } else {
                model.addRow(new Object[]{"Unknown item (ID: " + itemID + ")", quantity});
            }
        }

        //get current status and display next status 
        String currentStatus = orderDetails[3].trim();
        currentStatus = currentStatus.substring(0, 1).toUpperCase() + currentStatus.substring(1).toLowerCase();
        currentStatusTxt.setText(currentStatus);
        if (orderMethod.equalsIgnoreCase("Delivery")) {
            if (currentStatus.equalsIgnoreCase("ordered")) {
                nextStatusTxt.setText("In kitchen");
            } else if (currentStatus.equalsIgnoreCase("in kitchen")) {
                nextStatusTxt.setText("Ready");
            } else if (currentStatus.equalsIgnoreCase("ready")) {
                nextStatusTxt.setText("Picked up by runner");
            }
        } else {
            if (currentStatus.equalsIgnoreCase("ordered")) {
                nextStatusTxt.setText("In kitchen");
            } else if (currentStatus.equalsIgnoreCase("in kitchen")) {
                nextStatusTxt.setText("Ready");
            } else if (currentStatus.equalsIgnoreCase("ready")) {
                nextStatusTxt.setText("Completed");
            }
        }
    }
    
    public void refundOrder(int orderID) {
        Order order = new Order();
        //get order info - customer email
        String[] cancelOrder = order.getOrder(orderID);
        String cusEmail = cancelOrder[4]; //index 4
        try {
            order.updateStatus(orderID, "cancelled", "vendor");
            order.refund(orderID, cusEmail);
            JOptionPane.showMessageDialog(null, "Order " + orderID + " is rejected.");            
        } catch (IOException ex) {
            Logger.getLogger(customerOrderHistory.class.getName()).log(Level.SEVERE, null, ex);
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

        jLabel1 = new javax.swing.JLabel();
        menuBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        incomingItemTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        acceptBtn = new javax.swing.JButton();
        rejectBtn = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        incomingIDLabel = new javax.swing.JLabel();
        incomingEmailTxt = new javax.swing.JLabel();
        incomingMethodTxt = new javax.swing.JLabel();
        incomingTotalPriceTxt = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        currentOrderTable = new javax.swing.JTable();
        selectBtn = new javax.swing.JButton();
        filterStatusBox = new javax.swing.JComboBox<>();
        searchOrderIDTxt = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        updateOrderBtn = new javax.swing.JButton();
        cancelUpdateBtn = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        updateOrderTable = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        statusIdTxt = new javax.swing.JLabel();
        statusMethodTxt = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        currentStatusTxt = new javax.swing.JLabel();
        nextStatusTxt = new javax.swing.JLabel();
        orderMessageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Current Orders");

        menuBtn.setText("Main Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel2.setText("Incoming Order");

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel6.setText("Ordered Items:");

        incomingItemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Quantity"
            }
        ));
        jScrollPane4.setViewportView(incomingItemTable);
        if (incomingItemTable.getColumnModel().getColumnCount() > 0) {
            incomingItemTable.getColumnModel().getColumn(0).setResizable(false);
            incomingItemTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            incomingItemTable.getColumnModel().getColumn(1).setResizable(false);
            incomingItemTable.getColumnModel().getColumn(1).setPreferredWidth(20);
        }

        jLabel5.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel5.setText("Customer Email:");

        jLabel9.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel9.setText("Order Method:");

        acceptBtn.setText("Accept");
        acceptBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptBtnActionPerformed(evt);
            }
        });

        rejectBtn.setText("Reject");
        rejectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rejectBtnActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel8.setText("Total Paid:");

        jLabel7.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel7.setText("Order ID:");

        incomingIDLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        incomingIDLabel.setText("ID");

        incomingEmailTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        incomingEmailTxt.setText("Email");

        incomingMethodTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        incomingMethodTxt.setText("Method");

        incomingTotalPriceTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        incomingTotalPriceTxt.setText("RM");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(60, 60, 60)
                            .addComponent(incomingTotalPriceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(acceptBtn)
                            .addGap(94, 94, 94)
                            .addComponent(rejectBtn)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(incomingIDLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(incomingEmailTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(incomingMethodTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(incomingIDLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(incomingEmailTxt))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(incomingMethodTxt))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(incomingTotalPriceTxt))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(acceptBtn)
                    .addComponent(rejectBtn))
                .addGap(28, 28, 28))
        );

        jLabel3.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel3.setText("Current Orders");

        currentOrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. ", "Order ID", "Items", "Method", "Status"
            }
        ));
        jScrollPane5.setViewportView(currentOrderTable);
        if (currentOrderTable.getColumnModel().getColumnCount() > 0) {
            currentOrderTable.getColumnModel().getColumn(0).setResizable(false);
            currentOrderTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            currentOrderTable.getColumnModel().getColumn(1).setResizable(false);
            currentOrderTable.getColumnModel().getColumn(1).setPreferredWidth(20);
            currentOrderTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            currentOrderTable.getColumnModel().getColumn(3).setResizable(false);
            currentOrderTable.getColumnModel().getColumn(3).setPreferredWidth(40);
            currentOrderTable.getColumnModel().getColumn(4).setResizable(false);
            currentOrderTable.getColumnModel().getColumn(4).setPreferredWidth(40);
        }

        selectBtn.setText("Select Order");
        selectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBtnActionPerformed(evt);
            }
        });

        filterStatusBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Status", "Ordered", "In Kitchen", "Ready" }));
        filterStatusBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterStatusBoxActionPerformed(evt);
            }
        });

        searchOrderIDTxt.setText("Search Order ID");

        searchBtn.setText("Search");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(selectBtn)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(filterStatusBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchOrderIDTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(searchBtn))
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filterStatusBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchOrderIDTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(selectBtn)
                .addGap(14, 14, 14))
        );

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel4.setText("Update Order Status");

        jLabel11.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel11.setText("Order Method:");

        updateOrderBtn.setText("Update Status");
        updateOrderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateOrderBtnActionPerformed(evt);
            }
        });

        cancelUpdateBtn.setText("Cancel Update");
        cancelUpdateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelUpdateBtnActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel13.setText("Order ID:");

        jLabel14.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel14.setText("Ordered Items:");

        updateOrderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Quantity"
            }
        ));
        jScrollPane6.setViewportView(updateOrderTable);
        if (updateOrderTable.getColumnModel().getColumnCount() > 0) {
            updateOrderTable.getColumnModel().getColumn(0).setResizable(false);
            updateOrderTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            updateOrderTable.getColumnModel().getColumn(1).setResizable(false);
            updateOrderTable.getColumnModel().getColumn(1).setPreferredWidth(20);
        }

        jLabel12.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel12.setText("Current Status:");

        statusIdTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        statusMethodTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel15.setText("Next Status:");

        currentStatusTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        nextStatusTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel13))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(statusMethodTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(statusIdTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(207, 207, 207)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel12))
                                .addGap(18, 18, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(updateOrderBtn)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(currentStatusTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextStatusTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addComponent(cancelUpdateBtn)))))
                .addGap(35, 35, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(statusIdTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12)))
                    .addComponent(currentStatusTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel11)
                                .addComponent(jLabel15)
                                .addComponent(nextStatusTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(statusMethodTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updateOrderBtn)
                            .addComponent(cancelUpdateBtn))
                        .addGap(109, 109, 109)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        orderMessageLabel.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        orderMessageLabel.setText("Order Message");
        orderMessageLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(orderMessageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(339, 339, 339)
                        .addComponent(menuBtn)))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(menuBtn)
                        .addGap(19, 19, 19))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(orderMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void selectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBtnActionPerformed
        int selectedRow = currentOrderTable.getSelectedRow();
        //validation to choose a row 
        if (selectedRow >= 0) {
            Object id = currentOrderTable.getModel().getValueAt(selectedRow, 1);
            try {
                int selectID = Integer.parseInt(id.toString()); //convert the object to string and parse as int
                displayOrderDetails(selectID);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "The selected ID is not a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else { //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to view details.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_selectBtnActionPerformed

    private void updateOrderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateOrderBtnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to update the order's status?", "Update Order Status", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int orderID = Integer.parseInt(statusIdTxt.getText());
            String newStatus = nextStatusTxt.getText().toLowerCase();
            new Order().updateStatus(orderID, newStatus, "vendor");
            JOptionPane.showMessageDialog(null, "Order " + orderID + "'s status updated successfully!");
            displayCurrentOrder();

            //reset order details section
            GuiUtility.clearFields(statusIdTxt, statusMethodTxt, currentStatusTxt, nextStatusTxt);
            DefaultTableModel model = (DefaultTableModel) updateOrderTable.getModel();
            model.setRowCount(0);
        }
    }//GEN-LAST:event_updateOrderBtnActionPerformed

    private void acceptBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptBtnActionPerformed
        //update order status to Ordered or Assigning runner
        String orderID = incomingIDLabel.getText();
        String orderMethod = incomingMethodTxt.getText();
        Order order = new Order();
        
        if (orderMethod.equalsIgnoreCase("delivery")) {
            order.updateStatus(Integer.parseInt(orderID), "assigning runner", "vendor");
        } else {
            order.updateStatus(Integer.parseInt(orderID), "ordered", "vendor");
        }
        displayNewOrder(); //display next new order 
        displayCurrentOrder(); //refresh current orders table
        JOptionPane.showMessageDialog(null, "Order " + orderID + " is accepted.");
    }//GEN-LAST:event_acceptBtnActionPerformed

    private void rejectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rejectBtnActionPerformed
        //update order status to cancelled
        int orderID = Integer.parseInt(incomingIDLabel.getText());
        
        //may need to add cancellation reason
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to reject this order?", "Reject Order", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            refundOrder(orderID);
            displayNewOrder(); //display next new order
        }
    }//GEN-LAST:event_rejectBtnActionPerformed

    private void filterStatusBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterStatusBoxActionPerformed
        String filter = filterStatusBox.getSelectedItem().toString();
        if (filter.equals("Select Status")) {
            displayCurrentOrder(); //display all current orders
        } else {
            displayCurrentOrder(filter); //display based on filter
        }        
    }//GEN-LAST:event_filterStatusBoxActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        String search = searchOrderIDTxt.getText();
        if (search.equals("Search Order ID")) {
            JOptionPane.showMessageDialog(null, "Please enter your search.", "Alert", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                int searchID = Integer.parseInt(search.trim());
                displaySearch(searchID);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter numerical value for the Order ID.", "Alert", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_searchBtnActionPerformed

    private void cancelUpdateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelUpdateBtnActionPerformed
        //reset order details section
        GuiUtility.clearFields(statusIdTxt, statusMethodTxt, currentStatusTxt, nextStatusTxt);
        DefaultTableModel model = (DefaultTableModel) updateOrderTable.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_cancelUpdateBtnActionPerformed

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
            java.util.logging.Logger.getLogger(vendorCurrentOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(vendorCurrentOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(vendorCurrentOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(vendorCurrentOrder.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new vendorCurrentOrder().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptBtn;
    private javax.swing.JButton cancelUpdateBtn;
    private javax.swing.JTable currentOrderTable;
    private javax.swing.JLabel currentStatusTxt;
    private javax.swing.JComboBox<String> filterStatusBox;
    private javax.swing.JLabel incomingEmailTxt;
    private javax.swing.JLabel incomingIDLabel;
    private javax.swing.JTable incomingItemTable;
    private javax.swing.JLabel incomingMethodTxt;
    private javax.swing.JLabel incomingTotalPriceTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel nextStatusTxt;
    private javax.swing.JLabel orderMessageLabel;
    private javax.swing.JButton rejectBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchOrderIDTxt;
    private javax.swing.JButton selectBtn;
    private javax.swing.JLabel statusIdTxt;
    private javax.swing.JLabel statusMethodTxt;
    private javax.swing.JButton updateOrderBtn;
    private javax.swing.JTable updateOrderTable;
    // End of variables declaration//GEN-END:variables
}
