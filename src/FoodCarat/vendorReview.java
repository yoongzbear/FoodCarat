/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

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
        
        displayReviews();
        vendorFeedbackTxtArea.setEditable(false);
        
    }

    //display all vendor reviews
    public void displayReviews() {
        DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
        int index = 1;
        model.setRowCount(0);
        //access vendor orders through vendor class
        Vendor vendor = new Vendor(email);
        List<String[]> allReviews = vendor.getAllReviewInfo(email);
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
        Vendor vendor = new Vendor(email);
        List<String[]> allReviews = vendor.getAllReviewInfo(email);
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
        Vendor vendor = new Vendor(email);
        List<String[]> allReviews = vendor.getAllReviewInfo(email);

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
        Review selectedReview = new Review();
        String[] details = selectedReview.getReview(reviewID);
        
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
        
        //5, 3, vendor, 3, Kena cancelled, 2024-01-01, customer@mail.com, 3, Take away, [2;1|4;1], Ordered, customerEmail, NULL, NULL, NULL, 20.00, 2025-01-01 , 25.80
        idLabel.setText(details[1].trim()); //order ID
        dateLabel.setText(details[5].trim());
        methodLabel.setText(details[8].trim());
        emailLabel.setText(details[6].trim());
        priceLabel.setText("RM"+details[17].trim());
        ratingLabel.setText(details[3].trim() + " star");
        vendorFeedbackTxtArea.setText(details[4].trim());
    }
    
    //generate weekly chart
    public void generateChart() {
        //placeholder dulu
        //if all rating is 0, show message no ratings received for the week/month
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
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        weeklyStartDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        weeklyEndDateTxt = new javax.swing.JLabel();
        jMonthChooser1 = new com.toedter.calendar.JMonthChooser();
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        weeklyChartBtn = new javax.swing.JButton();
        monthlyChartBtn = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
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
                        .addComponent(sortBtn))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 646, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(revertBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(viewBtn)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewBtn)
                    .addComponent(revertBtn))
                .addGap(18, 18, 18))
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
        idLabel.setText("ID");

        dateLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        dateLabel.setText("Date");

        emailLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        emailLabel.setText("Email");

        methodLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        methodLabel.setText("Method");

        ratingLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        ratingLabel.setText("Rating");

        jLabel12.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel12.setText("Total Price: ");

        priceLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        priceLabel.setText("RM");

        vendorFeedbackTxtArea.setColumns(20);
        vendorFeedbackTxtArea.setRows(5);
        jScrollPane2.setViewportView(vendorFeedbackTxtArea);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ratingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(91, 91, 91)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(32, 32, 32)
                        .addComponent(methodLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(36, 36, 36)
                        .addComponent(priceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel8)
                    .addComponent(idLabel)
                    .addComponent(dateLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(emailLabel))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(priceLabel))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(methodLabel)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(ratingLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel14.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel14.setText("Weekly");

        jLabel15.setText("pie chart");
        jLabel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel16.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel16.setText("Monthly");

        jLabel17.setText("pie chart");
        jLabel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        weeklyStartDateChooser.setDateFormatString("yyyy-MM-dd");
        weeklyStartDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooserInput(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel18.setText("Select Starting Date:");

        jLabel19.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel19.setText("End Date:");

        weeklyEndDateTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        weeklyEndDateTxt.setText("yyyy-MM-dd");

        weeklyChartBtn.setText("Generate Chart");
        weeklyChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weeklyChartBtnActionPerformed(evt);
            }
        });

        monthlyChartBtn.setText("Generate Chart");
        monthlyChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthlyChartBtnActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel20.setText("Select Month and Year:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(weeklyStartDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                .addComponent(jLabel18)
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(weeklyEndDateTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(weeklyChartBtn))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(monthlyChartBtn)
                            .addComponent(jLabel20)
                            .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(monthlyChartBtn))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(weeklyStartDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(weeklyEndDateTxt)
                                .addGap(18, 18, 18)
                                .addComponent(weeklyChartBtn)))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(389, 389, 389)
                        .addComponent(menuBtn)
                        .addGap(71, 71, 71))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(42, Short.MAX_VALUE))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
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
        // TODO add your handling code here:
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

    private void weeklyChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weeklyChartBtnActionPerformed
        //get start date and end date, combine into a string and call ratingCount in review class
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date chosenDate = weeklyStartDateChooser.getDate();
        String startDate = dateFormat.format(chosenDate);
        
        String endDate = weeklyEndDateTxt.getText();
        String timeRange = startDate + "," + endDate;
        System.out.println(timeRange);
        
        Vendor vendor = new Vendor(email);
        
        //get rating count
        int[] ratingsCount = vendor.getVendorRatingCount("weekly", timeRange);
        
        //pass into chart making function - pie chart
    }//GEN-LAST:event_weeklyChartBtnActionPerformed

    private void jDateChooserInput(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooserInput
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date chosenDate = weeklyStartDateChooser.getDate();
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
                weeklyStartDateChooser.setDate(calendar.getTime());
                JOptionPane.showMessageDialog(null, "Only Mondays are allowed to be selected. The next Monday is selected.");
            }
            
            //calculate and display end date
            calendar.add(Calendar.DAY_OF_MONTH, 6); //Monday to Sunday
            Date endDate = calendar.getTime();
            weeklyEndDateTxt.setText(dateFormat.format(endDate));
        }

    }//GEN-LAST:event_jDateChooserInput

    private void monthlyChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthlyChartBtnActionPerformed
        //get month and year
        //month index + 1
        
        //send to timeRange
    }//GEN-LAST:event_monthlyChartBtnActionPerformed

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
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JButton filterBtn;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.toedter.calendar.JMonthChooser jMonthChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JButton monthlyChartBtn;
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
    private javax.swing.JLabel weeklyEndDateTxt;
    private com.toedter.calendar.JDateChooser weeklyStartDateChooser;
    // End of variables declaration//GEN-END:variables
}
