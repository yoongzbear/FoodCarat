/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author User
 */
public class managerMonRunPer extends javax.swing.JFrame {

    /**
     * Creates new form managerMonRunPer
     */
    public managerMonRunPer() {
        initComponents();
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(252, 204, 196));
        
        //Blank chart
        DefaultPieDataset blankDataset = new DefaultPieDataset();

        totalorderchart.setLayout(new java.awt.BorderLayout());
        totalorderchart.add(ChartUtility.createPieChart(blankDataset, "No Data Available"), java.awt.BorderLayout.CENTER);

        averagechart.setLayout(new java.awt.BorderLayout());
        averagechart.add(ChartUtility.createPieChart(blankDataset, "No Data Available"), java.awt.BorderLayout.CENTER);
    }

    private void displayRunnerPerformance(int selectedMonth) throws IOException {
        Manager manager = new Manager();
        Map<String, String> performanceDataMap = manager.getRunnerPerformance(selectedMonth);

        // Assuming you have a JTable set up with columns "RunnerID", "Total Orders", and "Average Rating"
        DefaultTableModel model = (DefaultTableModel) runpertable.getModel();
        model.setRowCount(0);  // Clear previous rows

        // Create a map to store total orders for each runner
        int rowNumber = 1;
        for (Map.Entry<String, String> entry : performanceDataMap.entrySet()) {
            String runnerID = entry.getKey();
            String performanceData = entry.getValue(); // performanceData is in the format "totalOrders,averageRating"
            String[] performanceValues = performanceData.split(",");

            int totalOrders = Integer.parseInt(performanceValues[0]);
            Review review = new Review();
            double averageRating = review.getAverageRating(runnerID, "runner");

            User user = new User();
            String[] runnerName = user.performSearch(runnerID, "user.txt");
            String name = runnerName[1];

            // Add row data to the table
            model.addRow(new Object[] {
                rowNumber,
                name,
                totalOrders,
                String.format("%.2f", averageRating)
            });

            rowNumber++;
        }

        // Calculate summary total orders and average rating
        int totalOrders = 0;
        double totalAverageRating = 0.0;
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            totalOrders += Integer.parseInt(model.getValueAt(i, 2).toString());
            totalAverageRating += Double.parseDouble(model.getValueAt(i, 3).toString());
        }

        double averageRatingSummary = rowCount > 0 ? totalAverageRating / rowCount : 0.0;

        // Add the summary row
        model.addRow(new Object[]{
            "Total:",
            "",
            totalOrders,
            String.format("%.2f", averageRatingSummary)
        });
    }
    
    public void createRunnerPerformancePieChart(Map<String, String> performanceDataMap) {
        int totalOrders = 0;
        double totalAverageRating = 0;

        // Calculate total orders and total average rating
        Map<String, Integer> totalOrdersMap = new HashMap<>();

        for (Map.Entry<String, String> entry : performanceDataMap.entrySet()) {
            String runnerID = entry.getKey();
            String[] performanceData = entry.getValue().split(",");

            int runnerTotalOrders = Integer.parseInt(performanceData[0]);
            double avgRating = Double.parseDouble(performanceData[1]);

            totalOrdersMap.put(runnerID, runnerTotalOrders);
            Review review = new Review();
            double averageRating = review.getAverageRating(runnerID, "runner");

            totalOrders += runnerTotalOrders; // Total orders
            totalAverageRating += averageRating; // Total average rating
        }

        // Create datasets for pie charts
        DefaultPieDataset ordersDataset = new DefaultPieDataset();
        DefaultPieDataset avgRatingDataset = new DefaultPieDataset();

        // Add data to the datasets
        for (String runnerID : performanceDataMap.keySet()) {
            int runnerTotalOrders = totalOrdersMap.get(runnerID);
            Review review = new Review();
            double averageRating = review.getAverageRating(runnerID, "runner");

            // Fetch runner name using the user utility
            User user = new User();
            String[] runnerDetails = user.performSearch(runnerID, "user.txt");
            String runnerName = runnerDetails.length > 1 ? runnerDetails[1] : runnerID;

            // Calculate and add percentages
            if (totalOrders > 0) {
                ordersDataset.setValue(runnerName, runnerTotalOrders / (double) totalOrders * 100);
            }

            if (totalAverageRating > 0) {
                avgRatingDataset.setValue(runnerName, averageRating / totalAverageRating * 100);
            }
        }

        // Create pie charts
        ChartPanel ordersChartPanel = ChartUtility.createPieChart(ordersDataset, "Runner Performance - Total Orders");
        ChartPanel avgRatingChartPanel = ChartUtility.createPieChart(avgRatingDataset, "Runner Performance - Average Rating");

        // Clear and update chart panels
        totalorderchart.removeAll();
        averagechart.removeAll();

        totalorderchart.setLayout(new java.awt.BorderLayout());
        totalorderchart.add(ordersChartPanel, java.awt.BorderLayout.CENTER);

        averagechart.setLayout(new java.awt.BorderLayout());
        averagechart.add(avgRatingChartPanel, java.awt.BorderLayout.CENTER);

        totalorderchart.revalidate();
        totalorderchart.repaint();

        averagechart.revalidate();
        averagechart.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Lmonitorrun = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        bsearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        runpertable = new javax.swing.JTable();
        monthcbx = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        totalorderchart = new javax.swing.JPanel();
        averagechart = new javax.swing.JPanel();
        backbtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Lmonitorrun.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        Lmonitorrun.setText("Monitor Runner Performance");

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel1.setText("Month : ");

        bsearch.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        bsearch.setText("Search");
        bsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bsearchActionPerformed(evt);
            }
        });

        runpertable.setFont(new java.awt.Font("Constantia", 0, 14)); // NOI18N
        runpertable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Runner Name", "Total Order", "Average Rating"
            }
        ));
        jScrollPane1.setViewportView(runpertable);

        monthcbx.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        monthcbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please select", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jLabel2.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        jLabel2.setText("Total Order Chart");

        jLabel3.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        jLabel3.setText("Average Rating Chart");

        javax.swing.GroupLayout totalorderchartLayout = new javax.swing.GroupLayout(totalorderchart);
        totalorderchart.setLayout(totalorderchartLayout);
        totalorderchartLayout.setHorizontalGroup(
            totalorderchartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 321, Short.MAX_VALUE)
        );
        totalorderchartLayout.setVerticalGroup(
            totalorderchartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 185, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout averagechartLayout = new javax.swing.GroupLayout(averagechart);
        averagechart.setLayout(averagechartLayout);
        averagechartLayout.setHorizontalGroup(
            averagechartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 332, Short.MAX_VALUE)
        );
        averagechartLayout.setVerticalGroup(
            averagechartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
        );

        backbtn.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        backbtn.setText("Main Menu");
        backbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(29, 29, 29)
                                .addComponent(monthcbx, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(bsearch)))
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(totalorderchart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 30, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(averagechart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Lmonitorrun)
                        .addGap(245, 245, 245)
                        .addComponent(backbtn)))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(Lmonitorrun))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(backbtn)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(bsearch)
                            .addComponent(monthcbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(10, 10, 10)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalorderchart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(averagechart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bsearchActionPerformed

        String selectedMonth = monthcbx.getSelectedItem().toString();
        // Array of month names
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        // Convert the month name to the month number (1 for January, 2 for February, etc.)
        int monthNumber = Arrays.asList(monthNames).indexOf(selectedMonth) + 1;
        
        if (selectedMonth == null || "Please select".equals(selectedMonth)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please select a valid month!");
            return;
        }
        
        try {
        // Fetch the performance data
        Manager manager = new Manager();
        Map<String, String> performanceDataMap = manager.getRunnerPerformance(monthNumber);

        
        if (performanceDataMap == null || performanceDataMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available for the selected month.");
            return;
        }
        // Call the method to create and display the pie charts
        createRunnerPerformancePieChart(performanceDataMap);
        // show the data in table
        displayRunnerPerformance(monthNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_bsearchActionPerformed

    private void backbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backbtnActionPerformed
        this.dispose();
        new managerMain().setVisible(true);
    }//GEN-LAST:event_backbtnActionPerformed

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
            java.util.logging.Logger.getLogger(managerMonRunPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(managerMonRunPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(managerMonRunPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(managerMonRunPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new managerMonRunPer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Lmonitorrun;
    private javax.swing.JPanel averagechart;
    private javax.swing.JButton backbtn;
    private javax.swing.JButton bsearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> monthcbx;
    private javax.swing.JTable runpertable;
    private javax.swing.JPanel totalorderchart;
    // End of variables declaration//GEN-END:variables
}
