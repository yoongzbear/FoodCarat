/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class vendorRevenue extends javax.swing.JFrame {

    private String vendorEmail = User.getSessionEmail();

    /**
     * Creates new form vendorRevenue
     */
    public vendorRevenue() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186, 85, 211)); //setting background color of frame
        setLocationRelativeTo(null);

        updateAnnualRevenue();
        updateCompletedOrders();
        updateMonthIncome();
        generatePieChart();
        generateMonthIncome();
        generateYearIncome();
    }

    private void updateAnnualRevenue() {
        double annualRevenue = fetchAnnualRevenue();
        annualRevenueJL.setText("RM" + String.format("%.2f", annualRevenue));
    }

    private void updateCompletedOrders() {
        int totalOrders = fetchCompletedOrders();
        completedOrderedJL.setText(String.valueOf(totalOrders));
    }

    private void updateMonthIncome() {
        int month = incomeJMC.getMonth() + 1;
        int year = incomeJYC.getYear();
        double income = fetchMonthIncome(month, year);
        totalIncomeMJL.setText("RM" + String.format("%.2f", income));
    }

    // Calculate Annual revenue
    private double fetchAnnualRevenue() {
        double annualRevenue = 0.0;
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        List<String[]> completedTasks = new Order().getOrderByStatus(vendorEmail, "completed");

        for (String[] task : completedTasks) {
            String incomeStr = task[8];
            String orderDateStr = task[9];

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date orderDate = sdf.parse(orderDateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(orderDate);
                int orderYear = cal.get(Calendar.YEAR);

                if (orderYear == currentYear) {
                    annualRevenue += Double.parseDouble(incomeStr);
                }
            } catch (ParseException | NumberFormatException e) {
            }
        }

        return annualRevenue;
    }

    // Total Completed orders
    private int fetchCompletedOrders() {
        List<String[]> completedOrders = new Order().getOrderByStatus(vendorEmail, "completed");

        return completedOrders.size();
    }

    // Income of the Month
    private double fetchMonthIncome(int month, int year) {
        List<String[]> completedTasks = new Order().getOrderByStatus(vendorEmail, "completed");
        double totalIncome = 0;

        for (String[] task : completedTasks) {
            String incomeStr = task[8];
            String orderDate = task[9];

            if (orderDate.startsWith(year + "-" + String.format("%02d", month))) {
                totalIncome += Double.parseDouble(incomeStr);
            }
        }

        return totalIncome;
    }

    private double fetchYearIncome(int year) {
        List<String[]> completedTasks = new Order().getOrderByStatus(vendorEmail, "completed");
        double totalIncome = 0;

        for (String[] task : completedTasks) {
            String incomeStr = task[8];
            String orderDate = task[9];

            if (orderDate.startsWith(String.valueOf(year))) {
                totalIncome += Double.parseDouble(incomeStr);
            }
        }

        return totalIncome;
    }

    private void generatePieChart() {
        int month = pieMonthJMC.getMonth() + 1;
        int year = pieYearJYC.getYear();

        List<String[]> completedTasks = new Order().getOrderByStatus(vendorEmail, "completed");

        int deliveryCount = 0;
        int takeAwayCount = 0;
        int dineInCount = 0;

        for (String[] task : completedTasks) {
            String orderDate = task[9];
            String orderType = task[1];

            if (orderDate.startsWith(year + "-" + String.format("%02d", month))) {
                switch (orderType.toLowerCase()) {
                    case "delivery":
                        deliveryCount++;
                        break;
                    case "take away":
                        takeAwayCount++;
                        break;
                    case "dine in":
                        dineInCount++;
                        break;
                }
            }
        }

        if (deliveryCount == 0 && takeAwayCount == 0 && dineInCount == 0) {
            JOptionPane.showMessageDialog(null, "No data available for the selected month of the year.", "No Data", JOptionPane.INFORMATION_MESSAGE);

            DefaultPieDataset emptyDataset = new DefaultPieDataset();
            emptyDataset.setValue("No Data", 1);

            displayPieChart(emptyDataset, "No Data Available For the Selected Month of the Year.");
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Delivery", deliveryCount);
        dataset.setValue("Take Away", takeAwayCount);
        dataset.setValue("Dine In", dineInCount);

        displayPieChart(dataset, "Order Type Distribution for " + month + "-" + year);
    }

    // Generate Income by month
    private void generateMonthIncome() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        // Get income data
        for (int i = 5; i >= 0; i--) {
            int month = currentMonth - i;
            int year = currentYear;

            if (month <= 0) {
                month = 12 + month;
                year--;
            }

            double income = fetchMonthIncome(month, year);

            dataset.addValue(income, "Income", month + "-" + year);
        }

        displayLineChart(dataset, "Income for Last 6 Months", "Month", "Income", true);
    }

    // Generate Income by year
    private void generateYearIncome() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        for (int i = 5; i >= 0; i--) {
            int year = currentYear - i;

            double income = fetchYearIncome(year);

            dataset.addValue(income, "Income", String.valueOf(year));
        }

        displayLineChart(dataset, "Income for Last 6 Years", "Year", "Income", false);
    }

    // Display Line chart
    private void displayLineChart(DefaultCategoryDataset dataset, String title, String xAxisLabel, String yAxisLabel, boolean isMonthChart) {
        JFreeChart lineChart = ChartFactory.createLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot plot = lineChart.getCategoryPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        int totalIncome = 0;
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Number value = dataset.getValue(0, i);
            if (value != null) {
                totalIncome += value.intValue();
            }
        }

        if (totalIncome > 5000) {
            rangeAxis.setTickUnit(new NumberTickUnit(2000));
        } else if (totalIncome > 3000) {
            rangeAxis.setTickUnit(new NumberTickUnit(500));
        } else if (totalIncome <= 100) {
            rangeAxis.setTickUnit(new NumberTickUnit(10));
        } else {
            rangeAxis.setTickUnit(new NumberTickUnit(200));
        }

        CategoryAxis categoryAxis = plot.getDomainAxis();
        categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);

        if (isMonthChart) {
            lineChartMonthJL.removeAll();
            lineChartMonthJL.setLayout(new BorderLayout());
            lineChartMonthJL.add(chartPanel, BorderLayout.CENTER);
            lineChartMonthJL.validate();
        } else {
            lineChartYearJL.removeAll();
            lineChartYearJL.setLayout(new BorderLayout());
            lineChartYearJL.add(chartPanel, BorderLayout.CENTER);
            lineChartYearJL.validate();
        }
    }

    // Display Pie chart
    private void displayPieChart(DefaultPieDataset dataset, String title) {
        JFreeChart pieChart = ChartFactory.createPieChart(title, dataset, true, true, false);

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Delivery", Color.BLUE);
        plot.setSectionPaint("Take Away", Color.GREEN);
        plot.setSectionPaint("Dine In", Color.RED);

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);

        pieChartJL.removeAll();
        pieChartJL.setLayout(new BorderLayout());
        pieChartJL.add(chartPanel, BorderLayout.CENTER);
        pieChartJL.validate();
        pieChartJL.repaint();
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
        pieChartJL = new javax.swing.JLabel();
        pieMonthJMC = new com.toedter.calendar.JMonthChooser();
        pieYearJYC = new com.toedter.calendar.JYearChooser();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        generateChartJB = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        lineChartMonthJL = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lineChartYearJL = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        annualRevenueJL = new javax.swing.JLabel();
        completedOrderedJL = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        incomeJMC = new com.toedter.calendar.JMonthChooser();
        incomeJYC = new com.toedter.calendar.JYearChooser();
        totalIncomeMJL = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Revenue");

        menuBtn.setText("Main Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        pieChartJL.setText("Pie Chart - show order, delivery,  take away(%) by month");

        jLabel7.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel7.setText("Month:");

        jLabel8.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel8.setText("Year:");

        generateChartJB.setText("Generate Chart");
        generateChartJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateChartJBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pieMonthJMC, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pieYearJYC, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addComponent(generateChartJB, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pieChartJL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pieMonthJMC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pieYearJYC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generateChartJB, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pieChartJL, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        lineChartMonthJL.setText("Line Chart - few month income - 6 month");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChartMonthJL, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChartMonthJL, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Month", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        lineChartYearJL.setText("Line Chart - few year income - register until now (max. 5 years)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChartYearJL, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lineChartYearJL, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Year", jPanel4);

        jLabel5.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel5.setText("Total completed orders");

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel4.setText("Current Annual Revenue");

        annualRevenueJL.setFont(new java.awt.Font("Cooper Black", 0, 20)); // NOI18N
        annualRevenueJL.setForeground(java.awt.Color.blue);
        annualRevenueJL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        annualRevenueJL.setText("RM100K");

        completedOrderedJL.setFont(new java.awt.Font("Cooper Black", 0, 20)); // NOI18N
        completedOrderedJL.setForeground(java.awt.Color.blue);
        completedOrderedJL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        completedOrderedJL.setText("200K");

        jLabel9.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel9.setText("Total Income of ");

        incomeJMC.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                incomeJMCPropertyChange(evt);
            }
        });

        incomeJYC.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                incomeJYCPropertyChange(evt);
            }
        });

        totalIncomeMJL.setFont(new java.awt.Font("Cooper Black", 0, 20)); // NOI18N
        totalIncomeMJL.setForeground(java.awt.Color.blue);
        totalIncomeMJL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalIncomeMJL.setText("200K");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(447, 447, 447)
                .addComponent(menuBtn)
                .addGap(14, 14, 14))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel4)
                .addGap(52, 52, 52)
                .addComponent(jLabel5)
                .addGap(79, 79, 79)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(incomeJMC, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(incomeJYC, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(annualRevenueJL, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(202, 202, 202)
                .addComponent(completedOrderedJL, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(totalIncomeMJL, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(198, 198, 198))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(menuBtn))
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel9))
                        .addComponent(incomeJMC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(incomeJYC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(completedOrderedJL)
                            .addComponent(annualRevenueJL))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(totalIncomeMJL))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void incomeJMCPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_incomeJMCPropertyChange
        updateMonthIncome();
    }//GEN-LAST:event_incomeJMCPropertyChange

    private void incomeJYCPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_incomeJYCPropertyChange
        updateMonthIncome();
    }//GEN-LAST:event_incomeJYCPropertyChange

    private void generateChartJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateChartJBActionPerformed
        generatePieChart();
    }//GEN-LAST:event_generateChartJBActionPerformed

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
            java.util.logging.Logger.getLogger(vendorRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(vendorRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(vendorRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(vendorRevenue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new vendorRevenue().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel annualRevenueJL;
    private javax.swing.JLabel completedOrderedJL;
    private javax.swing.JButton generateChartJB;
    private com.toedter.calendar.JMonthChooser incomeJMC;
    private com.toedter.calendar.JYearChooser incomeJYC;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lineChartMonthJL;
    private javax.swing.JLabel lineChartYearJL;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel pieChartJL;
    private com.toedter.calendar.JMonthChooser pieMonthJMC;
    private com.toedter.calendar.JYearChooser pieYearJYC;
    private javax.swing.JLabel totalIncomeMJL;
    // End of variables declaration//GEN-END:variables
}
