/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.BorderLayout;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author mastu
 */
public class vendorOrderHistory extends javax.swing.JFrame {

    /**
     * Creates new form vendorOrderHistory
     */
    private String email = User.getSessionEmail();
    Vendor vendor = new Vendor(email);

    public vendorOrderHistory() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186, 85, 211)); //setting background color of frame
        setLocationRelativeTo(null);
        
        displayVendorOrder();
        
        //set the placeholder for search box
        GuiUtility.setPlaceholder(searchTxt, "Enter your search");
        rangeBtn.setEnabled(false); //disable view range button
        
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
            orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
            String customerEmail = orderData[4];
            String orderTotal = orderData[8];

            String updatedOrderItems = item.replaceItemIDsWithNames(orderItems);

            //if status = pending accept, dont show
            if (orderStatus.equalsIgnoreCase("Pending accept") || orderStatus.equalsIgnoreCase("Cancelled") || orderStatus.equalsIgnoreCase("Assigning runner")) {
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
        statusLabel.setText(details[3].trim().substring(0, 1).toUpperCase() + details[3].trim().substring(1).toLowerCase());
        emailLabel.setText(details[4].trim());
        dateLabel.setText(details[9].trim());
        totalPriceLabel.setText("RM" + details[10].trim());

        //view if got feedback for the order
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
            JOptionPane.showMessageDialog(null, "Unable to display selected order", "Error", JOptionPane.ERROR_MESSAGE);
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
        boolean found = false;
        
        for (String[] orderData : allVendorOrders) {
            Item item = new Item();            
            String orderID = orderData[0];
            String orderMethod = orderData[1];
            String orderItems = orderData[2];
            String orderStatus = orderData[3];
            orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
            String customerEmail = orderData[4];

            String updatedOrderItems = item.replaceItemIDsWithNames(orderItems);

            //check if search matches any of item name, customer email, method, order id
            String lowerSearch = search.toLowerCase(); //standardize case of search to lowercase
            boolean matchesOrderID = orderID.toLowerCase().contains(lowerSearch);
            boolean matchesMethod = orderMethod.toLowerCase().contains(lowerSearch);
            boolean matchesEmail = customerEmail.toLowerCase().contains(lowerSearch);
            boolean matchesItems = updatedOrderItems.toLowerCase().contains(lowerSearch);
            boolean matchesStatus = orderStatus.toLowerCase().contains(lowerSearch);

            if (matchesOrderID || matchesMethod || matchesEmail || matchesItems || matchesStatus) {
                found = true;
                model.addRow(new Object[]{index++, orderID, customerEmail, orderMethod, updatedOrderItems, orderStatus});
            } 
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(null, "Search is not found.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            displayVendorOrder();
        }
    }

    //have filter to show monthly, or yearly
    public void displayOrderTimeRange(String timeRange, String inputTime) {
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        int index = 1;
        model.setRowCount(0);
        
        //get all orders from vendor, filter based on date  
        Order orders = new Order();
        List<String[]> allOrders = orders.getAllOrders(email);
        for (String[] orderData : allOrders) {
            Item item = new Item();            
            String orderID = orderData[0];
            String orderMethod = orderData[1];
            String orderItems = orderData[2];
            String orderStatus = orderData[3];
            orderStatus = orderStatus.substring(0, 1).toUpperCase() + orderStatus.substring(1).toLowerCase();
            String customerEmail = orderData[4];
            String orderDate = orderData[9];

            String updatedOrderItems = item.replaceItemIDsWithNames(orderItems);

            //excluding orders with status pending accept
            if (orderStatus.equalsIgnoreCase("Pending accept") || orderStatus.equalsIgnoreCase("Cancelled")) {
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

    //generate chart
    public void displayChart(String type, String timeRange) {
        Order order = new Order();
        List<String[]> orderCount = new  ArrayList<>();
        //get order count for each item based on time range
        if (type.equalsIgnoreCase("weekly")) {
            orderCount = order.getOrderedItemQuantities(email, "weekly", timeRange);
        } else if (type.equalsIgnoreCase("monthly")) {
            orderCount = order.getOrderedItemQuantities(email, "monthly", timeRange);
        } else if (type.equalsIgnoreCase("quarterly")) {
            orderCount = order.getOrderedItemQuantities(email, "quarterly", timeRange);
        }

        // Check if all items have 0 quantity
        boolean allZero = true;
        for (String[] item : orderCount) {
            int quantity = Integer.parseInt(item[1]);
            if (quantity > 0) {
                allZero = false;
                break;
            }
        }

        //display message and clear chart if all items is 0 
        if (allZero) {
            JOptionPane.showMessageDialog(null, "No items were sold for the period.", "No Sales Data", JOptionPane.INFORMATION_MESSAGE);
            if (type.equalsIgnoreCase("weekly")) {                
                weeklyChartPanel.removeAll();
                weeklyChartPanel.setLayout(new BorderLayout());
                weeklyChartPanel.revalidate();
                weeklyChartPanel.repaint();
            } else if (type.equalsIgnoreCase("monthly")) {
                monthlyChartPanel1.removeAll();
                monthlyChartPanel1.setLayout(new BorderLayout());
                monthlyChartPanel1.revalidate();
                monthlyChartPanel1.repaint();
            } else if (type.equalsIgnoreCase("quarterly")) {
                quarterlyChartPanel.removeAll();
                quarterlyChartPanel.setLayout(new BorderLayout());
                quarterlyChartPanel.revalidate();
                quarterlyChartPanel.repaint();
            }
            return;
        }

        //make item names into initials only 
        Map<String, String> itemIdToNameMap = new HashMap<>();
        for (String[] itemData : new Item().getAllItems(email, false)) {
            if (itemData[1].contains("(N/A)")) {
                itemData[1] = itemData[1].replace("(N/A)", "").trim(); //remove N/A
            }
            itemIdToNameMap.put(itemData[0], itemData[1]); //itemID, itemName
        }

        //pass into chart making function - bar chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] item : orderCount) {
            String itemId = item[0];
            String itemName = itemIdToNameMap.getOrDefault(itemId, "Unknown");
            String initials = getInitials(itemName);
            int count = Integer.parseInt(item[1]);

            dataset.addValue(count, "Orders", initials); //count, series, initials
        }

        if (type.equalsIgnoreCase("weekly")) {
            ChartPanel chartPanel = ChartUtility.createBarChart(dataset, "Weekly Ordered Items", "Item", "Count");

            weeklyChartPanel.removeAll();
            weeklyChartPanel.setLayout(new BorderLayout());
            weeklyChartPanel.add(chartPanel, BorderLayout.CENTER);
            weeklyChartPanel.revalidate();
            weeklyChartPanel.repaint();
        } else if (type.equalsIgnoreCase("monthly")) {
            ChartPanel chartPanel = ChartUtility.createBarChart(dataset, "Monthly Ordered Items", "Item", "Count");

            monthlyChartPanel1.removeAll();
            monthlyChartPanel1.setLayout(new BorderLayout());
            monthlyChartPanel1.add(chartPanel, BorderLayout.CENTER);
            monthlyChartPanel1.revalidate();
            monthlyChartPanel1.repaint();
        } else if (type.equalsIgnoreCase("quarterly")) {
            ChartPanel chartPanel = ChartUtility.createBarChart(dataset, "Quarterly Ordered Items", "Item", "Count");

            quarterlyChartPanel.removeAll();
            quarterlyChartPanel.setLayout(new BorderLayout());
            quarterlyChartPanel.add(chartPanel, BorderLayout.CENTER);
            quarterlyChartPanel.revalidate();
            quarterlyChartPanel.repaint();
        }

    }

    //helper method to return initials of item name
    private String getInitials(String name) {
        String[] words = name.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        chartWeekRange = new javax.swing.JLabel();
        weeklyDateChooser = new com.toedter.calendar.JDateChooser();
        chartWeekRange1 = new javax.swing.JLabel();
        weeklyEndDateTxt = new javax.swing.JLabel();
        weeklyChartBtn = new javax.swing.JButton();
        weeklyChartPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        chartWeekRange3 = new javax.swing.JLabel();
        chartMonthChooser = new com.toedter.calendar.JMonthChooser();
        chartWeekRange4 = new javax.swing.JLabel();
        chartYearChooser = new com.toedter.calendar.JYearChooser();
        monthChartBtn = new javax.swing.JButton();
        monthlyChartPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        chartWeekRange5 = new javax.swing.JLabel();
        chartQuarterYearChooser = new com.toedter.calendar.JYearChooser();
        quarterChartBtn = new javax.swing.JButton();
        quarterlyChartPanel = new javax.swing.JPanel();
        chartWeekRange6 = new javax.swing.JLabel();
        quarterChartBox = new javax.swing.JComboBox<>();
        chartWeekRange7 = new javax.swing.JLabel();
        startDateQuarterLabel = new javax.swing.JLabel();
        endDateQuarterLabel = new javax.swing.JLabel();
        chartWeekRange8 = new javax.swing.JLabel();
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

        dateLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        emailLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        methodLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        feedbackLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        feedbackLabel.setText("Order Feedback:");

        jLabel18.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel18.setText("Total Price:");

        totalPriceLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        statusLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

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

        chartWeekRange.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange.setText("Start Date:");

        weeklyDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                weeklyDateChooserjDateChooserInput(evt);
            }
        });

        chartWeekRange1.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange1.setText("End Date:");

        weeklyEndDateTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        weeklyChartBtn.setText("Generate Chart");
        weeklyChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weeklyChartBtnActionPerformed(evt);
            }
        });

        weeklyChartPanel.setMaximumSize(new java.awt.Dimension(467, 300));
        weeklyChartPanel.setMinimumSize(new java.awt.Dimension(467, 300));
        weeklyChartPanel.setPreferredSize(new java.awt.Dimension(467, 300));

        javax.swing.GroupLayout weeklyChartPanelLayout = new javax.swing.GroupLayout(weeklyChartPanel);
        weeklyChartPanel.setLayout(weeklyChartPanelLayout);
        weeklyChartPanelLayout.setHorizontalGroup(
            weeklyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        weeklyChartPanelLayout.setVerticalGroup(
            weeklyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(weeklyChartBtn)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chartWeekRange)
                            .addComponent(chartWeekRange1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(weeklyEndDateTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(weeklyDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(weeklyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartWeekRange)
                    .addComponent(weeklyDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chartWeekRange1)
                    .addComponent(weeklyEndDateTxt))
                .addGap(31, 31, 31)
                .addComponent(weeklyChartBtn)
                .addGap(30, 30, 30)
                .addComponent(weeklyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(137, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Weekly", jPanel3);

        chartWeekRange3.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange3.setText("Month:");

        chartWeekRange4.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange4.setText("Year:");

        monthChartBtn.setText("Generate Chart");
        monthChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthChartBtnActionPerformed(evt);
            }
        });

        monthlyChartPanel1.setMaximumSize(new java.awt.Dimension(467, 300));
        monthlyChartPanel1.setMinimumSize(new java.awt.Dimension(467, 300));
        monthlyChartPanel1.setPreferredSize(new java.awt.Dimension(467, 300));

        javax.swing.GroupLayout monthlyChartPanel1Layout = new javax.swing.GroupLayout(monthlyChartPanel1);
        monthlyChartPanel1.setLayout(monthlyChartPanel1Layout);
        monthlyChartPanel1Layout.setHorizontalGroup(
            monthlyChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        monthlyChartPanel1Layout.setVerticalGroup(
            monthlyChartPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chartWeekRange3)
                            .addComponent(chartWeekRange4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chartMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chartYearChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(monthChartBtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 27, Short.MAX_VALUE)
                .addComponent(monthlyChartPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartWeekRange3)
                    .addComponent(chartMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartWeekRange4))
                .addGap(33, 33, 33)
                .addComponent(monthChartBtn)
                .addGap(48, 48, 48)
                .addComponent(monthlyChartPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(117, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Monthly", jPanel4);

        chartWeekRange5.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange5.setText("Year:");

        chartQuarterYearChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                displayYear(evt);
            }
        });

        quarterChartBtn.setText("Generate Chart");
        quarterChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quarterChartBtnActionPerformed(evt);
            }
        });

        quarterlyChartPanel.setMaximumSize(new java.awt.Dimension(467, 300));
        quarterlyChartPanel.setMinimumSize(new java.awt.Dimension(467, 300));
        quarterlyChartPanel.setPreferredSize(new java.awt.Dimension(467, 300));

        javax.swing.GroupLayout quarterlyChartPanelLayout = new javax.swing.GroupLayout(quarterlyChartPanel);
        quarterlyChartPanel.setLayout(quarterlyChartPanelLayout);
        quarterlyChartPanelLayout.setHorizontalGroup(
            quarterlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        quarterlyChartPanelLayout.setVerticalGroup(
            quarterlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        chartWeekRange6.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange6.setText("Quarter:");

        quarterChartBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quarter", "Q1", "Q2", "Q3", "Q4" }));
        quarterChartBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseQuarter(evt);
            }
        });

        chartWeekRange7.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange7.setText("Start Date:");

        startDateQuarterLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        endDateQuarterLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        chartWeekRange8.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange8.setText("End Date:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(quarterChartBtn)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chartWeekRange6)
                            .addComponent(chartWeekRange5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(quarterChartBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chartQuarterYearChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chartWeekRange7)
                            .addComponent(chartWeekRange8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(startDateQuarterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(endDateQuarterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(103, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(quarterlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chartWeekRange6)
                    .addComponent(quarterChartBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartWeekRange7)
                    .addComponent(startDateQuarterLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartQuarterYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartWeekRange5)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chartWeekRange8)
                        .addComponent(endDateQuarterLabel)))
                .addGap(33, 33, 33)
                .addComponent(quarterChartBtn)
                .addGap(47, 47, 47)
                .addComponent(quarterlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Quarterly", jPanel5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
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
                        .addGap(18, 18, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(timeRangeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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
            try {
                int selectID = Integer.parseInt(id.toString()); //convert the object to string and parse as int
                displayVendorOrder(selectID);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "The selected ID is not a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
                    displayVendorOrder();
                    return;
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
            rangeBtn.setEnabled(true);
        }    
        else if (timeRangeBox.getSelectedItem().equals("Monthly")) {
            selectDateLabel.setText("Select:");
            dateChooser.setVisible(false);
            monthChooser.setVisible(true);
            yearChooser.setVisible(true);
            rangeBtn.setEnabled(true);
        } else if (timeRangeBox.getSelectedItem().equals("Yearly")) {
            selectDateLabel.setText("Select:");
            dateChooser.setVisible(false);
            monthChooser.setVisible(false);
            yearChooser.setVisible(true);
            rangeBtn.setEnabled(true);
        } else if (timeRangeBox.getSelectedItem().equals("Select Time Range")) {
            displayVendorOrder();
            rangeBtn.setEnabled(false);
            dateChooser.setVisible(true);
            dateChooser.setDate(null);
            monthChooser.setVisible(false);
            yearChooser.setVisible(false);
        }
    }//GEN-LAST:event_timeRangeBoxActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        String searchOrder = searchTxt.getText();
        if (searchOrder.equals("Enter item search")) { //if vendor doesn't enter any input in the search box
            JOptionPane.showMessageDialog(null, "Please enter item name to search.", "Alert", JOptionPane.WARNING_MESSAGE);
            displayVendorOrder();
        } else {
            displayOrderSearch(searchOrder);
        }        
    }//GEN-LAST:event_searchBtnActionPerformed

    private void weeklyDateChooserjDateChooserInput(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_weeklyDateChooserjDateChooserInput
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
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
    }//GEN-LAST:event_weeklyDateChooserjDateChooserInput

    private void weeklyChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weeklyChartBtnActionPerformed
        //get start date and end date, combine into a string and call ratingCount in review class
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date chosenDate = weeklyDateChooser.getDate();
        if (chosenDate == null) {
            JOptionPane.showMessageDialog(null, "Please select a date.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                String startDate = newDateFormat.format(chosenDate);
                //convert end date format
                Date endDate = oldDateFormat.parse(weeklyEndDateTxt.getText());
                String formatEndDate = newDateFormat.format(endDate);
                String timeRange = startDate + "," + formatEndDate;
                displayChart("weekly", timeRange);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Invalid end date format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_weeklyChartBtnActionPerformed

    private void monthChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthChartBtnActionPerformed
        //get month and year
        String month = String.valueOf(chartMonthChooser.getMonth() + 1); //index of month + 1
        String year = String.valueOf(chartYearChooser.getYear());
        String timeRange = month + "," + year;
        displayChart("monthly", timeRange);
    }//GEN-LAST:event_monthChartBtnActionPerformed

    private void quarterChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quarterChartBtnActionPerformed
        //get quarter and year        
        String quarter = quarterChartBox.getSelectedItem().toString();
        String year = String.valueOf(chartQuarterYearChooser.getYear());
        String timeRange = "";
        if (quarter.equalsIgnoreCase("Quarter")) {
            JOptionPane.showMessageDialog(null, "Please select a quarter", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (quarter.equalsIgnoreCase("Q1")) {
                timeRange = year + "-01-01," + year + "-03-31"; //January 1 – March 31
            } else if (quarter.equalsIgnoreCase("Q2")) {
                timeRange = year + "-04-01," + year + "-06-30"; //April 1 – June 30
            } else if (quarter.equalsIgnoreCase("Q3")) {
                timeRange = year + "-07-01," + year + "-09-30"; //July 1 – September 30
            } else if (quarter.equalsIgnoreCase("Q4")) {
                timeRange = year + "-10-01," + year + "-12-31"; //October 1 – December 31
            }
            displayChart("quarterly", timeRange);
        }
    }//GEN-LAST:event_quarterChartBtnActionPerformed

    private void chooseQuarter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseQuarter
        //display start and end date
        String quarter = quarterChartBox.getSelectedItem().toString();
        String year = String.valueOf(chartQuarterYearChooser.getYear());
        if (quarter.equalsIgnoreCase("Q1")) {
            //January 1 – March 31
            startDateQuarterLabel.setText("1 Jan " + year);
            endDateQuarterLabel.setText("31 Mar " + year);
        } else if (quarter.equalsIgnoreCase("Q2")) {
            //April 1 – June 30
            startDateQuarterLabel.setText("1 Apr " + year);
            endDateQuarterLabel.setText("3  Jun" + year);
        } else if (quarter.equalsIgnoreCase("Q3")) {
            //July 1 – September 30
            startDateQuarterLabel.setText("1 Jul " + year);
            endDateQuarterLabel.setText("30 Sep " + year);
        } else if (quarter.equalsIgnoreCase("Q4")) {
            //October 1 – December 31
            startDateQuarterLabel.setText("1 Oct " + year);
            endDateQuarterLabel.setText("31 Dec " + year);
        } else {
            GuiUtility.clearFields(startDateQuarterLabel, endDateQuarterLabel);
        }
    }//GEN-LAST:event_chooseQuarter

    private void displayYear(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_displayYear
        //dynamically change start and end date label based on year 
        String quarter = quarterChartBox.getSelectedItem().toString();
        String year = String.valueOf(chartQuarterYearChooser.getYear());
        if (!quarter.equalsIgnoreCase("Quarter") && year.length() == 4) {
            if (quarter.equalsIgnoreCase("Q1")) {
                //January 1 – March 31
                startDateQuarterLabel.setText("1 Jan " + year);
                endDateQuarterLabel.setText("31 Mar " + year);
            } else if (quarter.equalsIgnoreCase("Q2")) {
                //April 1 – June 30
                startDateQuarterLabel.setText("1 Apr " + year);
                endDateQuarterLabel.setText("3  Jun" + year);
            } else if (quarter.equalsIgnoreCase("Q3")) {
                //July 1 – September 30
                startDateQuarterLabel.setText("1 Jul " + year);
                endDateQuarterLabel.setText("30 Sep " + year);
            } else if (quarter.equalsIgnoreCase("Q4")) {
                //October 1 – December 31
                startDateQuarterLabel.setText("1 Oct " + year);
                endDateQuarterLabel.setText("31 Dec " + year);
            }
        }
    }//GEN-LAST:event_displayYear

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
    private com.toedter.calendar.JMonthChooser chartMonthChooser;
    private com.toedter.calendar.JYearChooser chartQuarterYearChooser;
    private javax.swing.JLabel chartWeekRange;
    private javax.swing.JLabel chartWeekRange1;
    private javax.swing.JLabel chartWeekRange3;
    private javax.swing.JLabel chartWeekRange4;
    private javax.swing.JLabel chartWeekRange5;
    private javax.swing.JLabel chartWeekRange6;
    private javax.swing.JLabel chartWeekRange7;
    private javax.swing.JLabel chartWeekRange8;
    private com.toedter.calendar.JYearChooser chartYearChooser;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel endDateQuarterLabel;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JButton monthChartBtn;
    private com.toedter.calendar.JMonthChooser monthChooser;
    private javax.swing.JPanel monthlyChartPanel1;
    private javax.swing.JTable orderTable;
    private javax.swing.JComboBox<String> quarterChartBox;
    private javax.swing.JButton quarterChartBtn;
    private javax.swing.JPanel quarterlyChartPanel;
    private javax.swing.JButton rangeBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchTxt;
    private javax.swing.JLabel selectDateLabel;
    private javax.swing.JLabel startDateQuarterLabel;
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
