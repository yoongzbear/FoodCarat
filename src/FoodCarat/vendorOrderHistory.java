/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
    private String email = "vendor@mail.com";
//    private String email = User.getSessionEmail();
    Vendor vendor = new Vendor(email);
    
    public vendorOrderHistory() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186,85,211)); //setting background color of frame
        
        //test if it works
        displayVendorOrder();
        
        //set the placeholder for search box
        GuiUtility.setPlaceholder(searchTxt, "Enter your search");
        
        //hide month and year chooser for table
        monthChooser.setVisible(false);
        yearChooser.setVisible(false);        
    }    
    
    //display all orders to vendor
    public void displayVendorOrder() {
        //display on table
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        int index = 1;
        model.setRowCount(0);
        //access vendor orders through vendor class
        Order orders = new Order();
        List<String[]> allOrders = orders.getAllOrders(email);
        for (String[] orderData : allOrders) {
            Item item = new Item();            
            String orderID = orderData[0];
            String orderMethod = orderData[1];
            String orderItems = orderData[2];
            String orderStatus = orderData[3];
            String customerEmail = orderData[4];
            String orderTotal = orderData[8];

            String updatedOrderItems = item.replaceItemIDsWithNames(orderItems);

            //if status = pending accept, dont show
            if (orderStatus.equalsIgnoreCase("Pending accept") || orderStatus.equalsIgnoreCase("Canceled")) {
                continue;
            } else {
                model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
            }            
        }
    }
    
    //display order details
    public void displayVendorOrder(int orderID) {
        DecimalFormat df = new DecimalFormat("0.00");
                
        String[] details = new Order().getOrder(orderID);  
        
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
            int itemID = Integer.parseInt(parts[0]); 
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
        dateLabel.setText(details[9].trim());
        totalPriceLabel.setText("RM" + details[10].trim());

        //view if got feedback for the order
        Review orderReview = new Review(orderID);
        //String reviews = orderReview.getFeedback();
        Review review = new Review(orderID);
        try {
            String feedback = review.getFeedback();
            String[] feedbackParts = feedback.split(",");
            String orderFeedback = feedbackParts[1];
            if (!orderFeedback.equals("null")) {
                //display feedback in feedback text area
                feedbackTxtArea.setText(orderFeedback);
            } else {
                feedbackTxtArea.setText("-");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //display vendor order based on search - item name, customer email, method, order id, status
    public void displayOrderSearch(String search) {
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        Order orders = new Order();
        List<String[]> allVendorOrders = orders.getAllOrders(email);
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);
        
        for (String[] orderData : allVendorOrders) {
            Item item = new Item();            
            String orderID = orderData[0];
            String orderMethod = orderData[1];
            String orderItems = orderData[2];
            String orderStatus = orderData[3];
            String customerEmail = orderData[4];
            String orderTotal = orderData[7];

            String updatedOrderItems = item.replaceItemIDsWithNames(orderItems);

            //check if search matches any of item name, customer email, method, order id
            String lowerSearch = search.toLowerCase(); //standardize case of search to lowercase
            boolean matchesOrderID = orderID.toLowerCase().contains(lowerSearch);
            boolean matchesMethod = orderMethod.toLowerCase().contains(lowerSearch);
            boolean matchesEmail = customerEmail.toLowerCase().contains(lowerSearch);
            boolean matchesItems = updatedOrderItems.toLowerCase().contains(lowerSearch);
            boolean matchesStatus = orderStatus.toLowerCase().contains(lowerSearch);

            if (matchesOrderID || matchesMethod || matchesEmail || matchesItems) {
                model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
            }
        }
    }

    //have filter to show monthly, or yearly
    public void displayOrderTimeRange(String timeRange, String inputTime) {
        //get date to test         
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        int index = 1;
        model.setRowCount(0);
        
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        //get all orders from vendor, filter based on date  
        Order orders = new Order();
        List<String[]> allOrders = orders.getAllOrders(email);
        for (String[] orderData : allOrders) {
            Item item = new Item();            
            //orderID,orderMethod,[itemID;quantity],orderStatus,customerEmail,runnerEmail,cancelReason,deliveryFee,totalPaid,date,totalprice
            String orderID = orderData[0];
            String orderMethod = orderData[1];
            String orderItems = orderData[2];
            String orderStatus = orderData[3];
            String customerEmail = orderData[4];
            String orderTotal = orderData[8];
            String orderDate = orderData[9];

            String updatedOrderItems = item.replaceItemIDsWithNames(orderItems);

            //excluding orders with status pending accept
            if (orderStatus.equalsIgnoreCase("Pending accept") || orderStatus.equalsIgnoreCase("Canceled")) {
                continue;
            } else {
                if (timeRange.equalsIgnoreCase("Daily")) { //daily
                    //inputTime = date
                    if (orderDate.trim().equals(inputTime.trim())) {
                        model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
                    }
                } else if (timeRange.equalsIgnoreCase("Monthly")) { //monthly
                    //inputTime = 1,2025
                    String[] inputTimeParts = inputTime.split(",");
                    int inputMonth = Integer.parseInt(inputTimeParts[0]);
                    int inputYear = Integer.parseInt(inputTimeParts[1]);

                    String[] orderDateParts = orderDate.split("-");
                    int orderYear = Integer.parseInt(orderDateParts[0]);
                    int orderMonth = Integer.parseInt(orderDateParts[1]);

                    if (orderYear == inputYear && orderMonth == inputMonth) {
                        model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
                    }
                } else if (timeRange.equalsIgnoreCase("Yearly")) { //yearly
                    int inputYear = Integer.parseInt(inputTime);
                    String[] orderDateParts = orderDate.split("-");
                    int orderYear = Integer.parseInt(orderDateParts[0]);
                    if (orderYear == inputYear) {
                        model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
                    }
                }
            }
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
        dateLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        methodLabel = new javax.swing.JLabel();
        feedbackLabel = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        totalPriceLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        feedbackTxtArea = new javax.swing.JTextArea();
        rangeBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        monthChartBtn = new javax.swing.JButton();
        chartWeekRange = new javax.swing.JLabel();
        weeklyDateChooser = new com.toedter.calendar.JDateChooser();
        chartWeekRange1 = new javax.swing.JLabel();
        weeklyEndDateTxt = new javax.swing.JLabel();
        weeklyChartBtn = new javax.swing.JButton();
        chartWeekRange3 = new javax.swing.JLabel();
        jMonthChooser1 = new com.toedter.calendar.JMonthChooser();
        chartWeekRange4 = new javax.swing.JLabel();
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        weeklyChartPanel = new javax.swing.JPanel();
        monthlyChartPanel = new javax.swing.JPanel();
        dateChooser = new com.toedter.calendar.JDateChooser();
        selectDateLabel = new javax.swing.JLabel();
        monthChooser = new com.toedter.calendar.JMonthChooser();
        yearChooser = new com.toedter.calendar.JYearChooser();

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

        timeRangeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Time Range", "Daily", "Monthly", "Yearly" }));
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

        dateLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        dateLabel.setText("Date");

        emailLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        emailLabel.setText("Email");

        methodLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        methodLabel.setText("Method");

        feedbackLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        feedbackLabel.setText("Order Feedback:");

        jLabel18.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel18.setText("Total Price:");

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
                                .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalPriceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                .addContainerGap(55, Short.MAX_VALUE))
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
                    .addComponent(dateLabel)
                    .addComponent(jLabel16)
                    .addComponent(statusLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(totalPriceLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel18))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(emailLabel)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(methodLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(feedbackLabel))
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

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel4.setText("Weekly");

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel6.setText("Monthly");

        monthChartBtn.setText("Generate Chart");

        chartWeekRange.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange.setText("Start Date:");

        weeklyDateChooser.setDateFormatString("yyyy-MM-dd");
        weeklyDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserInput(evt);
            }
        });

        chartWeekRange1.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange1.setText("End Date:");

        weeklyEndDateTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        weeklyEndDateTxt.setText("yyyy-MM-dd");

        weeklyChartBtn.setText("Generate Chart");

        chartWeekRange3.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange3.setText("Month:");

        chartWeekRange4.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange4.setText("Year:");

        javax.swing.GroupLayout weeklyChartPanelLayout = new javax.swing.GroupLayout(weeklyChartPanel);
        weeklyChartPanel.setLayout(weeklyChartPanelLayout);
        weeklyChartPanelLayout.setHorizontalGroup(
            weeklyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        weeklyChartPanelLayout.setVerticalGroup(
            weeklyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 186, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout monthlyChartPanelLayout = new javax.swing.GroupLayout(monthlyChartPanel);
        monthlyChartPanel.setLayout(monthlyChartPanelLayout);
        monthlyChartPanelLayout.setHorizontalGroup(
            monthlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 455, Short.MAX_VALUE)
        );
        monthlyChartPanelLayout.setVerticalGroup(
            monthlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 178, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chartWeekRange3)
                                .addGap(18, 18, 18)
                                .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(chartWeekRange4)
                                .addGap(18, 18, 18)
                                .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4)
                            .addComponent(weeklyChartBtn)
                            .addComponent(monthChartBtn)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(monthlyChartPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(weeklyChartPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chartWeekRange)
                                .addGap(18, 18, 18)
                                .addComponent(weeklyDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(chartWeekRange1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(weeklyEndDateTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartWeekRange)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chartWeekRange1)
                        .addComponent(weeklyEndDateTxt))
                    .addComponent(weeklyDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(weeklyChartBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(weeklyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chartWeekRange3)
                            .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(monthChartBtn))
                    .addComponent(chartWeekRange4))
                .addGap(18, 18, 18)
                .addComponent(monthlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dateChooser.setDateFormatString("yyyy-MM-dd");

        selectDateLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        selectDateLabel.setText("Select:");

        yearChooser.setMinimumSize(new java.awt.Dimension(80, 22));
        yearChooser.setPreferredSize(new java.awt.Dimension(70, 22));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(viewBtn)
                            .addComponent(jScrollPane1)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(timeRangeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(searchBtn))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(selectDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(monthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rangeBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(78, 78, 78)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(menuBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeRangeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchBtn))
                                    .addComponent(rangeBtn))
                                .addGap(11, 11, 11)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(viewBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(monthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(selectDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void viewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewBtnActionPerformed
        //if got reason ID, display the label and text field for reason cancelled
        int selectedRow = orderTable.getSelectedRow();

        //have validation to "choose an item in the table"
        if (selectedRow >= 0) {
            Object id = orderTable.getModel().getValueAt(selectedRow, 1);
            int selectID = (int) id;
            displayVendorOrder(selectID);
        } else {
            //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to view details.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_viewBtnActionPerformed

    private void rangeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rangeBtnActionPerformed
        String timeRange = timeRangeBox.getSelectedItem().toString();
        String inputTime = "";

        if (timeRange.equalsIgnoreCase("Select Time Range")) {
            JOptionPane.showMessageDialog(null, "Please select a time range.", "Alert", JOptionPane.WARNING_MESSAGE);
            displayVendorOrder();
        } else {
            if (timeRange.equalsIgnoreCase("Daily")) {
                //get date
                if (dateChooser.getDate() == null) {
                    JOptionPane.showMessageDialog(null, "Please select a date.", "Alert", JOptionPane.WARNING_MESSAGE);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    inputTime = dateFormat.format(dateChooser.getDate());
                }
            } else if (timeRange.equalsIgnoreCase("Monthly")) {
                //get month and year, append into input time string
                String month = String.valueOf(monthChooser.getMonth() + 1); //index of month + 1 
                String year = String.valueOf(yearChooser.getYear());
                inputTime = month + "," + year;
            } else if (timeRange.equalsIgnoreCase("Yearly")) {
                inputTime = String.valueOf(yearChooser.getYear());
            }
            displayOrderTimeRange(timeRange, inputTime); //call method
        }
    }//GEN-LAST:event_rangeBtnActionPerformed

    private void timeRangeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeRangeBoxActionPerformed
        if (timeRangeBox.getSelectedItem().equals("Daily")) {
            //display calendar to choose date 
            selectDateLabel.setText("Date:");
            dateChooser.setVisible(true);
            monthChooser.setVisible(false);
            yearChooser.setVisible(false);
        }    
        else if (timeRangeBox.getSelectedItem().equals("Monthly")) {
            selectDateLabel.setText("Select:");
            dateChooser.setVisible(false);
            monthChooser.setVisible(true);
            yearChooser.setVisible(true);
        } else if (timeRangeBox.getSelectedItem().equals("Yearly")) {
            selectDateLabel.setText("Select:");
            dateChooser.setVisible(false);
            monthChooser.setVisible(false);
            yearChooser.setVisible(true);
        } else if (timeRangeBox.getSelectedItem().equals("Select Time Range")) {
            displayVendorOrder();
        }
    }//GEN-LAST:event_timeRangeBoxActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        // TODO add your handling code here:
        String searchOrder = searchTxt.getText();
        if (searchOrder.equals("Enter item search")) { //if vendor doesn't enter any input in the search box
            JOptionPane.showMessageDialog(null, "Please enter item name to search.", "Alert", JOptionPane.WARNING_MESSAGE);
            displayVendorOrder();
        } else {
            displayOrderSearch(searchOrder);
        }        
    }//GEN-LAST:event_searchBtnActionPerformed

    private void jDateChooserInput(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooserInput
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date chosenDate = weeklyDateChooser.getDate();
        if (chosenDate != null) {
            //set the date to the monday of the week
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(chosenDate);

            //check if the selected date is Monday
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                //move to the nearest next Monday
                while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                weeklyDateChooser.setDate(calendar.getTime());
                JOptionPane.showMessageDialog(null, "Only Mondays are allowed to be selected. The next Monday is selected.");
            }
            
            //calculate and display end date
            calendar.add(Calendar.DAY_OF_MONTH, 6); //Monday to Sunday
            Date endDate = calendar.getTime();
            weeklyEndDateTxt.setText(dateFormat.format(endDate));
        }
    }//GEN-LAST:event_jDateChooserInput

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
    private javax.swing.JLabel chartWeekRange;
    private javax.swing.JLabel chartWeekRange1;
    private javax.swing.JLabel chartWeekRange3;
    private javax.swing.JLabel chartWeekRange4;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel feedbackLabel;
    private javax.swing.JTextArea feedbackTxtArea;
    private javax.swing.JLabel idLabel;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private com.toedter.calendar.JMonthChooser jMonthChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JButton monthChartBtn;
    private com.toedter.calendar.JMonthChooser monthChooser;
    private javax.swing.JPanel monthlyChartPanel;
    private javax.swing.JTable orderTable;
    private javax.swing.JButton rangeBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchTxt;
    private javax.swing.JLabel selectDateLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JComboBox<String> timeRangeBox;
    private javax.swing.JLabel totalPriceLabel;
    private javax.swing.JButton viewBtn;
    private javax.swing.JButton weeklyChartBtn;
    private javax.swing.JPanel weeklyChartPanel;
    private com.toedter.calendar.JDateChooser weeklyDateChooser;
    private javax.swing.JLabel weeklyEndDateTxt;
    private com.toedter.calendar.JYearChooser yearChooser;
    // End of variables declaration//GEN-END:variables
}
