/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author mastu
 */
public class vendorReview extends javax.swing.JFrame {

    private String email = "vendor@mail.com";
//    private String email = User.getSessionEmail();
    private String role = "vendor";

    /**
     * Creates new form vendorReview
     */
    public vendorReview() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186, 85, 211)); //setting background color of frame
        setLocationRelativeTo(null);
        
        displayReviews();
        vendorFeedbackTxtArea.setEditable(false);
        
    }

    //display all vendor reviews
    public void displayReviews() {
        DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
        int index = 1;
        model.setRowCount(0);
        List<String[]> allReviews = new Review().getAllReviews(email, "vendor");
        System.out.println(email);
        for (String[] reviewData : allReviews) {
                    
            //0reviewID,1orderID,2reviewType,3rating,4review,5date,6customerEmail
            String reviewID = reviewData[0];
            String rating = reviewData[3];
            String feedback = reviewData[4];
            String reviewDate = reviewData[5];
            String customerEmail = reviewData[6];

            model.addRow(new Object[]{index++, reviewID, customerEmail, reviewDate, rating + "🌟", feedback});
        }
    }
    
    //display based on filters (rating)
    public void displayReviews(String[] filter) {
        DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
        int index = 1;
        model.setRowCount(0);
        //get the filters
        List<String[]> allReviews = new Review().getAllReviews(email, "vendor");
        for (String[] reviewData : allReviews) {
                    
            //0reviewID,1orderID,2reviewType,3rating,4review,5date,6customerEmail
            String reviewID = reviewData[0];
            String rating = reviewData[3];
            String feedback = reviewData[4];
            String reviewDate = reviewData[5];
            String customerEmail = reviewData[6];

            for (String selectedRating : filter) {
                if (rating.equals(selectedRating)) {
                    model.addRow(new Object[]{index++, reviewID, customerEmail, reviewDate, rating + "🌟", feedback});
                    break;
                }
            }
        }
    }
    
    //sort based on sorting
    public void displayReviews(String sorting) {
        DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
        int index = 1;
        model.setRowCount(0);
        List<String[]> allReviews = new Review().getAllReviews(email, "vendor");

        //sorting
        if (sorting.equals("Highest to Lowest")) {
            allReviews.sort((a, b) -> Integer.parseInt(b[3]) - Integer.parseInt(a[3])); // Descending order
        } else if (sorting.equals("Lowest to Highest")) {
            allReviews.sort((a, b) -> Integer.parseInt(a[3]) - Integer.parseInt(b[3])); // Ascending order
        }

        for (String[] reviewData : allReviews) {

            //0reviewID,1orderID,2reviewType,3rating,4review,5date,6customerEmail
            String reviewID = reviewData[0];
            String rating = reviewData[3];
            String feedback = reviewData[4];
            String reviewDate = reviewData[5];
            String customerEmail = reviewData[6];

            model.addRow(new Object[]{index++, reviewID, customerEmail, reviewDate, rating + "🌟", feedback});
        }
    }
    
    //display selected vendor review 
    public void displaySelectedReview(String reviewID) {
        DecimalFormat df = new DecimalFormat("0.00");
        //review, order, items
        String[] details = new Review().getReview(reviewID);
        
        //get items, price, and quantity to display in table
        Item item = new Item();
        //get item data from Item class to get price
        String orderItems = details[9].trim();
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
        
        //5, 3, vendor, 3, Kena cancelled, 2024-01-01, customer@mail.com, 3, Take away, [2;1|4;1], Ordered, customerEmail, NULL, NULL, NULL, 20.00, 2025-01-01 , 25.80
        idLabel.setText(details[1].trim()); //order ID
        dateLabel.setText(details[5].trim());
        methodLabel.setText(details[8].trim());
        emailLabel.setText(details[6].trim());
        priceLabel.setText("RM"+details[17].trim());
        ratingLabel.setText(details[3].trim() + " 🌟");
        vendorFeedbackTxtArea.setText(details[4].trim());
    }
    
    //generate weekly chart
    public void generateChart(String type, String timeRange) {
        int[] ratingsCount = new int[5];
        if (type.equalsIgnoreCase("weekly")) {
            //get rating count
            ratingsCount = new Review().ratingCount(email, "vendor", "weekly", timeRange);
        } else if (type.equalsIgnoreCase("monthly")) {
            ratingsCount = new Review().ratingCount(email, "vendor", "monthly", timeRange);
        }
        
        for (int num : ratingsCount) {
            System.out.println(num);
        }

        //pass into chart making function - pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();
        String[] ratingLabels = {"1", "2", "3", "4", "5"};

        for (int i = 0; i < ratingsCount.length; i++) { //adding label and count into dataset
            if (ratingsCount[i] > 0) { //only add ratings with more than 0 count
                dataset.setValue(ratingLabels[i], ratingsCount[i]);
            }
        }

        if (type.equalsIgnoreCase("weekly")) {
            ChartPanel chartPanel = ChartUtility.createPieChart(dataset, "Weekly Rating");
            weeklyChartPanel.removeAll();
            weeklyChartPanel.setLayout(new BorderLayout());
            weeklyChartPanel.add(chartPanel, BorderLayout.CENTER);
            weeklyChartPanel.revalidate();
            weeklyChartPanel.repaint();
        } else if (type.equalsIgnoreCase("monthly")) {
            ChartPanel chartPanel = ChartUtility.createPieChart(dataset, "Monthly Rating");
            monthlyChartPanel.removeAll();
            monthlyChartPanel.setLayout(new BorderLayout());
            monthlyChartPanel.add(chartPanel, BorderLayout.CENTER);
            monthlyChartPanel.revalidate();
            monthlyChartPanel.repaint();
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
        ratingOneChkBox = new javax.swing.JCheckBox();
        ratingTwoChkBox = new javax.swing.JCheckBox();
        sortComboBox = new javax.swing.JComboBox<>();
        ratingThreeChkBox = new javax.swing.JCheckBox();
        ratingFourChkBox = new javax.swing.JCheckBox();
        ratingFiveChkBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        reviewTable = new javax.swing.JTable();
        sortBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        filterBtn = new javax.swing.JButton();
        viewBtn = new javax.swing.JButton();
        revertBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        methodLabel = new javax.swing.JLabel();
        ratingLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        vendorFeedbackTxtArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        monthChartBtn = new javax.swing.JButton();
        chartWeekRange = new javax.swing.JLabel();
        weeklyDateChooser = new com.toedter.calendar.JDateChooser();
        chartWeekRange1 = new javax.swing.JLabel();
        weeklyEndDateTxt = new javax.swing.JLabel();
        weeklyChartBtn = new javax.swing.JButton();
        chartWeekRange3 = new javax.swing.JLabel();
        chartMonthChooser = new com.toedter.calendar.JMonthChooser();
        chartWeekRange4 = new javax.swing.JLabel();
        chartYearChooser = new com.toedter.calendar.JYearChooser();
        weeklyChartPanel = new javax.swing.JPanel();
        monthlyChartPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Vendor Review");

        menuBtn.setText("Main Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        ratingOneChkBox.setText("1 🌟");

        ratingTwoChkBox.setText("2 🌟");

        sortComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort Rating", "Lowest to Highest", "Highest to Lowest" }));

        ratingThreeChkBox.setText("3 🌟");

        ratingFourChkBox.setText("4 🌟");

        ratingFiveChkBox.setText("5 🌟");

        reviewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Review ID", "Customer Email", "Date", "Rating", "Feedback"
            }
        ));
        jScrollPane1.setViewportView(reviewTable);
        if (reviewTable.getColumnModel().getColumnCount() > 0) {
            reviewTable.getColumnModel().getColumn(0).setResizable(false);
            reviewTable.getColumnModel().getColumn(0).setPreferredWidth(20);
            reviewTable.getColumnModel().getColumn(1).setResizable(false);
            reviewTable.getColumnModel().getColumn(1).setPreferredWidth(20);
            reviewTable.getColumnModel().getColumn(3).setResizable(false);
            reviewTable.getColumnModel().getColumn(3).setPreferredWidth(60);
            reviewTable.getColumnModel().getColumn(4).setResizable(false);
            reviewTable.getColumnModel().getColumn(4).setPreferredWidth(30);
        }

        sortBtn.setText("Sort");
        sortBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortBtnActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel2.setText("Filter:");

        filterBtn.setText("Filter");
        filterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBtnActionPerformed(evt);
            }
        });

        viewBtn.setText("View Details");
        viewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewBtnActionPerformed(evt);
            }
        });

        revertBtn.setText("Revert Table");
        revertBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(revertBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(viewBtn)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ratingOneChkBox, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ratingTwoChkBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ratingThreeChkBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ratingFourChkBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ratingFiveChkBox)
                                .addGap(18, 18, 18)
                                .addComponent(filterBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sortBtn)))
                        .addGap(138, 138, 138))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 874, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ratingOneChkBox)
                    .addComponent(ratingTwoChkBox)
                    .addComponent(ratingThreeChkBox)
                    .addComponent(ratingFourChkBox)
                    .addComponent(ratingFiveChkBox)
                    .addComponent(filterBtn)
                    .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sortBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewBtn)
                    .addComponent(revertBtn))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel3.setText("Vendor Review Details");

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel6.setText("Ordered Items:");

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Price (RM)", "Quantity"
            }
        ));
        jScrollPane4.setViewportView(itemTable);
        if (itemTable.getColumnModel().getColumnCount() > 0) {
            itemTable.getColumnModel().getColumn(1).setResizable(false);
            itemTable.getColumnModel().getColumn(1).setPreferredWidth(30);
            itemTable.getColumnModel().getColumn(2).setResizable(false);
            itemTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        }

        jLabel7.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel7.setText("Vendor Feedback:");

        jLabel8.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel8.setText("Date:");

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel4.setText("Order ID:");

        jLabel5.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel5.setText("Customer Email:");

        jLabel9.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel9.setText("Order Method:");

        jLabel10.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel10.setText("Vendor Rating:");

        idLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        idLabel.setText("sd");

        dateLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        dateLabel.setText("date");

        emailLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        emailLabel.setText("em");

        methodLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        methodLabel.setText("cha");

        ratingLabel.setFont(new java.awt.Font("Segoe UI Emoji", 0, 14)); // NOI18N
        ratingLabel.setText("44");

        jLabel12.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel12.setText("Total Price: ");

        priceLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        priceLabel.setText("meow");

        vendorFeedbackTxtArea.setColumns(20);
        vendorFeedbackTxtArea.setRows(5);
        jScrollPane2.setViewportView(vendorFeedbackTxtArea);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                        .addGap(25, 25, 25))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8)
                                        .addGap(18, 18, 18)
                                        .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(methodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(priceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(ratingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addComponent(jLabel3))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(dateLabel)
                        .addComponent(methodLabel)
                        .addComponent(jLabel9))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(idLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(emailLabel)
                    .addComponent(jLabel12)
                    .addComponent(priceLabel)
                    .addComponent(jLabel10)
                    .addComponent(ratingLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel13.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel13.setText("Weekly");

        jLabel15.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel15.setText("Monthly");

        monthChartBtn.setText("Generate Chart");
        monthChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthChartBtnActionPerformed(evt);
            }
        });

        chartWeekRange.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange.setText("Start Date:");

        weeklyDateChooser.setDateFormatString("yyyy-MM-dd");
        weeklyDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                weeklyDateChooserjDateChooserInput(evt);
            }
        });

        chartWeekRange1.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange1.setText("End Date:");

        weeklyEndDateTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        weeklyEndDateTxt.setText("yyyy-MM-dd");

        weeklyChartBtn.setText("Generate Chart");
        weeklyChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weeklyChartBtnActionPerformed(evt);
            }
        });

        chartWeekRange3.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange3.setText("Month:");

        chartWeekRange4.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange4.setText("Year:");

        weeklyChartPanel.setPreferredSize(new java.awt.Dimension(325, 200));

        javax.swing.GroupLayout weeklyChartPanelLayout = new javax.swing.GroupLayout(weeklyChartPanel);
        weeklyChartPanel.setLayout(weeklyChartPanelLayout);
        weeklyChartPanelLayout.setHorizontalGroup(
            weeklyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 455, Short.MAX_VALUE)
        );
        weeklyChartPanelLayout.setVerticalGroup(
            weeklyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        monthlyChartPanel.setPreferredSize(new java.awt.Dimension(325, 200));

        javax.swing.GroupLayout monthlyChartPanelLayout = new javax.swing.GroupLayout(monthlyChartPanel);
        monthlyChartPanel.setLayout(monthlyChartPanelLayout);
        monthlyChartPanelLayout.setHorizontalGroup(
            monthlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        monthlyChartPanelLayout.setVerticalGroup(
            monthlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(chartWeekRange3)
                        .addGap(18, 18, 18)
                        .addComponent(chartMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(chartWeekRange4)
                        .addGap(18, 18, 18)
                        .addComponent(chartYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel13)
                    .addComponent(weeklyChartBtn)
                    .addComponent(monthChartBtn)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(monthlyChartPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                        .addComponent(weeklyChartPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(chartWeekRange)
                        .addGap(18, 18, 18)
                        .addComponent(weeklyDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chartWeekRange1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(weeklyEndDateTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartWeekRange)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chartWeekRange1)
                        .addComponent(weeklyEndDateTxt))
                    .addComponent(weeklyDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(weeklyChartBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(weeklyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chartWeekRange3)
                            .addComponent(chartMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chartYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(monthChartBtn))
                    .addComponent(chartWeekRange4))
                .addGap(18, 18, 18)
                .addComponent(monthlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(43, 43, 43))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(menuBtn)
                        .addGap(19, 19, 19))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(menuBtn)
                        .addGap(19, 19, 19))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void viewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewBtnActionPerformed
        //display selected row of item in the table
        int selectedRow = reviewTable.getSelectedRow();

        //have validation to "choose an item in the table"
        if (selectedRow >= 0) {
            Object id = reviewTable.getModel().getValueAt(selectedRow, 1);
            displaySelectedReview(id.toString());
        } else {
            //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to view details.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_viewBtnActionPerformed

    private void filterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtnActionPerformed
        String[] selectedFilter = new String[5];
        int index = 0;
        
        if (ratingOneChkBox.isSelected()) {
            selectedFilter[index++] = "1";
        }
        if (ratingTwoChkBox.isSelected()) {
            selectedFilter[index++] = "2";
        } 
        if (ratingThreeChkBox.isSelected()) {
            selectedFilter[index++] = "3";
        } 
        if (ratingFourChkBox.isSelected()) {
            selectedFilter[index++] = "4";
        } 
        if (ratingFiveChkBox.isSelected()) {
            selectedFilter[index++] = "5";
        }
        selectedFilter = Arrays.copyOf(selectedFilter, index); //adjust the size of array
        //call displayreview filter
        displayReviews(selectedFilter);
    }//GEN-LAST:event_filterBtnActionPerformed

    private void revertBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertBtnActionPerformed
        ratingOneChkBox.setSelected(false);
        ratingTwoChkBox.setSelected(false);
        ratingThreeChkBox.setSelected(false);
        ratingFourChkBox.setSelected(false);
        ratingFiveChkBox.setSelected(false);
        sortComboBox.setSelectedItem("Sort Rating");
        displayReviews();
    }//GEN-LAST:event_revertBtnActionPerformed

    private void sortBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortBtnActionPerformed
        //get the combo box
        String sorting = (String) sortComboBox.getSelectedItem();
        if (sorting.equals("Sort Rating")) {
            JOptionPane.showMessageDialog(null, "Please select sorting order.", "Alert", JOptionPane.WARNING_MESSAGE);
        } else {
            displayReviews(sorting);
        }        
    }//GEN-LAST:event_sortBtnActionPerformed

    private void weeklyDateChooserjDateChooserInput(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_weeklyDateChooserjDateChooserInput
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
    }//GEN-LAST:event_weeklyDateChooserjDateChooserInput

    private void weeklyChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weeklyChartBtnActionPerformed
        //get start date and end date, combine into a string and call ratingCount in review class
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date chosenDate = weeklyDateChooser.getDate();
        String startDate = dateFormat.format(chosenDate);

        String endDate = weeklyEndDateTxt.getText();
        String timeRange = startDate + "," + endDate;

        generateChart("weekly", timeRange);
    }//GEN-LAST:event_weeklyChartBtnActionPerformed

    private void monthChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthChartBtnActionPerformed
        //get month and year
        String month = String.valueOf(chartMonthChooser.getMonth() + 1); //index of month + 1
        String year = String.valueOf(chartYearChooser.getYear());
        String timeRange = month + "," + year;
        Vendor vendor = new Vendor(email);
        //get rating count
        int[] ratingsCount = new Review().ratingCount(email, "vendor", "monthly", timeRange);

        //call method to generate chart - pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();
        String[] ratingLabels = {"1", "2", "3", "4", "5"};

        for (int i = 0; i < ratingsCount.length; i++) { //adding label and count into dataset
            if (ratingsCount[i] > 0) { //only add ratings with more than 0 count
                dataset.setValue(ratingLabels[i], ratingsCount[i]);
            }
        }

        ChartPanel chartPanel = ChartUtility.createPieChart(dataset, "Monthly Rating");

        monthlyChartPanel.removeAll();
        monthlyChartPanel.setLayout(new BorderLayout());
        monthlyChartPanel.add(chartPanel, BorderLayout.CENTER);
        monthlyChartPanel.revalidate();
        monthlyChartPanel.repaint();
    }//GEN-LAST:event_monthChartBtnActionPerformed

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
            java.util.logging.Logger.getLogger(vendorReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(vendorReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(vendorReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(vendorReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new vendorReview().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JMonthChooser chartMonthChooser;
    private javax.swing.JLabel chartWeekRange;
    private javax.swing.JLabel chartWeekRange1;
    private javax.swing.JLabel chartWeekRange3;
    private javax.swing.JLabel chartWeekRange4;
    private com.toedter.calendar.JYearChooser chartYearChooser;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JButton filterBtn;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JButton monthChartBtn;
    private javax.swing.JPanel monthlyChartPanel;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JCheckBox ratingFiveChkBox;
    private javax.swing.JCheckBox ratingFourChkBox;
    private javax.swing.JLabel ratingLabel;
    private javax.swing.JCheckBox ratingOneChkBox;
    private javax.swing.JCheckBox ratingThreeChkBox;
    private javax.swing.JCheckBox ratingTwoChkBox;
    private javax.swing.JButton revertBtn;
    private javax.swing.JTable reviewTable;
    private javax.swing.JButton sortBtn;
    private javax.swing.JComboBox<String> sortComboBox;
    private javax.swing.JTextArea vendorFeedbackTxtArea;
    private javax.swing.JButton viewBtn;
    private javax.swing.JButton weeklyChartBtn;
    private javax.swing.JPanel weeklyChartPanel;
    private com.toedter.calendar.JDateChooser weeklyDateChooser;
    private javax.swing.JLabel weeklyEndDateTxt;
    // End of variables declaration//GEN-END:variables
}
