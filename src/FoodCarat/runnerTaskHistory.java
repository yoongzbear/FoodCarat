/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import com.sun.jdi.connect.spi.Connection;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Yuna
 */
public class runnerTaskHistory extends javax.swing.JFrame {
    //private String runnerEmail = User.getSessionEmail();
    private String runnerEmail = "runner3@mail.com";

    /**
     * Creates new form runnerTaskHistory
     */
    public runnerTaskHistory() {
        initComponents();
        setLocationRelativeTo(null);
        
        fetchAllData(); // Show all data on startup
    
    }

    private void fetchAllData() {
        Runner runner = new Runner();
        List<String[]> completedTasks = runner.getCompletedTask(runnerEmail);

            // Example: Display tasks in the console or use them to populate a table
            for (String[] task : completedTasks) {
                String orderId = task[0]; 
                String cusName = task[4]; 
                
                String itemIDString = task[2]; 
                // Extract the itemIDs
                itemIDString = itemIDString.replaceAll("[\\[\\]]", "");
                String[] itemIDs = itemIDString.split(";");

                // Get vendor info for the first item
                String firstItemID = itemIDs[0];

                Item item = new Item();
                String[] vendorInfo = item.getVendorInfoByItemID(Integer.parseInt(firstItemID.trim()));
                String vendorName = vendorInfo[1];
                
                String deliveryFee = task[7]; 
                String orderDate = task[9];
                
                DefaultTableModel model = (DefaultTableModel) taskHistoryJT.getModel();
                model.setRowCount(0);
                
                model.addRow(new Object[]{
                        orderId, cusName, vendorName, orderDate, deliveryFee
                });
            }
    }

            /**
    private void generateBarChart(ResultSet data) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            if (data == null) { // Placeholder for initial loading
                // Generate dummy data
                for (int i = 1; i <= 12; i++) {
                    dataset.addValue(Math.random() * 100, "Tasks", "Month " + i);
                }
            } else {
                while (data.next()) {
                    int month = data.getDate("date").toLocalDate().getMonthValue();
                    dataset.incrementValue(data.getInt("task_count"), "Tasks", "Month " + month);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Task History", "Month", "Total Tasks",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 300));
        barChartJL.removeAll();
        barChartJL.add(panel);
        barChartJL.validate();
    }
    *         * */

    private void filterData(String type, Object... params) {
        /**
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdb", "username", "password");
            String query = "";
            PreparedStatement stmt;

            switch (type) {
                case "Daily":
                    query = "SELECT * FROM tasks WHERE date BETWEEN ? AND ? AND runner_email = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setDate(1, new java.sql.Date(((java.util.Date) params[0]).getTime()));
                    stmt.setDate(2, new java.sql.Date(((java.util.Date) params[1]).getTime()));
                    break;
                case "Monthly":
                    query = "SELECT * FROM tasks WHERE MONTH(date) BETWEEN ? AND ? AND runner_email = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, (Integer) params[0]);
                    stmt.setInt(2, (Integer) params[1]);
                    break;
                case "Yearly":
                    query = "SELECT * FROM tasks WHERE YEAR(date) BETWEEN ? AND ? AND runner_email = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, (Integer) params[0]);
                    stmt.setInt(2, (Integer) params[1]);
                    break;
                default:
                    return;
            }
            stmt.setString(3, runnerEmail);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) taskHistoryJT.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("vendor"),
                        rs.getDate("date"),
                        rs.getDouble("income")
                });
            }
            generateBarChart(rs); // Generate chart for filtered data
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering data: " + e.getMessage());
        }
        **/
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
        endDate = new com.toedter.calendar.JDateChooser();
        DgenerateJB = new javax.swing.JButton();
        startDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        monthJP = new javax.swing.JPanel();
        jMonthChooser1 = new com.toedter.calendar.JMonthChooser();
        jMonthChooser2 = new com.toedter.calendar.JMonthChooser();
        MgenerateJB = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        yearJP = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        jYearChooser2 = new com.toedter.calendar.JYearChooser();
        YgenerateJB = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        barChartJL = new javax.swing.JLabel();
        allJB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Task History");

        backJB.setText("<  Main Menu");
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
                .addGap(15, 15, 15)
                .addComponent(backJB)
                .addGap(158, 158, 158)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(endDate, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(startDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(endDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(65, Short.MAX_VALUE)
                .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jMonthChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(MgenerateJB)
                .addGap(38, 38, 38))
        );
        monthJPLayout.setVerticalGroup(
            monthJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, monthJPLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(monthJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(monthJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jMonthChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jMonthChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(monthJPLayout.createSequentialGroup()
                        .addComponent(MgenerateJB)
                        .addGap(3, 3, 3)))
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
                .addComponent(barChartJL, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
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
        filterData("Daily", startDate.getDate(), endDate.getDate());
    }//GEN-LAST:event_DgenerateJBActionPerformed

    private void MgenerateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MgenerateJBActionPerformed
        filterData("Monthly", jMonthChooser1.getMonth() + 1, jMonthChooser2.getMonth() + 1);
    }//GEN-LAST:event_MgenerateJBActionPerformed

    private void YgenerateJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_YgenerateJBActionPerformed
        filterData("Yearly", jYearChooser1.getYear(), jYearChooser2.getYear());
    }//GEN-LAST:event_YgenerateJBActionPerformed

    private void allJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allJBActionPerformed
        fetchAllData();
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
            java.util.logging.Logger.getLogger(runnerTaskHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(runnerTaskHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(runnerTaskHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(runnerTaskHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new runnerTaskHistory().setVisible(true);
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
    private javax.swing.JPanel dayJP;
    private com.toedter.calendar.JDateChooser endDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private com.toedter.calendar.JMonthChooser jMonthChooser1;
    private com.toedter.calendar.JMonthChooser jMonthChooser2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private com.toedter.calendar.JYearChooser jYearChooser2;
    private javax.swing.JPanel monthJP;
    private com.toedter.calendar.JDateChooser startDate;
    private javax.swing.JTable taskHistoryJT;
    private javax.swing.JPanel yearJP;
    // End of variables declaration//GEN-END:variables
}