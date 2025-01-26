/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
public class managerMonVenPer extends javax.swing.JFrame {

    /**
     * Creates new form managerMonVenPer
     */
    public managerMonVenPer() {
        initComponents();
        setLocationRelativeTo(null);
        totalrevenuechart.setBackground(new Color(211, 211, 211));// Light gray
        totalorderchart.setBackground(new Color(211, 211, 211));
        averagechart.setBackground(new Color(211, 211, 211)); 
    }
    
    private void displayVendorPerformance(int selectedMonth) throws IOException {
        Manager manager = new Manager();
        Map<String, String> performanceDataMap = manager.getVendorPerformanceByMonth(selectedMonth);

        // Assuming you have a JTable set up with columns "Num", "Vendor Name", "Total Revenue", "Total Orders", and "Average Value Per Order"
        DefaultTableModel model = (DefaultTableModel) VenPertable.getModel();
        model.setRowCount(0); // Clear previous rows

        int rowNumber = 1;
        for (Map.Entry<String, String> entry : performanceDataMap.entrySet()) {
            String vendorEmail = entry.getKey();
            String performanceData = entry.getValue(); // performanceData is in the format "totalOrders,totalRevenue,avgOrderValue"
            String[] performanceValues = performanceData.split(",");

            int totalOrders = Integer.parseInt(performanceValues[0]);
            double totalRevenue = Double.parseDouble(performanceValues[1]);
            double avgOrderValue = Double.parseDouble(performanceValues[2]);

            User user = new User();
            String[] venName = user.performSearch(vendorEmail, "user.txt");
            String vendorName = venName[1];

            // Add row data to the table
            model.addRow(new Object[] {
                rowNumber,
                vendorName,
                String.format("%.2f", totalRevenue),
                totalOrders,
                String.format("%.2f", avgOrderValue)
            });

            rowNumber++;
        }
    }
    
    public void createVendorPerformancePieChart(Map<String, String> performanceDataMap) {
        double totalRevenue = 0;
        int totalOrders = 0;
        double totalAverageValue = 0;

        // Create maps to store individual vendor data
        Map<String, Double> revenueMap = new HashMap<>();
        Map<String, Integer> ordersMap = new HashMap<>();
        Map<String, Double> avgValueMap = new HashMap<>();

        // Parse the performance data map and calculate totals
        for (Map.Entry<String, String> entry : performanceDataMap.entrySet()) {
            String vendorID = entry.getKey();
            String[] performanceData = entry.getValue().split(",");

            double vendorRevenue = Double.parseDouble(performanceData[1]);
            int vendorOrders = Integer.parseInt(performanceData[0]);
            double vendorAvgValue = Double.parseDouble(performanceData[2]);

            revenueMap.put(vendorID, vendorRevenue);
            ordersMap.put(vendorID, vendorOrders);
            avgValueMap.put(vendorID, vendorAvgValue);

            totalRevenue += vendorRevenue;
            totalOrders += vendorOrders;
            totalAverageValue += vendorAvgValue;
        }

        // Create datasets for pie charts
        DefaultPieDataset revenueDataset = new DefaultPieDataset();
        DefaultPieDataset ordersDataset = new DefaultPieDataset();
        DefaultPieDataset avgValueDataset = new DefaultPieDataset();

        // Add data to the datasets
        for (String vendorID : performanceDataMap.keySet()) {
            double vendorRevenue = revenueMap.get(vendorID);
            int vendorOrders = ordersMap.get(vendorID);
            double vendorAvgValue = avgValueMap.get(vendorID);

            // Fetch runner name using the user utility
            User user = new User();
            String[] vendorDetails = user.performSearch(vendorID, "user.txt");
            String vendorName = vendorDetails.length > 1 ? vendorDetails[1] : vendorID;

            // Calculate and add percentages
            if (totalRevenue > 0) {
                revenueDataset.setValue(vendorName, vendorRevenue / totalRevenue * 100);
            }

            if (totalOrders > 0) {
                ordersDataset.setValue(vendorName, vendorOrders / (double) totalOrders * 100);
            }

            if (totalAverageValue > 0) {
                avgValueDataset.setValue(vendorName, vendorAvgValue / totalAverageValue * 100);
            }
        }

        // Create pie charts
        ChartPanel revenueChartPanel = ChartUtility.createPieChart(revenueDataset, "Vendor Performance - Total Revenue");
        ChartPanel ordersChartPanel = ChartUtility.createPieChart(ordersDataset, "Vendor Performance - Total Orders");
        ChartPanel avgValueChartPanel = ChartUtility.createPieChart(avgValueDataset, "Vendor Performance - Average Order Value");

        // Clear and update chart panels
        totalrevenuechart.removeAll();
        totalorderchart.removeAll();
        averagechart.removeAll();

        totalrevenuechart.setLayout(new java.awt.BorderLayout());
        totalrevenuechart.add(revenueChartPanel, java.awt.BorderLayout.CENTER);

        totalorderchart.setLayout(new java.awt.BorderLayout());
        totalorderchart.add(ordersChartPanel, java.awt.BorderLayout.CENTER);

        averagechart.setLayout(new java.awt.BorderLayout());
        averagechart.add(avgValueChartPanel, java.awt.BorderLayout.CENTER);

        totalrevenuechart.revalidate();
        totalrevenuechart.repaint();

        totalorderchart.revalidate();
        totalorderchart.repaint();

        averagechart.revalidate();
        averagechart.repaint();
    }
    
    private void addTotalRow(List<String[]> vendorData) {
        double totalRevenue = 0;
        int totalOrders = 0;
        double totalAvgOrderValue = 0;

        // Loop through the data and sum up the values
        for (String[] data : vendorData) {
            totalRevenue += Double.parseDouble(data[2]); // Total Revenue
            totalOrders += Integer.parseInt(data[3]);    // Total Orders
            totalAvgOrderValue += Double.parseDouble(data[4]); // Average Value Per Order
        }

        // Calculate the average order value if there are multiple vendors
        double avgOrderValue = (vendorData.size() > 0) ? totalAvgOrderValue / vendorData.size() : 0;
        avgOrderValue = Double.parseDouble(String.format("%.2f", avgOrderValue));
        // Add the total row to the table
        DefaultTableModel model = (DefaultTableModel) VenPertable.getModel();
        model.addRow(new Object[]{"Total:", "", totalRevenue, totalOrders, avgOrderValue});
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
        searchbtn = new javax.swing.JButton();
        monthlabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        VenPertable = new javax.swing.JTable();
        monthcbx = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        totalrevenuechart = new javax.swing.JPanel();
        totalorderchart = new javax.swing.JPanel();
        averagechart = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Monitor Vendor Performance");

        searchbtn.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        searchbtn.setText("Search");
        searchbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchbtnActionPerformed(evt);
            }
        });

        monthlabel.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        monthlabel.setText("Month:");

        VenPertable.setFont(new java.awt.Font("Constantia", 0, 14)); // NOI18N
        VenPertable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "No", "Vendor", "Total Revenue", "Total order", "Average value per order", "Average rating"
            }
        ));
        jScrollPane1.setViewportView(VenPertable);

        monthcbx.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        monthcbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please select", "January", "February", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jLabel2.setText("Total Revenue Chart");

        jLabel3.setText("Total Order Chart");

        jLabel4.setText("Average Value Per Order Chart");

        javax.swing.GroupLayout totalrevenuechartLayout = new javax.swing.GroupLayout(totalrevenuechart);
        totalrevenuechart.setLayout(totalrevenuechartLayout);
        totalrevenuechartLayout.setHorizontalGroup(
            totalrevenuechartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );
        totalrevenuechartLayout.setVerticalGroup(
            totalrevenuechartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout totalorderchartLayout = new javax.swing.GroupLayout(totalorderchart);
        totalorderchart.setLayout(totalorderchartLayout);
        totalorderchartLayout.setHorizontalGroup(
            totalorderchartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 384, Short.MAX_VALUE)
        );
        totalorderchartLayout.setVerticalGroup(
            totalorderchartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout averagechartLayout = new javax.swing.GroupLayout(averagechart);
        averagechart.setLayout(averagechartLayout);
        averagechartLayout.setHorizontalGroup(
            averagechartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 381, Short.MAX_VALUE)
        );
        averagechartLayout.setVerticalGroup(
            averagechartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(monthlabel)
                        .addGap(40, 40, 40)
                        .addComponent(monthcbx, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(searchbtn)))
                .addGap(54, 54, 54)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(totalrevenuechart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(totalorderchart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(33, 33, 33))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(averagechart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(481, 481, 481))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(monthlabel)
                        .addComponent(monthcbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchbtn)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(totalorderchart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(totalrevenuechart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(averagechart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchbtnActionPerformed
        String selectedMonth = (String) monthcbx.getSelectedItem();
         // Array of month names to convert to month number (January = 1, February = 2, etc.)
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        // Convert selectedMonth to month number
        int monthNumber = Arrays.asList(monthNames).indexOf(selectedMonth) + 1;
        
        // Validate if the user has selected a valid month
        if (selectedMonth == null || "Please select".equals(selectedMonth)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please select a valid month!");
            return;
        }
        
        try {
        // Call the Manager method to get vendor performance data
        Manager manager = new Manager();
        Map<String, String> performanceDataMap = manager.getVendorPerformanceByMonth(monthNumber);

        if (performanceDataMap == null || performanceDataMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data available for the selected month.");
            return;
        }
        // Create and display the pie chart with the data
        createVendorPerformancePieChart(performanceDataMap);
        displayVendorPerformance(monthNumber);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_searchbtnActionPerformed

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
            java.util.logging.Logger.getLogger(managerMonVenPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(managerMonVenPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(managerMonVenPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(managerMonVenPer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new managerMonVenPer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable VenPertable;
    private javax.swing.JPanel averagechart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> monthcbx;
    private javax.swing.JLabel monthlabel;
    private javax.swing.JButton searchbtn;
    private javax.swing.JPanel totalorderchart;
    private javax.swing.JPanel totalrevenuechart;
    // End of variables declaration//GEN-END:variables
}
