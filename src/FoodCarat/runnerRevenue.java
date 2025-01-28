/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
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

/**
 *
 * @author Yuna
 */
public class runnerRevenue extends javax.swing.JFrame {
    private String runnerEmail = User.getSessionEmail();

    /**
     * Creates new form runnerRevenue
     */
    public runnerRevenue() {
        initComponents();
        setLocationRelativeTo(null);
        
        fetchFilteredData(null,null);
        chartAllData();
        updateTotalIncomeCurrentMonth();
    }
    
    // Method to calculate and display total income for the current month
    private void updateTotalIncomeCurrentMonth() {
        DefaultTableModel model = (DefaultTableModel) revenueJT.getModel();
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate nextMonth = currentMonth.plusMonths(1);

        double totalIncome = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 1);
            String incomeString = (String) model.getValueAt(i, 2);

            LocalDate taskDate = LocalDate.parse(dateString);
            double income = Double.parseDouble(incomeString);

            if (!taskDate.isBefore(currentMonth) && taskDate.isBefore(nextMonth)) {
                totalIncome += income;
            }
        }

        incomeThisMJL.setText(String.format("RM %.2f", totalIncome));
    }

    // Method to calculate and display total income for selected range
    private void updateTotalIncomeSelected(LocalDate startDate, LocalDate endDate) {
        DefaultTableModel model = (DefaultTableModel) revenueJT.getModel();
        double totalIncome = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 1);
            String incomeString = (String) model.getValueAt(i, 2);

            LocalDate taskDate = LocalDate.parse(dateString);
            double income = Double.parseDouble(incomeString);

            if ((startDate == null || !taskDate.isBefore(startDate)) &&
                (endDate == null || !taskDate.isAfter(endDate))) {
                totalIncome += income;
            }
        }

        incomeSelectJL.setText(String.format("RM %.2f", totalIncome));
    }
    
    // Update the table
    private void fetchFilteredData(LocalDate startDate, LocalDate endDate) {
        List<String[]> completedTasks = new Order().getCompletedTask(runnerEmail);
        completedTasks.sort((task1, task2) -> task2[9].compareTo(task1[9]));

        DefaultTableModel model = (DefaultTableModel) revenueJT.getModel();
        model.setRowCount(0);

        for (String[] task : completedTasks) {
            String orderDate = task[9];
            LocalDate taskDate = LocalDate.parse(orderDate);

            if ((startDate == null || !taskDate.isBefore(startDate)) &&
                (endDate == null || !taskDate.isAfter(endDate))) {
                String orderId = task[0];
                String deliveryFee = task[7];

                model.addRow(new Object[]{
                    orderId, orderDate, deliveryFee
                });
            }
        }
    }

    // Chart for all income
    private void chartAllData() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) revenueJT.getModel();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusMonths(4);

        Map<Month, Double> incomeSums = new LinkedHashMap<>();

        for (int i = 0; i < 5; i++) {
            Month month = startDate.plusMonths(i).getMonth();
            incomeSums.put(month, 0.00);
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 1);
            String incomeString = (String) model.getValueAt(i, 2);

            LocalDate taskDate = LocalDate.parse(dateString);
            Month taskMonth = taskDate.getMonth();
            double income = Double.parseDouble(incomeString);

            if (incomeSums.containsKey(taskMonth)) {
                incomeSums.put(taskMonth, incomeSums.get(taskMonth) + income);
            }
        }
        
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Month, Double> entry : incomeSums.entrySet()) {
            String monthLabel = entry.getKey().name();
            String formattedIncome = df.format(entry.getValue());
            dataset.addValue(Double.parseDouble(formattedIncome), "Income", monthLabel);
        }

        displayLineChart(dataset, "Income Over Last 5 Months", "Months", "Total Income");
    }

    // Generate Daily Chart
    private void generateDailyChart(LocalDate startDate, LocalDate endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) revenueJT.getModel();

        Map<LocalDate, Double> incomeSums = new TreeMap<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 1);
            String incomeString = (String) model.getValueAt(i, 2);

            LocalDate taskDate = LocalDate.parse(dateString);
            double income = Double.parseDouble(incomeString);

            if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
                incomeSums.put(taskDate, incomeSums.getOrDefault(taskDate, 0.0) + income);
            }
        }
        
        DecimalFormat df = new DecimalFormat("0.00");
         for (Map.Entry<LocalDate, Double> entry : incomeSums.entrySet()) {
            String day = entry.getKey().toString();
            dataset.addValue(Double.parseDouble(df.format(entry.getValue())), "Income", day);
        }

        displayLineChart(dataset, "Income of Tasks from " + startDate + " to " + endDate, "Date", "Total Income");
        updateTotalIncomeSelected(startDate, endDate);
    }

    // Generate monthly chart
    private void generateMonthlyChart(LocalDate startDate, LocalDate endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) revenueJT.getModel();

        Map<YearMonth, Double> incomeSums = new TreeMap<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 1);
            String incomeString = (String) model.getValueAt(i, 2);

            LocalDate taskDate = LocalDate.parse(dateString);
            double income = Double.parseDouble(incomeString);

            if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
                YearMonth taskYearMonth = YearMonth.from(taskDate);

                incomeSums.put(taskYearMonth, incomeSums.getOrDefault(taskYearMonth, 0.0) + income);
            }
        }
        
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<YearMonth, Double> entry : incomeSums.entrySet()) {
            String monthLabel = entry.getKey().getMonth() + " " + entry.getKey().getYear();
            dataset.addValue(Double.parseDouble(df.format(entry.getValue())), "Income", monthLabel);
        }

        displayLineChart(dataset, "Income of Tasks from " + startDate + " to " + endDate, "Months", "Total Income");
        updateTotalIncomeSelected(startDate, endDate);

    }

    // Generate yearly chart
    private void generateYearlyChart(int startYear, int endYear) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DefaultTableModel model = (DefaultTableModel) revenueJT.getModel();
        Map<Integer, Double> incomeSums = new TreeMap<>();
        double totalIncome = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String dateString = (String) model.getValueAt(i, 1);
            String incomeString = (String) model.getValueAt(i, 2);

            LocalDate taskDate = LocalDate.parse(dateString);
            int taskYear = taskDate.getYear();
            double income = Double.parseDouble(incomeString);

            if (taskYear >= startYear && taskYear <= endYear) {
                incomeSums.put(taskYear, incomeSums.getOrDefault(taskYear, 0.0) + income);
                totalIncome += income; // Add to the total income
            }

            if (taskYear >= startYear && taskYear <= endYear) {
                incomeSums.put(taskYear, incomeSums.getOrDefault(taskYear, 0.0) + income);
            }
        }
    
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> entry : incomeSums.entrySet()) {
            String yearLabel = String.valueOf(entry.getKey());
            dataset.addValue(Double.parseDouble(df.format(entry.getValue())), "Income", yearLabel);
        }

        displayLineChart(dataset, "Income of Tasks from " + startYear + " to " + endYear, "Years", "Total Income");
        incomeSelectJL.setText(String.format("RM %.2f", totalIncome));
    }

    // Display chart
    private void displayLineChart(DefaultCategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
        JFreeChart barChart = ChartFactory.createLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        
        int totalIncome = 0;
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Number value = dataset.getValue(0, i);
            if (value != null) {
                totalIncome += value.intValue();
            }
        }

        if (totalIncome <= 10) {
            rangeAxis.setTickUnit(new NumberTickUnit(1));
        } else {
            rangeAxis.setTickUnit(new NumberTickUnit(20));
        }

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);

        lineChartJL.removeAll();
        lineChartJL.setLayout(new BorderLayout());
        lineChartJL.add(chartPanel, BorderLayout.CENTER);
        lineChartJL.validate();
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
        backJB = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        revenueJT = new javax.swing.JTable();
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
        allJB = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lineChartJL = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        incomeSelectJL = new javax.swing.JLabel();
        incomeThisMJL = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));

        backJB.setText("<  Main Menu");
        backJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backJBActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Revenue");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(backJB)
                .addGap(239, 239, 239)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(backJB))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1)))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        revenueJT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Date", "Income (RM)"
            }
        ));
        jScrollPane2.setViewportView(revenueJT);
        if (revenueJT.getColumnModel().getColumnCount() > 0) {
            revenueJT.getColumnModel().getColumn(0).setPreferredWidth(10);
            revenueJT.getColumnModel().getColumn(1).setPreferredWidth(200);
            revenueJT.getColumnModel().getColumn(2).setPreferredWidth(200);
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

        jTabbedPane1.addTab("Daily", dayJP);

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

        jTabbedPane1.addTab("Monthly", monthJP);

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

        jTabbedPane1.addTab("Yearly", yearJP);

        allJB.setText("Show All");
        allJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allJBActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lineChartJL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lineChartJL.setText("Line Chart");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChartJL, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChartJL, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel2.setText("Total Income (This Month):");

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel6.setText("Total Income (Selected Range):");

        incomeSelectJL.setFont(new java.awt.Font("Cooper Black", 0, 30)); // NOI18N
        incomeSelectJL.setText("RM0");

        incomeThisMJL.setFont(new java.awt.Font("Cooper Black", 0, 30)); // NOI18N
        incomeThisMJL.setText("RM0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(incomeThisMJL, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addComponent(incomeSelectJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(incomeThisMJL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(incomeSelectJL))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(allJB, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(allJB))
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        fetchFilteredData(startLocalDate, endLocalDate);
        generateDailyChart(startLocalDate, endLocalDate);
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

        startLocalDate = startLocalDate.withDayOfMonth(1);
        endLocalDate = endLocalDate.withDayOfMonth(endLocalDate.lengthOfMonth());

        fetchFilteredData(startLocalDate, endLocalDate);
        generateMonthlyChart(startLocalDate, endLocalDate);
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

        LocalDate startLocalDate = LocalDate.of(startYear, 1, 1);
        LocalDate endLocalDate = LocalDate.of(endYear, 12, 31);

        fetchFilteredData(startLocalDate, endLocalDate);
        generateYearlyChart(startYear, endYear);
    }//GEN-LAST:event_YgenerateJBActionPerformed

    private void allJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allJBActionPerformed
        fetchFilteredData(null, null);
        chartAllData();
        updateTotalIncomeSelected(null, null);
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
            java.util.logging.Logger.getLogger(runnerRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(runnerRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(runnerRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(runnerRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new runnerRevenue().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DgenerateJB;
    private javax.swing.JButton MgenerateJB;
    private javax.swing.JButton YgenerateJB;
    private javax.swing.JButton allJB;
    private javax.swing.JButton backJB;
    private javax.swing.JPanel dayJP;
    private com.toedter.calendar.JDateChooser endDateChooser;
    private com.toedter.calendar.JDateChooser endMonthChooser;
    private javax.swing.JLabel incomeSelectJL;
    private javax.swing.JLabel incomeThisMJL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private com.toedter.calendar.JYearChooser jYearChooser2;
    private javax.swing.JLabel lineChartJL;
    private javax.swing.JPanel monthJP;
    private javax.swing.JTable revenueJT;
    private com.toedter.calendar.JDateChooser startDateChooser;
    private com.toedter.calendar.JDateChooser startMonthChooser;
    private javax.swing.JPanel yearJP;
    // End of variables declaration//GEN-END:variables
}