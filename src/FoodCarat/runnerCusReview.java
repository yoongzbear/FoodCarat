/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Yuna
 */
public class runnerCusReview extends javax.swing.JFrame {
    private String runnerEmail = User.getSessionEmail();
    private String Role = "runner";
    private List<String[]> currentData;

    public runnerCusReview() {
        initComponents();
        setLocationRelativeTo(null);
        
        addCheckBoxListeners();
        fetchData(null, null);
        chartAllData();
    }


    // Update the table
    private void fetchData(LocalDate startDate, LocalDate endDate) {
        List<String[]> cusReview = new Review().getAllReviews(runnerEmail, Role);
        cusReview.sort((review1, review2) -> review2[5].compareTo(review1[5]));

        currentData = new ArrayList<>();

        for (String[] reviewInfo : cusReview) {
            String reviewDate = reviewInfo[5];
            LocalDate date = LocalDate.parse(reviewDate);

            // Filter by date range if provided
            if ((startDate == null || !date.isBefore(startDate)) &&
                (endDate == null || !date.isAfter(endDate))) {
                currentData.add(reviewInfo);
            }
        }

        // Apply any selected checkbox
        filterDataByCheckBox();
    }

    // Filter the current data based on checkbox selections
    private void filterDataByCheckBox() {
        Set<String> selectedRatings = new HashSet<>();
        if (oneJCB.isSelected()) selectedRatings.add("1");
        if (twoJCB.isSelected()) selectedRatings.add("2");
        if (threeJCB.isSelected()) selectedRatings.add("3");
        if (fourJCB.isSelected()) selectedRatings.add("4");
        if (fiveJCB.isSelected()) selectedRatings.add("5");

        DefaultTableModel model = (DefaultTableModel) cusReviewJT.getModel();
        model.setRowCount(0);
        
        // Populate the table with filtered data
        for (String[] reviewInfo : currentData) {
            String rank = reviewInfo[3];

            // Check if the rank matches selected ratings or if no ratings are selected
            if (selectedRatings.isEmpty() || selectedRatings.contains(rank)) {
                model.addRow(new Object[]{
                    reviewInfo[0],
                    rank,
                    reviewInfo[5]
                });
            }
        }
    }

    // Apply filters and refresh table
    private void applyFilters() {
        filterDataByCheckBox();
    }

    // for rank's filter
    private void addCheckBoxListeners() {
        ActionListener filterListener = e -> applyFilters();
        oneJCB.addActionListener(filterListener);
        twoJCB.addActionListener(filterListener);
        threeJCB.addActionListener(filterListener);
        fourJCB.addActionListener(filterListener);
        fiveJCB.addActionListener(filterListener);
    }
    
    private void chartAllData() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusMonths(4);

        // store the total count of each rating (1-5)
        Map<Integer, Integer> ratingCounts = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i, 0);
        }

        DefaultTableModel model = (DefaultTableModel) cusReviewJT.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 2);
            String rankStr = (String) model.getValueAt(i, 1);
            LocalDate reviewDate = LocalDate.parse(dateString);

            if (!reviewDate.isBefore(startDate)) {
                int rank = Integer.parseInt(rankStr);
                ratingCounts.put(rank, ratingCounts.getOrDefault(rank, 0) + 1);
            }
        }

        if (ratingCounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data available for the selected date range.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            displayBarChart(dataset, "No Available Data", "Ranks", "Total Count");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : ratingCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Total Count of Ratings", String.valueOf(entry.getKey()));
        }

        displayBarChart(dataset, "Ratings Over Last 5 Months", "Ranks", "Total Count");
    }

    // Generate Daily Bar Chart
    private void generateDailyBarChart(LocalDate startDate, LocalDate endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) cusReviewJT.getModel();

        Map<Integer, Integer> ratingCounts = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i, 0);
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 2);
            String rankStr = (String) model.getValueAt(i, 1);
            LocalDate reviewDate = LocalDate.parse(dateString);

            if (!reviewDate.isBefore(startDate) && !reviewDate.isAfter(endDate)) {
                int rank = Integer.parseInt(rankStr);
                ratingCounts.put(rank, ratingCounts.getOrDefault(rank, 0) + 1);
            }
        }

        if (ratingCounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data available for the selected date range.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            displayBarChart(dataset, "No Available Data", "Ranks", "Total Count");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : ratingCounts.entrySet()) {
            int rank = entry.getKey();
            int count = entry.getValue();
            dataset.addValue(count, "Total Count of Ratings", String.valueOf(rank));
        }

        displayBarChart(dataset, "Rantings from " + startDate + " to " + endDate, "Ranks", "Total Count");
    }

    // Generate monthly bar chart
    private void generateMonthlyBarChart(LocalDate startDate, LocalDate endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) cusReviewJT.getModel();

        Map<Integer, Integer> rankCounts = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            rankCounts.put(i, 0);
        }
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 2);
            String rankStr = (String) model.getValueAt(i, 1);
            LocalDate reviewDate = LocalDate.parse(dateString);

            if (!reviewDate.isBefore(startDate) && !reviewDate.isAfter(endDate)) {
                int rank = Integer.parseInt(rankStr);
                rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            }
        }

        if (rankCounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data available for the selected date range.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            displayBarChart(dataset, "No Available Data", "Ranks", "Total Count");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Total Count of Ratings", String.valueOf(entry.getKey()));
        }

        displayBarChart(dataset, "Rantings from " + startDate + " to " + endDate, "Ranks", "Total Count");
    }

    // Generate yearly bar chart
    private void generateYearlyBarChart(int startYear, int endYear) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) cusReviewJT.getModel();
        
        Map<Integer, Integer> rankCounts = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            rankCounts.put(i, 0);
        }
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 2);
            String rankStr = (String) model.getValueAt(i, 1);
            LocalDate reviewDate = LocalDate.parse(dateString);

            int reviewYear = reviewDate.getYear();
            if (reviewYear >= startYear && reviewYear <= endYear) {
                int rank = Integer.parseInt(rankStr);
                rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            }
        }

        if (rankCounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data available for the selected years.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            displayBarChart(dataset, "No Available Data", "Ranks", "Total Count");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : rankCounts.entrySet()) {
            dataset.addValue(entry.getValue(), "Total Count of Ratings", String.valueOf(entry.getKey()));
        }

        displayBarChart(dataset, "Rantings from " + startYear + " to " + endYear, "Ranks", "Total Count");
    }

    // Display bar chart
    private void displayBarChart(DefaultCategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
        JFreeChart barChart = ChartFactory.createBarChart(
            title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // Calculate total reviews
        int totalReviews = 0;
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Number value = dataset.getValue(0, i);
            if (value != null) {
                totalReviews += value.intValue();
            }
        }

        // Set tick unit
        if (totalReviews <= 10) {
            rangeAxis.setTickUnit(new NumberTickUnit(1));
        } else {
            rangeAxis.setTickUnit(new NumberTickUnit(10));
        }

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);

        barChartJL.removeAll();
        barChartJL.setLayout(new BorderLayout());
        barChartJL.add(chartPanel, BorderLayout.CENTER);
        barChartJL.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        dayJP = new javax.swing.JPanel();
        endDateChooser = new com.toedter.calendar.JDateChooser();
        DgenerateJB = new javax.swing.JButton();
        startDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        monthJP = new javax.swing.JPanel();
        MgenerateJB = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        endMonthChooser = new com.toedter.calendar.JDateChooser();
        startMonthChooser = new com.toedter.calendar.JDateChooser();
        yearJP = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        startYearChooser = new com.toedter.calendar.JYearChooser();
        endYearChooser = new com.toedter.calendar.JYearChooser();
        YgenerateJB = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        backJB = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        cusReviewJT = new javax.swing.JTable();
        oneJCB = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        twoJCB = new javax.swing.JCheckBox();
        threeJCB = new javax.swing.JCheckBox();
        fourJCB = new javax.swing.JCheckBox();
        fiveJCB = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        barChartJL = new javax.swing.JLabel();
        allJB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        dayJP.setBackground(new java.awt.Color(255, 255, 255));
        dayJP.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        DgenerateJB.setText("Generate");
        DgenerateJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DgenerateJBActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("-");

        javax.swing.GroupLayout dayJPLayout = new javax.swing.GroupLayout(dayJP);
        dayJP.setLayout(dayJPLayout);
        dayJPLayout.setHorizontalGroup(
            dayJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dayJPLayout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(startDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(DgenerateJB)
                .addGap(22, 22, 22))
        );
        dayJPLayout.setVerticalGroup(
            dayJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dayJPLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(dayJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dayJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(startDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(endDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dayJPLayout.createSequentialGroup()
                        .addComponent(DgenerateJB)
                        .addGap(3, 3, 3)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Day", dayJP);

        monthJP.setBackground(new java.awt.Color(255, 255, 255));
        monthJP.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        MgenerateJB.setText("Generate");
        MgenerateJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MgenerateJBActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("-");

        javax.swing.GroupLayout monthJPLayout = new javax.swing.GroupLayout(monthJP);
        monthJP.setLayout(monthJPLayout);
        monthJPLayout.setHorizontalGroup(
            monthJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(monthJPLayout.createSequentialGroup()
                .addContainerGap(43, Short.MAX_VALUE)
                .addComponent(startMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MgenerateJB)
                .addGap(32, 32, 32))
        );
        monthJPLayout.setVerticalGroup(
            monthJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, monthJPLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(monthJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(MgenerateJB)
                    .addComponent(startMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(endMonthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        jTabbedPane1.addTab("Month", monthJP);

        yearJP.setBackground(new java.awt.Color(255, 255, 255));
        yearJP.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("-");

        YgenerateJB.setText("Generate");
        YgenerateJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                YgenerateJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout yearJPLayout = new javax.swing.GroupLayout(yearJP);
        yearJP.setLayout(yearJPLayout);
        yearJPLayout.setHorizontalGroup(
            yearJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yearJPLayout.createSequentialGroup()
                .addGap(127, 127, 127)
                .addComponent(startYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(YgenerateJB)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        yearJPLayout.setVerticalGroup(
            yearJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(yearJPLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(yearJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(yearJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(endYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(startYearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(yearJPLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(YgenerateJB)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Year", yearJP);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Customer Review");

        backJB.setText("Main Menu");
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
                .addGap(265, 265, 265)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addGap(129, 129, 129)
                .addComponent(backJB)
                .addGap(20, 20, 20))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(backJB))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        cusReviewJT.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        cusReviewJT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Review ID", "Rating (1 - 5)", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(cusReviewJT);
        if (cusReviewJT.getColumnModel().getColumnCount() > 0) {
            cusReviewJT.getColumnModel().getColumn(0).setPreferredWidth(10);
            cusReviewJT.getColumnModel().getColumn(1).setPreferredWidth(20);
            cusReviewJT.getColumnModel().getColumn(2).setPreferredWidth(100);
        }

        oneJCB.setText("1 🌟");

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel2.setText("Filter:");

        twoJCB.setText("2 🌟");

        threeJCB.setText("3 🌟");

        fourJCB.setText("4 🌟");

        fiveJCB.setText("5 🌟");

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        barChartJL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        barChartJL.setText("Bar Chart");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(barChartJL, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(barChartJL, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );

        allJB.setText("Show All");
        allJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(oneJCB)
                                        .addGap(18, 18, 18)
                                        .addComponent(twoJCB)
                                        .addGap(18, 18, 18)
                                        .addComponent(threeJCB)
                                        .addGap(18, 18, 18)
                                        .addComponent(fourJCB)
                                        .addGap(18, 18, 18)
                                        .addComponent(fiveJCB))
                                    .addComponent(allJB, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 37, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(allJB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(oneJCB)
                            .addComponent(twoJCB)
                            .addComponent(threeJCB)
                            .addComponent(fourJCB)
                            .addComponent(fiveJCB)))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backJBActionPerformed
        dispose();
        new runnerMain().setVisible(true);
    }//GEN-LAST:event_backJBActionPerformed

    private void DgenerateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DgenerateJBActionPerformed
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "End date cannot be earlier than start date.", "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert to LocalDate for compatibility with the generateDailyBarChart method
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        fetchData(startLocalDate, endLocalDate);
        generateDailyBarChart(startLocalDate, endLocalDate);
    }//GEN-LAST:event_DgenerateJBActionPerformed

    private void MgenerateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MgenerateJBActionPerformed
        Date start = startMonthChooser.getDate();
        Date end = endMonthChooser.getDate();

        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (end.before(start)) {
            JOptionPane.showMessageDialog(this, "End date cannot be earlier than start date.", "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDate startLocalDate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Adjust start and end date
        startLocalDate = startLocalDate.withDayOfMonth(1);
        endLocalDate = endLocalDate.withDayOfMonth(endLocalDate.lengthOfMonth());

        fetchData(startLocalDate, endLocalDate);
        generateMonthlyBarChart(startLocalDate, endLocalDate);
    }//GEN-LAST:event_MgenerateJBActionPerformed

    private void YgenerateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YgenerateJBActionPerformed
        int startYear = startYearChooser.getYear();
        int endYear = endYearChooser.getYear();

        if (startYear == 0 || endYear == 0) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (startYear > endYear) {
            JOptionPane.showMessageDialog(this, "End date cannot be earlier than start date.", "Invalid Date Range", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert the start and end years to LocalDate (start of the year and end of the year)
        LocalDate startLocalDate = LocalDate.of(startYear, 1, 1);
        LocalDate endLocalDate = LocalDate.of(endYear, 12, 31);

        fetchData(startLocalDate, endLocalDate);
        generateYearlyBarChart(startYear, endYear);
    }//GEN-LAST:event_YgenerateJBActionPerformed

    private void allJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allJBActionPerformed
        fetchData(null, null);
        chartAllData();
    }//GEN-LAST:event_allJBActionPerformed

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
            java.util.logging.Logger.getLogger(runnerCusReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(runnerCusReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(runnerCusReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(runnerCusReview.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new runnerCusReview().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DgenerateJB;
    private javax.swing.JButton MgenerateJB;
    private javax.swing.JButton YgenerateJB;
    private javax.swing.JButton allJB;
    private javax.swing.JButton backJB;
    private javax.swing.JLabel barChartJL;
    private javax.swing.JTable cusReviewJT;
    private javax.swing.JPanel dayJP;
    private com.toedter.calendar.JDateChooser endDateChooser;
    private com.toedter.calendar.JDateChooser endMonthChooser;
    private com.toedter.calendar.JYearChooser endYearChooser;
    private javax.swing.JCheckBox fiveJCB;
    private javax.swing.JCheckBox fourJCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel monthJP;
    private javax.swing.JCheckBox oneJCB;
    private com.toedter.calendar.JDateChooser startDateChooser;
    private com.toedter.calendar.JDateChooser startMonthChooser;
    private com.toedter.calendar.JYearChooser startYearChooser;
    private javax.swing.JCheckBox threeJCB;
    private javax.swing.JCheckBox twoJCB;
    private javax.swing.JPanel yearJP;
    // End of variables declaration//GEN-END:variables
}