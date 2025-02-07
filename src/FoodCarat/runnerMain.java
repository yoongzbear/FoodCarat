/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class runnerMain extends javax.swing.JFrame {

    private String email = User.getSessionEmail();
    private String name = User.getSessionName();
    Runner runner = new Runner();

    public runnerMain() {
        initComponents();
        setLocationRelativeTo(null);
        username.setText("");
        String[] runnerDetails = runner.getRunnerDetails(email);

        if (runnerDetails != null) {
            String plateNumber = runnerDetails[1];
            String salary = runnerDetails.length > 2 ? runnerDetails[3] : null;

            // Use HTML for multiline text
            StringBuilder textBuilder = new StringBuilder("<html>");
            textBuilder.append(name).append("<br>");
            textBuilder.append(plateNumber).append("<br>");

            if (salary != null && !salary.isEmpty()) {
                textBuilder.append("RM ").append(salary);
            }

            textBuilder.append("</html>");
            username.setText(textBuilder.toString());
        }

        // Status
        String status = runnerDetails[2];
        ImageIcon icon;

        if ("available".equalsIgnoreCase(status)) {
            avaJRB.setSelected(true);
            icon = new ImageIcon("images/runner/available.png");
        } else {
            unaJRB.setSelected(true);
            icon = new ImageIcon("images/runner/unavailable.jpg");
        }

        // Resize the image to 200x200
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);

        // Set the resized icon
        statusImage.setIcon(resizedIcon);

        displayCurrentTask();

        loadLatestCompletedTasks(notificJT);
    }

    // Display Current Task in table
    private void displayCurrentTask() {
        Order order = new Order();
        List<String[]> allOrders = order.getAllOrders();

        for (String[] orderData : allOrders) {
            if (email.equals(orderData[5]) && !orderData[3].equalsIgnoreCase("assigning runner") && !orderData[3].equalsIgnoreCase("completed")) {

                Item item = new Item();
                String itemIDString = orderData[2].replaceAll("[\\[\\]]", "");
                String[] itemDetails = itemIDString.split("\\|");
                StringBuilder formattedItems = new StringBuilder();

                for (int i = 0; i < itemDetails.length; i++) {
                    String[] parts = itemDetails[i].split(";");
                    int itemID = Integer.parseInt(parts[0]);
                    int quantity = Integer.parseInt(parts[1]);

                    // Get item name from Item class
                    String[] itemData = item.itemData(itemID);
                    String itemName = itemData != null && itemData.length > 1 ? itemData[1] : "Unknown Item";

                    // Format item and quantity
                    if (i > 0) {
                        formattedItems.append(", ");
                    }
                    formattedItems.append(itemName).append("[").append(quantity).append("]");
                }

                String items = formattedItems.toString(); // Combine all items into a single string

                String[] vendorInfo = item.getVendorInfoByItemID(Integer.parseInt(itemDetails[0].split(";")[0].trim()));
                String vendorName = vendorInfo[1];

                String[] customerInfo = new User().getUserInfo(orderData[4]);
                String customerName = customerInfo[1];

                String address = new Customer().getCustomerAddress(orderData[4]);
                String contactNumber = (customerInfo != null && customerInfo.length > 5) ? customerInfo[5] : "Not available";

                String deliveryFee = orderData[7];
                String status = orderData[3].substring(0, 1).toUpperCase() + orderData[3].substring(1).toLowerCase();

                taskInfo.removeAll();
                taskInfo.setText("<html>Vendor Name: " + vendorName + "<br>"
                        + "Item(s): " + items + "<br>"
                        + "Customer Name: " + customerName + "<br>"
                        + "Customer Contact No.: " + contactNumber + "<br>"
                        + "Address: " + address + "<br>"
                        + "Status: " + status + "<br>"
                        + "Delivery Fee: " + deliveryFee + "</html>");
            }
        }
    }

    public void loadLatestCompletedTasks(JTable table) {
        DefaultTableModel model = (DefaultTableModel) notificJT.getModel();
        model.setRowCount(0);

        List<String[]> completedTasks = getLatestCompletedTasks(10);

        for (String[] task : completedTasks) {
            model.addRow(task);
        }

        model.fireTableDataChanged();
    }

    // Fetch latest 10 completed tasks
    public List<String[]> getLatestCompletedTasks(int limit) {
        List<String[]> tasks = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/customerOrder.txt"))) {
            String line;
            List<String[]> tempTasks = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                String status = orderData[3];
                String runnerEmail = orderData[5];

                if (runnerEmail.equalsIgnoreCase(email)) {
                    String date = orderData[9];
                    String orderID = orderData[0];

                    if ("completed".equalsIgnoreCase(status)) {
                        String deliveryFee = orderData[7];
                        tempTasks.add(new String[]{date, "Congratulations! You have a new income! (order: " + orderID + ")", deliveryFee});
                    } else if ("assigning runner".equalsIgnoreCase(status)) {
                        String deliveryFee = "-";
                        tempTasks.add(new String[]{date, "You have been assigned a new task. Please check the task reception area. (order: " + orderID + ")", deliveryFee});
                    }
                }
            }

            tempTasks.sort((a, b) -> {
                LocalDate dateA = LocalDate.parse(a[0], formatter);
                LocalDate dateB = LocalDate.parse(b[0], formatter);

                // Sort by date (Descending)
                int dateComparison = dateB.compareTo(dateA);

                if (dateComparison == 0) {
                    Pattern pattern = Pattern.compile("order: (\\d+)");
                    Matcher matcherA = pattern.matcher(a[1]);
                    Matcher matcherB = pattern.matcher(b[1]);

                    int orderIdA = 0, orderIdB = 0;
                    if (matcherA.find()) {
                        orderIdA = Integer.parseInt(matcherA.group(1));
                    }
                    if (matcherB.find()) {
                        orderIdB = Integer.parseInt(matcherB.group(1));
                    }

                    // Sort by Order ID (Descending)
                    return Integer.compare(orderIdB, orderIdA);
                }

                return dateComparison;
            });

            tasks = tempTasks.subList(0, Math.min(limit, tempTasks.size()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        taskInfo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        avaJRB = new javax.swing.JRadioButton();
        unaJRB = new javax.swing.JRadioButton();
        statusImage = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        viewTaskJB = new javax.swing.JButton();
        THistoryJB = new javax.swing.JButton();
        CusReviewJB = new javax.swing.JButton();
        revenueJB = new javax.swing.JButton();
        logoutButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        username = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notificJT = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setText("Current Task Information");

        taskInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        taskInfo.setText("No Task");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(taskInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taskInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setText("Are You Ready for Task Allocation?");

        statusGroup.add(avaJRB);
        avaJRB.setText("Available");
        avaJRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                avaJRBActionPerformed(evt);
            }
        });

        statusGroup.add(unaJRB);
        unaJRB.setText("Unavailable");
        unaJRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unaJRBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(unaJRB, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(avaJRB, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(statusImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(avaJRB, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(unaJRB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(153, 255, 153));

        viewTaskJB.setText("View Task");
        viewTaskJB.setPreferredSize(new java.awt.Dimension(70, 20));
        viewTaskJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewTaskJBActionPerformed(evt);
            }
        });

        THistoryJB.setText("Task History");
        THistoryJB.setPreferredSize(new java.awt.Dimension(70, 20));
        THistoryJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                THistoryJBActionPerformed(evt);
            }
        });

        CusReviewJB.setText("Customer Review");
        CusReviewJB.setPreferredSize(new java.awt.Dimension(70, 20));
        CusReviewJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CusReviewJBActionPerformed(evt);
            }
        });

        revenueJB.setText("Revenue");
        revenueJB.setMinimumSize(new java.awt.Dimension(72, 23));
        revenueJB.setPreferredSize(new java.awt.Dimension(70, 20));
        revenueJB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revenueJBActionPerformed(evt);
            }
        });

        logoutButton.setText("Log Out");
        logoutButton.setPreferredSize(new java.awt.Dimension(70, 20));
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(THistoryJB, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                    .addComponent(viewTaskJB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logoutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(revenueJB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CusReviewJB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(viewTaskJB, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(THistoryJB, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(CusReviewJB, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(revenueJB, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        jPanel4.setBackground(new java.awt.Color(153, 255, 153));

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel1.setText("Runner Main Page");

        username.setBackground(new java.awt.Color(255, 255, 255));
        username.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        username.setText("(Ruuner Name)");

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel4.setText("FoodCarat Food Court");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(username)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(196, 196, 196)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(username))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel3.setText("Notification");

        notificJT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Message", "Earnings (RM)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(notificJT);
        if (notificJT.getColumnModel().getColumnCount() > 0) {
            notificJT.getColumnModel().getColumn(0).setPreferredWidth(25);
            notificJT.getColumnModel().getColumn(1).setPreferredWidth(405);
            notificJT.getColumnModel().getColumn(2).setPreferredWidth(30);
        }

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 14, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void viewTaskJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTaskJBActionPerformed
        dispose();
        new runnerViewTask().setVisible(true);
    }//GEN-LAST:event_viewTaskJBActionPerformed

    private void THistoryJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_THistoryJBActionPerformed
        dispose();
        new runnerTaskHistory().setVisible(true);
    }//GEN-LAST:event_THistoryJBActionPerformed

    private void CusReviewJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CusReviewJBActionPerformed
        dispose();
        new runnerCusReview().setVisible(true);
    }//GEN-LAST:event_CusReviewJBActionPerformed

    private void revenueJBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revenueJBActionPerformed
        dispose();
        new runnerRevenue().setVisible(true);
    }//GEN-LAST:event_revenueJBActionPerformed

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to log out? Status will be changed to Unavailable.", "Runner Log Out", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            runner.updateRunnerStatus(email, "unavailable");
            runner.logOut();
            JOptionPane.showMessageDialog(null, "You have logged out.", "Log Out", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new userLogin().setVisible(true);
        }
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void avaJRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_avaJRBActionPerformed
        String status = "available";

        runner.updateRunnerStatus(email, status);

        ImageIcon icon = new ImageIcon("images/runner/available.png");
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        statusImage.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_avaJRBActionPerformed

    private void unaJRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unaJRBActionPerformed
        String status = "unavailable";

        runner.updateRunnerStatus(email, status);

        ImageIcon icon = new ImageIcon("images/runner/unavailable.jpg");
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        statusImage.setIcon(new ImageIcon(scaledImage));
    }//GEN-LAST:event_unaJRBActionPerformed

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
            java.util.logging.Logger.getLogger(runnerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(runnerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(runnerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(runnerMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new runnerMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CusReviewJB;
    private javax.swing.JButton THistoryJB;
    private javax.swing.JRadioButton avaJRB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logoutButton;
    private javax.swing.JTable notificJT;
    private javax.swing.JButton revenueJB;
    private javax.swing.ButtonGroup statusGroup;
    private javax.swing.JLabel statusImage;
    private javax.swing.JLabel taskInfo;
    private javax.swing.JRadioButton unaJRB;
    private javax.swing.JLabel username;
    private javax.swing.JButton viewTaskJB;
    // End of variables declaration//GEN-END:variables
}
