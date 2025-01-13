/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mastu
 */
public class vendorOrderHistory extends javax.swing.JFrame {

    /**
     * Creates new form vendorOrderHistory
     */       
    //change to userSession 
    private String email = "chagee@mail.com";
//    private String email = User.getSessionEmail();
    Vendor vendor = new Vendor(email);
    
    public vendorOrderHistory() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186,85,211)); //setting background color of frame
        
        //test if it works
        displayVendorOrder();
        
        //set the placeholder for search box
        setPlaceholder(searchTxt, "Enter your search");
        
        //hide month and year combo box for table
        monthTableBox.setVisible(false);
        yearTableBox.setVisible(false);
    }    
    
    //display all orders to vendor
    public void displayVendorOrder() {
        //display on table
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        int index = 1;
        model.setRowCount(0);
        //access vendor orders through vendor class
        Vendor vendor = new Vendor(email);
        List<String[]> allOrders = vendor.getVendorOrders(email);
        for (String[] orderData : allOrders) {
            Item item = new Item();            
            //1,Take away,[1;1|2;1],Ordered,customerEmail,NULL,NULL,20.00,2025-01-01,27.80
            String orderID = orderData[0];
            String orderMethod = orderData[1];
            String orderItems = orderData[2];
            String orderStatus = orderData[3];
            String customerEmail = orderData[4];
            String orderTotal = orderData[8];

            String updatedOrderItems = replaceItemIDsWithNames(orderItems, item);

            model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
        }
    }
    
    //display order details
    public void displayVendorOrder(String orderID) {
        DecimalFormat df = new DecimalFormat("0.00");
        Order order = new Order();
                
        String[] details = order.getOrder(orderID);  
        
        //get items, price, and quantity to display in table
        Item item = new Item();
        //get item data from Item class to get price
        String orderItems = details[2].trim();
        //remove square brackets and split the items by "|"
        String[] itemDetails = orderItems.replace("[", "").replace("]", "").split("\\|");

        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.setRowCount(0);

        for (String detail : itemDetails) {
            String[] parts = detail.split(";");
            String itemID = parts[0]; 
            int quantity = Integer.parseInt(parts[1]); 

            //retrieve item data through Item class
            String[] itemData = item.itemData(itemID);
            if (itemData != null && itemData.length > 3) {
                String itemName = itemData[1]; 
                double price = Double.parseDouble(itemData[3]); 

                model.addRow(new Object[]{itemName, df.format(price), quantity});
            } else {
                model.addRow(new Object[]{"Unknown item (ID: " + itemID + ")", 0.0, quantity});
            }
        }

        idLabel.setText(details[0].trim());
        methodLabel.setText(details[1].trim());
        statusLabel.setText(details[3].trim());
        emailLabel.setText(details[4].trim());
        totalPriceLabel.setText("RM"+details[8].trim());
        
        //cancellation reason - need to connect with the get cancellation reason based on cancel id
        //orderID,orderMethod,[itemID;quantity],orderStatus,customerEmail,runnerEmail,cancelReason,deliveryFee,totalPaid,date
        if (details[6].trim().equals("NULL")) { //if status == cancelled
            reasonLabel.setVisible(false);
            cancelReasonLabel.setVisible(false);
        } else {
            cancelReasonLabel.setText(details[5].trim());
        }       
        
        //view if got feedback for the order
//        if (details[6].trim().equals("NULL")) { //check in review if got order id && type == order
//            feedbackLabel.setVisible(false);
//            feedbackTxtArea.setVisible(false);
//        } else {
//            feedbackTxtArea.setText(details[5].trim()); //display the feedback from the review
//        } 
    }
    
    //display vendor order based on search 
    public void displayOrderSearch(String search) {
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        Vendor vendor = new Vendor(email);
        List<String[]> allVendorOrders = vendor.getVendorOrders(email);
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);
        
        for (String[] orderData : allVendorOrders) {
            Item item = new Item();            
            //1,Take away,[1;1|2;1],Ordered,customerEmail,NULL,NULL,27.80
            String orderID = orderData[0];
            String orderMethod = orderData[1];
            String orderItems = orderData[2];
            String orderStatus = orderData[3];
            String customerEmail = orderData[4];
            String orderTotal = orderData[7];

            String updatedOrderItems = replaceItemIDsWithNames(orderItems, item);

            //check if item type matches the filter
            boolean isFound = false;
            if (updatedOrderItems.toLowerCase().contains(search.toLowerCase())) {
                //search email, date, method??, items???
                model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
                index++;
            }
        }
    }

    //have filter to show monthly, or yearly
    public void displayOrderTimeRange(String[] timeRange) {
        //get time range []
        //if monthly --> [month, year]
        
        //if year --> [null, year]
    }
    
    //filter to show daily orders including completed orders
    public void displayOrderTimeRange() {
        //display orders matching the given date
    }
    
    
    //helper method to change item id to item name
    private String replaceItemIDsWithNames(String orderItems, Item item) {
        //remove square brackets and split the items by "|"
        String[] itemDetails = orderItems.replace("[", "").replace("]", "").split("\\|");
        StringBuilder updatedItems = new StringBuilder();

        //replace itemID with item name
        for (int i = 0; i < itemDetails.length; i++) {
            String detail = itemDetails[i];
            String[] parts = detail.split(";");
            String itemID = parts[0]; // itemID
            int quantity = Integer.parseInt(parts[1]); // quantity

            String[] itemData = item.itemData(itemID);
            String itemName = itemData != null && itemData.length > 1 ? itemData[1] : "Unknown Item";

            //update item format [itemName;quantity]
            if (i > 0) {
                updatedItems.append(", ");
            }
            updatedItems.append(itemName);
        }

        return updatedItems.toString();
    }
    
    //helper method to adjust search box
    public void setPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);  

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                //if the text field contains the placeholder, clear it when the clicked
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);  
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                //if the text field is empty, show the placeholder again
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
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
        orderTable = new javax.swing.JTable();
        viewBtn = new javax.swing.JButton();
        timeRangeBox = new javax.swing.JComboBox<>();
        searchTxt = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        idLabel1 = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        methodLabel = new javax.swing.JLabel();
        feedbackLabel = new javax.swing.JLabel();
        reasonLabel = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        cancelReasonLabel = new javax.swing.JLabel();
        totalPriceLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        feedbackTxtArea = new javax.swing.JTextArea();
        rangeBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        chartMonthBox = new javax.swing.JComboBox<>();
        monthChartBtn = new javax.swing.JButton();
        chartWeekRange = new javax.swing.JLabel();
        monthTableBox = new javax.swing.JComboBox<>();
        yearTableBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Order History");

        menuBtn.setText("Main Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        orderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Order ID", "Customer Email", "Order Method", "Ordered Item(s)", "Order Status"
            }
        ));
        jScrollPane1.setViewportView(orderTable);
        if (orderTable.getColumnModel().getColumnCount() > 0) {
            orderTable.getColumnModel().getColumn(0).setResizable(false);
            orderTable.getColumnModel().getColumn(0).setPreferredWidth(10);
            orderTable.getColumnModel().getColumn(1).setResizable(false);
            orderTable.getColumnModel().getColumn(1).setPreferredWidth(10);
            orderTable.getColumnModel().getColumn(2).setPreferredWidth(60);
            orderTable.getColumnModel().getColumn(3).setResizable(false);
            orderTable.getColumnModel().getColumn(3).setPreferredWidth(50);
            orderTable.getColumnModel().getColumn(4).setPreferredWidth(150);
            orderTable.getColumnModel().getColumn(5).setResizable(false);
            orderTable.getColumnModel().getColumn(5).setPreferredWidth(40);
        }

        viewBtn.setText("View Details");
        viewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewBtnActionPerformed(evt);
            }
        });

        timeRangeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Time Range", "Today", "Monthly", "Yearly" }));
        timeRangeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeRangeBoxActionPerformed(evt);
            }
        });

        searchTxt.setText("Enter your search");

        searchBtn.setText("Search");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel9.setText("Order Details");

        jLabel10.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel10.setText("Ordered Items:");

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Price (RM)", "Quantity"
            }
        ));
        jScrollPane5.setViewportView(itemTable);

        jLabel12.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel12.setText("Date:");

        jLabel13.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel13.setText("Order ID:");

        jLabel14.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel14.setText("Customer Email:");

        jLabel15.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel15.setText("Order Method:");

        idLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        idLabel.setText("ID");

        idLabel1.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        idLabel1.setText("Date");

        emailLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        emailLabel.setText("Email");

        methodLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        methodLabel.setText("Method");

        feedbackLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        feedbackLabel.setText("Order Feedback:");

        reasonLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        reasonLabel.setText("Cancellation Reason:");

        jLabel18.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel18.setText("Total Price:");

        cancelReasonLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        cancelReasonLabel.setText("Reason");

        totalPriceLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        totalPriceLabel.setText("RM");

        statusLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        statusLabel.setText("Status");

        jLabel16.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel16.setText("Status:");

        feedbackTxtArea.setColumns(20);
        feedbackTxtArea.setRows(5);
        jScrollPane2.setViewportView(feedbackTxtArea);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(methodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(idLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(reasonLabel)
                                    .addComponent(jLabel18))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(totalPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cancelReasonLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(29, 29, 29))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(feedbackLabel)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12)
                    .addComponent(idLabel)
                    .addComponent(idLabel1)
                    .addComponent(jLabel16)
                    .addComponent(statusLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(totalPriceLabel))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(emailLabel)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(methodLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(reasonLabel)
                        .addComponent(cancelReasonLabel)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(feedbackLabel)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        rangeBtn.setText("View Range");
        rangeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rangeBtnActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel2.setText("Order Summary Report");

        jLabel3.setText("pie chart");

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel4.setText("Weekly");

        jLabel5.setText("pie chart");

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel6.setText("Monthly");

        chartMonthBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Month" }));

        monthChartBtn.setText("Select");

        chartWeekRange.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange.setText("XX/XX/XXXX-XX/XX/XXXX");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 242, Short.MAX_VALUE)
                                .addComponent(chartMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(monthChartBtn))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chartWeekRange, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(chartWeekRange))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(chartMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthChartBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        monthTableBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "November", "December" }));

        yearTableBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Year", "Use model lor", "maybe max 2 years?" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(viewBtn)
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(timeRangeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(monthTableBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(yearTableBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(rangeBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(searchBtn)))
                        .addGap(48, 48, 48)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(443, 443, 443)
                        .addComponent(menuBtn)))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(menuBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timeRangeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(searchBtn))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(monthTableBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(rangeBtn)
                                .addComponent(yearTableBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(viewBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void viewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewBtnActionPerformed
        //if got reason ID, display the label adn text field for reason cancelled
        //display selected row of item in the table
        int selectedRow = orderTable.getSelectedRow();

        //have validation to "choose an item in the table"
        if (selectedRow >= 0) {
            Object id = orderTable.getModel().getValueAt(selectedRow, 1);
            displayVendorOrder(id.toString());
        } else {
            //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to view details.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_viewBtnActionPerformed

    private void rangeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rangeBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rangeBtnActionPerformed

    private void timeRangeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeRangeBoxActionPerformed
        // TODO add your handling code here:
        if (timeRangeBox.getSelectedItem().equals("Daily")) {
            //display calendar to choose date 
            //displayOrderTimeRange(date);
        }
        
        else if (timeRangeBox.getSelectedItem().equals("Monthly")) {
            monthTableBox.setVisible(true);
            yearTableBox.setVisible(true);
        } else if (timeRangeBox.getSelectedItem().equals("Yearly")) {
            monthTableBox.setVisible(false);
            yearTableBox.setVisible(true);
        } 
    }//GEN-LAST:event_timeRangeBoxActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        // TODO add your handling code here:
        String searchOrder = searchTxt.getText();
        displayOrderSearch(searchOrder);
    }//GEN-LAST:event_searchBtnActionPerformed

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
            java.util.logging.Logger.getLogger(vendorOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(vendorOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(vendorOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(vendorOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new vendorOrderHistory().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cancelReasonLabel;
    private javax.swing.JComboBox<String> chartMonthBox;
    private javax.swing.JLabel chartWeekRange;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel feedbackLabel;
    private javax.swing.JTextArea feedbackTxtArea;
    private javax.swing.JLabel idLabel;
    private javax.swing.JLabel idLabel1;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JButton monthChartBtn;
    private javax.swing.JComboBox<String> monthTableBox;
    private javax.swing.JTable orderTable;
    private javax.swing.JButton rangeBtn;
    private javax.swing.JLabel reasonLabel;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchTxt;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JComboBox<String> timeRangeBox;
    private javax.swing.JLabel totalPriceLabel;
    private javax.swing.JButton viewBtn;
    private javax.swing.JComboBox<String> yearTableBox;
    // End of variables declaration//GEN-END:variables
}
