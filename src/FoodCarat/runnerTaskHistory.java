/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

public class runnerTaskHistory extends javax.swing.JFrame {

    private String runnerEmail = User.getSessionEmail();

    /**
     * Creates new form runnerTaskHistory
     */
    public runnerTaskHistory() {
        initComponents();
        setLocationRelativeTo(null);

        fetchFilteredData(null, null);
        chartAllData();
    }

    // Update the table
    private void fetchFilteredData(LocalDate startDate, LocalDate endDate) {
        List<String[]> completedTasks = new Order().getCompletedTask(runnerEmail);
        completedTasks.sort((task1, task2) -> task2[9].compareTo(task1[9]));

        DefaultTableModel model = (DefaultTableModel) taskHistoryJT.getModel();
        model.setRowCount(0);

        for (String[] task : completedTasks) {
            String orderDate = task[9];
            LocalDate taskDate = LocalDate.parse(orderDate);

            if ((startDate == null || !taskDate.isBefore(startDate))
                    && (endDate == null || !taskDate.isAfter(endDate))) {

                String orderId = task[0];
                String cusEmail = task[4];
                String[] cusInfo = new User().getUserInfo(cusEmail);
                String cusName = cusInfo[1];

                String itemIDString = task[2];
                itemIDString = itemIDString.replaceAll("[\\[\\]]", "");
                String[] itemIDs = itemIDString.split(";");

                String firstItemID = itemIDs[0];
                Item item = new Item();
                String[] vendorInfo = item.getVendorInfoByItemID(Integer.parseInt(firstItemID.trim()));
                String vendorName = vendorInfo[1];

                String deliveryFee = task[7];

                model.addRow(new Object[]{
                    orderId, cusName, vendorName, orderDate, deliveryFee
                });
            }
        }
    }

    private void chartAllData() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) taskHistoryJT.getModel();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusMonths(4); // Start from 4 months ago (including current)

        Map<YearMonth, Integer> taskCounts = new LinkedHashMap<>();

        for (int i = 0; i < 5; i++) {
            YearMonth month = YearMonth.from(startDate.plusMonths(i));
            taskCounts.put(month, 0);
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 3);
            LocalDate taskDate = LocalDate.parse(dateString);
            YearMonth taskYearMonth = YearMonth.from(taskDate);

            if (taskCounts.containsKey(taskYearMonth)) {
                taskCounts.put(taskYearMonth, taskCounts.get(taskYearMonth) + 1);
            }
        }

        if (taskCounts.values().stream().allMatch(count -> count == 0)) {
            displayBarChart(dataset, "No Available Data for The Last 5 Months.", "Months", "Number of Tasks");
            return;
        }

        for (Map.Entry<YearMonth, Integer> entry : taskCounts.entrySet()) {
            String monthLabel = entry.getKey().getMonth().name() + " " + entry.getKey().getYear();
            dataset.addValue(entry.getValue(), "Tasks", monthLabel);
        }

        displayBarChart(dataset, "Tasks Over Last 5 Months", "Months", "Number of Tasks");
    }

    private void generateDailyBarChart(LocalDate startDate, LocalDate endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) taskHistoryJT.getModel();
        Map<LocalDate, Integer> taskCountMap = new TreeMap<>();

        // Count tasks for each date
        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 3);
            LocalDate taskDate = LocalDate.parse(dateString);

            if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
                taskCountMap.put(taskDate, taskCountMap.getOrDefault(taskDate, 0) + 1);
            }
        }

        if (taskCountMap.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data available for the selected date range.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            displayBarChart(dataset, "No Available Data", "Date", "Number of Tasks");
            return;
        }

        for (Map.Entry<LocalDate, Integer> entry : taskCountMap.entrySet()) {
            String day = entry.getKey().toString();
            dataset.addValue(entry.getValue(), "Tasks", day);
        }

        displayBarChart(dataset, "Number of Tasks from " + startDate + " to " + endDate, "Date", "Number of Tasks");
    }

    // Generate monthly bar chart
    private void generateMonthlyBarChart(LocalDate startDate, LocalDate endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) taskHistoryJT.getModel();

        Map<YearMonth, Integer> taskCounts = new TreeMap<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 3);
            LocalDate taskDate = LocalDate.parse(dateString);

            // Check if the taskDate is within the selected date range
            if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
                YearMonth taskYearMonth = YearMonth.from(taskDate);

                // Increment task count for this year-month
                taskCounts.put(taskYearMonth, taskCounts.getOrDefault(taskYearMonth, 0) + 1);
            }
        }

        // Handle case where no data exists
        if (taskCounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data available for the selected date range.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            displayBarChart(dataset, "No Available Data", "Months", "Number of Tasks");
            return;
        }

        for (Map.Entry<YearMonth, Integer> entry : taskCounts.entrySet()) {
            String monthLabel = entry.getKey().getMonth() + " " + entry.getKey().getYear();
            dataset.addValue(entry.getValue(), "Tasks", monthLabel);
        }

        displayBarChart(dataset, "Number of Tasks from " + startDate + " to " + endDate, "Months", "Number of Tasks");
    }

    // Generate yearly bar chart
    private void generateYearlyBarChart(int startYear, int endYear) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) taskHistoryJT.getModel();
        Map<Integer, Integer> taskCounts = new TreeMap<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 3);
            LocalDate taskDate = LocalDate.parse(dateString);
            int taskYear = taskDate.getYear();

            if (taskYear >= startYear && taskYear <= endYear) {
                taskCounts.put(taskYear, taskCounts.getOrDefault(taskYear, 0) + 1); // Increment task count for each year
            }
        }

        // Handle case where no data exists
        if (taskCounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No data available for the selected years.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            displayBarChart(dataset, "No Available Data", "Years", "Number of Tasks");
            return;
        }

        for (Map.Entry<Integer, Integer> entry : taskCounts.entrySet()) {
            String yearLabel = String.valueOf(entry.getKey());
            dataset.addValue(entry.getValue(), "Tasks", yearLabel);
        }

        displayBarChart(dataset, "Number of Tasks from " + startYear + " to " + endYear, "Years", "Number of Tasks");
    }

    // Display the bar chart
    private void displayBarChart(DefaultCategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
        JFreeChart barChart = ChartFactory.createBarChart(
                title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        int totalTasks = 0;
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Number value = dataset.getValue(0, i);
            if (value != null) {
                totalTasks += value.intValue();
            }
        }

        // Set tick unit
        if (totalTasks <= 10) {
            rangeAxis.setTickUnit(new NumberTickUnit(1));
        } else {
            rangeAxis.setTickUnit(new NumberTickUnit(10));
        }

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        chartPanel.setMouseWheelEnabled(false);// Disable zooming with mouse wheel
        chartPanel.setDomainZoomable(false);// Disable domain zooming (x-axis)
        chartPanel.setRangeZoomable(false);// Disable range zooming (y-axis)
        chartPanel.setPopupMenu(null);// Remove the right-click menu

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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        backJB = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        taskHistoryJT = new javax.swing.JTable();
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
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        jYearChooser2 = new com.toedter.calendar.JYearChooser();
        YgenerateJB = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        barChartJL = new javax.swing.JLabel();
        allJB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Task History");

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
                .addGap(302, 302, 302)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(backJB)
                .addGap(17, 17, 17))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(backJB)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        taskHistoryJT.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        taskHistoryJT.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        taskHistoryJT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Oder ID.", "Customer Name", "Vendor", "Date", "Income(RM)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        taskHistoryJT.setRowHeight(25);
        taskHistoryJT.setShowGrid(false);
        jScrollPane1.setViewportView(taskHistoryJT);
        if (taskHistoryJT.getColumnModel().getColumnCount() > 0) {
            taskHistoryJT.getColumnModel().getColumn(0).setPreferredWidth(10);
            taskHistoryJT.getColumnModel().getColumn(1).setPreferredWidth(200);
            taskHistoryJT.getColumnModel().getColumn(2).setPreferredWidth(200);
            taskHistoryJT.getColumnModel().getColumn(3).setPreferredWidth(100);
            taskHistoryJT.getColumnModel().getColumn(4).setPreferredWidth(30);
        }

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
                .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jYearChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(jYearChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(yearJPLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(YgenerateJB)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Year", yearJP);

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
                .addContainerGap(9, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(allJB, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(allJB, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

        fetchFilteredData(startLocalDate, endLocalDate);
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

        // Adjust the start and end date
        startLocalDate = startLocalDate.withDayOfMonth(1);
        endLocalDate = endLocalDate.withDayOfMonth(endLocalDate.lengthOfMonth());

        fetchFilteredData(startLocalDate, endLocalDate);
        generateMonthlyBarChart(startLocalDate, endLocalDate);
    }//GEN-LAST:event_MgenerateJBActionPerformed

    private void YgenerateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YgenerateJBActionPerformed
        int startYear = jYearChooser1.getYear();
        int endYear = jYearChooser2.getYear();

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

        fetchFilteredData(startLocalDate, endLocalDate);
        generateYearlyBarChart(startYear, endYear);
    }//GEN-LAST:event_YgenerateJBActionPerformed

    private void allJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allJBActionPerformed
        fetchFilteredData(null, null);
        chartAllData();
    }//GEN-LAST:event_allJBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DgenerateJB;
    private javax.swing.JButton MgenerateJB;
    private javax.swing.JButton YgenerateJB;
    private javax.swing.JButton allJB;
    private javax.swing.JButton backJB;
    private javax.swing.JLabel barChartJL;
    private javax.swing.JPanel dayJP;
    private com.toedter.calendar.JDateChooser endDateChooser;
    private com.toedter.calendar.JDateChooser endMonthChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private com.toedter.calendar.JYearChooser jYearChooser2;
    private javax.swing.JPanel monthJP;
    private com.toedter.calendar.JDateChooser startDateChooser;
    private com.toedter.calendar.JDateChooser startMonthChooser;
    private javax.swing.JTable taskHistoryJT;
    private javax.swing.JPanel yearJP;
    // End of variables declaration//GEN-END:variables
}
