/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author mastu
 */
public class vendorReview extends javax.swing.JFrame {

    private String email = User.getSessionEmail();
    Vendor vendor = new Vendor(email);

    /**
     * Creates new form vendorReview
     */
    public vendorReview() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186, 85, 211)); //setting background color of frame
        setLocationRelativeTo(null);

        displayReviews();
        reviewTableListener();
        ratingOneChkBox.setSelected(true);
        ratingTwoChkBox.setSelected(true);
        ratingThreeChkBox.setSelected(true);
        ratingFourChkBox.setSelected(true);
        ratingFiveChkBox.setSelected(true);
        vendorFeedbackTxtArea.setEditable(false);

        //text wrap for feedback
        vendorFeedbackTxtArea.setLineWrap(true);
        vendorFeedbackTxtArea.setWrapStyleWord(true);

    }

    //display all vendor reviews
    public void displayReviews() {
        DefaultTableModel model = (DefaultTableModel) reviewTable.getModel();
        int index = 1;
        model.setRowCount(0);
        List<String[]> allReviews = new Review().getAllReviews(email, "vendor");
        for (String[] reviewData : allReviews) {

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

            String reviewID = reviewData[0];
            String rating = reviewData[3];
            String feedback = reviewData[4];
            String reviewDate = reviewData[5];
            String customerEmail = reviewData[6];

            model.addRow(new Object[]{index++, reviewID, customerEmail, reviewDate, rating + "🌟", feedback});
        }
    }

    //display selected vendor review 
    public void displaySelectedReview(int reviewID) {
        DecimalFormat df = new DecimalFormat("0.00");
        String[] details = new Review().getReview(reviewID);

        if (details == null) {
            JOptionPane.showMessageDialog(null, "No review found for ID: " + reviewID, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (details.length < 10) {
            JOptionPane.showMessageDialog(null, "Review data is incomplete for ID: " + reviewID, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //get items, price, and quantity to display in table
        Item item = new Item();
        String orderItems = details[9].trim();

        //remove square brackets and split the items by "|"
        String[] itemDetails = orderItems.replace("[", "").replace("]", "").split("\\|");

        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.setRowCount(0);

        for (String detail : itemDetails) {
            String[] parts = detail.split(";");
            if (parts.length < 2) {
                continue;
            }

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

        try {
            idLabel.setText(details[1].trim()); // Order ID
            dateLabel.setText(details[5].trim());
            methodLabel.setText(details[8].trim().substring(0, 1).toUpperCase() + details[8].trim().substring(1).toLowerCase());
            emailLabel.setText(details[6].trim());
            priceLabel.setText("RM" + details[17].trim());
            ratingLabel.setText(details[3].trim() + " 🌟");
            vendorFeedbackTxtArea.setText(details[4].trim());
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(null, "Review data structure is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //generate weekly, monthly, quarterly, yearly chart
    public void generateChart(String type, String timeRange) {
        int[] ratingsCount = new int[5];
        if (type.equalsIgnoreCase("weekly")) {
            //get rating count
            ratingsCount = new Review().ratingCount(email, "vendor", "weekly", timeRange);
        } else if (type.equalsIgnoreCase("monthly")) {
            ratingsCount = new Review().ratingCount(email, "vendor", "monthly", timeRange);
        } else if (type.equalsIgnoreCase("quarterly")) {
            ratingsCount = new Review().ratingCount(email, "vendor", "quarterly", timeRange);
        } else if (type.equalsIgnoreCase("yearly")) {
            ratingsCount = new Review().ratingCount(email, "vendor", "yearly", timeRange);
        }

        //check if all ratings are 0
        boolean allZero = true;
        for (int i = 0; i < ratingsCount.length; i++) {
            if (ratingsCount[i] > 0) {
                allZero = false;
                break;
            }
        }

        //display message and clear chart if all items is 0 
        if (allZero) {
            JOptionPane.showMessageDialog(null, "No ratings were given for the period.", "No Rating Data", JOptionPane.INFORMATION_MESSAGE);
            DefaultPieDataset blankDataset = new DefaultPieDataset();
            blankDataset.setValue("No Data", 0);
            if (type.equalsIgnoreCase("weekly")) {
                weeklyChartPanel.removeAll();
                weeklyChartPanel.setLayout(new BorderLayout());
                weeklyChartPanel.add(ChartUtility.createPieChart(blankDataset, "No Data Available"), java.awt.BorderLayout.CENTER);
                weeklyChartPanel.revalidate();
                weeklyChartPanel.repaint();
            } else if (type.equalsIgnoreCase("monthly")) {
                monthlyChartPanel.removeAll();
                monthlyChartPanel.setLayout(new BorderLayout());
                monthlyChartPanel.add(ChartUtility.createPieChart(blankDataset, "No Data Available"), java.awt.BorderLayout.CENTER);
                monthlyChartPanel.revalidate();
                monthlyChartPanel.repaint();
            } else if (type.equalsIgnoreCase("quarterly")) {
                quarterlyChartPanel.removeAll();
                quarterlyChartPanel.setLayout(new BorderLayout());
                quarterlyChartPanel.add(ChartUtility.createPieChart(blankDataset, "No Data Available"), java.awt.BorderLayout.CENTER);
                quarterlyChartPanel.revalidate();
                quarterlyChartPanel.repaint();
            } else if (type.equalsIgnoreCase("yearly")) {
                yearlyChartPanel.removeAll();
                yearlyChartPanel.setLayout(new BorderLayout());
                yearlyChartPanel.add(ChartUtility.createPieChart(blankDataset, "No Data Available"), java.awt.BorderLayout.CENTER);
                yearlyChartPanel.revalidate();
                yearlyChartPanel.repaint();
            }
            return;
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
        } else if (type.equalsIgnoreCase("quarterly")) {
            ChartPanel chartPanel = ChartUtility.createPieChart(dataset, "Quarterly Rating");
            quarterlyChartPanel.removeAll();
            quarterlyChartPanel.setLayout(new BorderLayout());
            quarterlyChartPanel.add(chartPanel, BorderLayout.CENTER);
            quarterlyChartPanel.revalidate();
            quarterlyChartPanel.repaint();
        } else if (type.equalsIgnoreCase("yearly")) {
            ChartPanel chartPanel = ChartUtility.createPieChart(dataset, "Yearly Rating");
            yearlyChartPanel.removeAll();
            yearlyChartPanel.setLayout(new BorderLayout());
            yearlyChartPanel.add(chartPanel, BorderLayout.CENTER);
            yearlyChartPanel.revalidate();
            yearlyChartPanel.repaint();
        }
    }

    private void reviewTableListener() {
        reviewTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { 
                    int selectedRow = reviewTable.getSelectedRow();
                    if (selectedRow != -1) { //check if a row is actually selected
                        Object idObj = reviewTable.getModel().getValueAt(selectedRow, 1);
                        try {
                            if (idObj == null) {
                                throw new NumberFormatException("ID is null");
                            }

                            String cleanID = idObj.toString().trim().replaceAll("[^0-9]", "");
                            if (cleanID.isEmpty()) {
                                throw new NumberFormatException("ID is empty after cleaning");
                            }

                            int id = Integer.parseInt(cleanID);
                            displaySelectedReview(id);

                        } catch (NumberFormatException error) {
                            JOptionPane.showMessageDialog(null, "The selected ID is not a valid number: " + idObj, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
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
        jPanel1 = new javax.swing.JPanel();
        ratingOneChkBox = new javax.swing.JCheckBox();
        ratingTwoChkBox = new javax.swing.JCheckBox();
        sortComboBox = new javax.swing.JComboBox<>();
        ratingThreeChkBox = new javax.swing.JCheckBox();
        ratingFourChkBox = new javax.swing.JCheckBox();
        ratingFiveChkBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        reviewTable = new javax.swing.JTable();
        filterBtn = new javax.swing.JButton();
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
        jLabel11 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        chartWeekRange = new javax.swing.JLabel();
        weeklyDateChooser = new com.toedter.calendar.JDateChooser();
        chartWeekRange1 = new javax.swing.JLabel();
        weeklyEndDateTxt = new javax.swing.JLabel();
        weeklyChartBtn = new javax.swing.JButton();
        weeklyChartPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        chartWeekRange3 = new javax.swing.JLabel();
        chartMonthChooser = new com.toedter.calendar.JMonthChooser();
        chartWeekRange4 = new javax.swing.JLabel();
        chartYearChooser = new com.toedter.calendar.JYearChooser();
        monthChartBtn = new javax.swing.JButton();
        monthlyChartPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
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
        jPanel7 = new javax.swing.JPanel();
        chartWeekRange9 = new javax.swing.JLabel();
        chartYearlyChooser = new com.toedter.calendar.JYearChooser();
        yearlyChartBtn = new javax.swing.JButton();
        yearlyChartPanel = new javax.swing.JPanel();

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
        sortComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortComboBoxActionPerformed(evt);
            }
        });

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

        filterBtn.setText("Filter");
        filterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBtnActionPerformed(evt);
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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
                        .addGap(405, 405, 405)
                        .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 874, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ratingOneChkBox)
                    .addComponent(ratingTwoChkBox)
                    .addComponent(ratingThreeChkBox)
                    .addComponent(ratingFourChkBox)
                    .addComponent(ratingFiveChkBox)
                    .addComponent(filterBtn)
                    .addComponent(sortComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(revertBtn)
                .addContainerGap(15, Short.MAX_VALUE))
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

        dateLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        emailLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        methodLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        ratingLabel.setFont(new java.awt.Font("Segoe UI Emoji", 0, 14)); // NOI18N

        jLabel12.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel12.setText("Total Price: ");

        priceLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

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

        jLabel11.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel11.setText("Rating Summary Report");

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
            .addGap(0, 300, Short.MAX_VALUE)
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
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chartWeekRange)
                                    .addComponent(chartWeekRange1))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(weeklyEndDateTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(weeklyDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(weeklyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(39, Short.MAX_VALUE))
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
                .addGap(28, 28, 28)
                .addComponent(weeklyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(102, Short.MAX_VALUE))
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

        monthlyChartPanel.setMaximumSize(new java.awt.Dimension(467, 300));
        monthlyChartPanel.setMinimumSize(new java.awt.Dimension(467, 300));
        monthlyChartPanel.setPreferredSize(new java.awt.Dimension(467, 300));

        javax.swing.GroupLayout monthlyChartPanelLayout = new javax.swing.GroupLayout(monthlyChartPanel);
        monthlyChartPanel.setLayout(monthlyChartPanelLayout);
        monthlyChartPanelLayout.setHorizontalGroup(
            monthlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        monthlyChartPanelLayout.setVerticalGroup(
            monthlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chartWeekRange3)
                            .addComponent(chartWeekRange4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(chartMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chartYearChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(monthChartBtn))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(monthlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartWeekRange3)
                    .addComponent(chartMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartWeekRange4))
                .addGap(33, 33, 33)
                .addComponent(monthChartBtn)
                .addGap(50, 50, 50)
                .addComponent(monthlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Monthly", jPanel5);

        chartWeekRange5.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange5.setText("Year:");

        chartQuarterYearChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chartQuarterYearChooserdisplayYear(evt);
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
                quarterChartBoxchooseQuarter(evt);
            }
        });

        chartWeekRange7.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange7.setText("Start Date:");

        startDateQuarterLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        endDateQuarterLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        chartWeekRange8.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange8.setText("End Date:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(quarterChartBtn)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(chartWeekRange6)
                                    .addComponent(chartWeekRange5))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(quarterChartBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(chartQuarterYearChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chartWeekRange7)
                                    .addComponent(chartWeekRange8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(startDateQuarterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                    .addComponent(endDateQuarterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(quarterlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chartWeekRange6)
                    .addComponent(quarterChartBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartWeekRange7)
                    .addComponent(startDateQuarterLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartQuarterYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartWeekRange5)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chartWeekRange8)
                        .addComponent(endDateQuarterLabel)))
                .addGap(33, 33, 33)
                .addComponent(quarterChartBtn)
                .addGap(50, 50, 50)
                .addComponent(quarterlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(105, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Quarterly", jPanel6);

        chartWeekRange9.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        chartWeekRange9.setText("Year:");

        yearlyChartBtn.setText("Generate Chart");
        yearlyChartBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearlyChartBtnActionPerformed(evt);
            }
        });

        yearlyChartPanel.setMaximumSize(new java.awt.Dimension(467, 300));
        yearlyChartPanel.setMinimumSize(new java.awt.Dimension(467, 300));
        yearlyChartPanel.setPreferredSize(new java.awt.Dimension(467, 300));

        javax.swing.GroupLayout yearlyChartPanelLayout = new javax.swing.GroupLayout(yearlyChartPanel);
        yearlyChartPanel.setLayout(yearlyChartPanelLayout);
        yearlyChartPanelLayout.setHorizontalGroup(
            yearlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
        );
        yearlyChartPanelLayout.setVerticalGroup(
            yearlyChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yearlyChartBtn)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(chartWeekRange9)
                                .addGap(18, 18, 18)
                                .addComponent(chartYearlyChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(yearlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartYearlyChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartWeekRange9))
                .addGap(33, 33, 33)
                .addComponent(yearlyChartBtn)
                .addGap(18, 18, 18)
                .addComponent(yearlyChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(177, 177, 177))
        );

        jTabbedPane1.addTab("Yearly", jPanel7);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
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
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(469, 469, 469)
                        .addComponent(menuBtn)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menuBtn)
                    .addComponent(jLabel1))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

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
                generateChart("weekly", timeRange);
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
        generateChart("monthly", timeRange);
    }//GEN-LAST:event_monthChartBtnActionPerformed

    private void chartQuarterYearChooserdisplayYear(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chartQuarterYearChooserdisplayYear
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
        } else {
            GuiUtility.clearFields(startDateQuarterLabel, endDateQuarterLabel);
        }
    }//GEN-LAST:event_chartQuarterYearChooserdisplayYear

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
            generateChart("quarterly", timeRange);
        }
    }//GEN-LAST:event_quarterChartBtnActionPerformed

    private void quarterChartBoxchooseQuarter(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quarterChartBoxchooseQuarter
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
    }//GEN-LAST:event_quarterChartBoxchooseQuarter

    private void yearlyChartBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearlyChartBtnActionPerformed
        String year = String.valueOf(chartYearlyChooser.getYear());
        String timeRange = year;
        generateChart("yearly", timeRange);
    }//GEN-LAST:event_yearlyChartBtnActionPerformed

    private void sortComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortComboBoxActionPerformed
        //get the combo box
        String sorting = (String) sortComboBox.getSelectedItem();
        if (sorting.equals("Sort Rating")) {
            displayReviews();
        } else {
            displayReviews(sorting);
        }
    }//GEN-LAST:event_sortComboBoxActionPerformed

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
    private com.toedter.calendar.JYearChooser chartQuarterYearChooser;
    private javax.swing.JLabel chartWeekRange;
    private javax.swing.JLabel chartWeekRange1;
    private javax.swing.JLabel chartWeekRange3;
    private javax.swing.JLabel chartWeekRange4;
    private javax.swing.JLabel chartWeekRange5;
    private javax.swing.JLabel chartWeekRange6;
    private javax.swing.JLabel chartWeekRange7;
    private javax.swing.JLabel chartWeekRange8;
    private javax.swing.JLabel chartWeekRange9;
    private com.toedter.calendar.JYearChooser chartYearChooser;
    private com.toedter.calendar.JYearChooser chartYearlyChooser;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JLabel endDateQuarterLabel;
    private javax.swing.JButton filterBtn;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JButton monthChartBtn;
    private javax.swing.JPanel monthlyChartPanel;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JComboBox<String> quarterChartBox;
    private javax.swing.JButton quarterChartBtn;
    private javax.swing.JPanel quarterlyChartPanel;
    private javax.swing.JCheckBox ratingFiveChkBox;
    private javax.swing.JCheckBox ratingFourChkBox;
    private javax.swing.JLabel ratingLabel;
    private javax.swing.JCheckBox ratingOneChkBox;
    private javax.swing.JCheckBox ratingThreeChkBox;
    private javax.swing.JCheckBox ratingTwoChkBox;
    private javax.swing.JButton revertBtn;
    private javax.swing.JTable reviewTable;
    private javax.swing.JComboBox<String> sortComboBox;
    private javax.swing.JLabel startDateQuarterLabel;
    private javax.swing.JTextArea vendorFeedbackTxtArea;
    private javax.swing.JButton weeklyChartBtn;
    private javax.swing.JPanel weeklyChartPanel;
    private com.toedter.calendar.JDateChooser weeklyDateChooser;
    private javax.swing.JLabel weeklyEndDateTxt;
    private javax.swing.JButton yearlyChartBtn;
    private javax.swing.JPanel yearlyChartPanel;
    // End of variables declaration//GEN-END:variables
}
