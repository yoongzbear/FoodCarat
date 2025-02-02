/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author User
 */
public class adminNotification extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    private String userFile = "resources/user.txt";
    private String cuscreditFile = "resources/transactionCredit.txt";
    /**
     * Creates new form adminNotification
     */
    public adminNotification() {
        initComponents();
        getContentPane().setBackground(new Color(255, 255, 204));
        setLocationRelativeTo(null);
        // Initialize the table model and set it to the table
        tableModel = (DefaultTableModel) notificationtable.getModel();
        
        setColumnWidths();
        setRowHeight();
        
        Admin admin = new Admin();
        ArrayList<String> allTransactions = admin.getTransactionMessages();
        displayAllTransactions(allTransactions); // Populate the table with all transactions
        
        // Add row selection listener to show receipt on row click
        notificationtable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showReceipt();
            }
        });
    }
    
    private void setColumnWidths() {
        TableColumn column = null;
        column = notificationtable.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);

        column = notificationtable.getColumnModel().getColumn(1);
        column.setPreferredWidth(950);
    }

    private void setRowHeight() {
        notificationtable.setRowHeight(30);
    }

    // Show receipt when a row is clicked
    private void showReceipt() {
        int selectedRow = notificationtable.getSelectedRow();
        if (selectedRow != -1) {
            String message = tableModel.getValueAt(selectedRow, 1).toString(); // The message contains the details

            String[] messageParts = message.split(" ");

            // Extract the transaction ID from the message
            String transactionMessageId = messageParts[messageParts.length - 1].replace(")", "").replace(":", "");

            // Retrieve details from the customer credit.txt file
            Admin admin = new Admin();
            String[] customerDetails = admin.performSearch(transactionMessageId, cuscreditFile);
                    
            if (customerDetails != null) {
                // Parse details from the file
                String transID = customerDetails[0];
                String email = customerDetails[1];
                double currentAmount = Double.parseDouble(customerDetails[2]);
                double topUpAmount = Double.parseDouble(customerDetails[3]);
                String datePayment = customerDetails[4];
                String timePayment = customerDetails[5];
                
                // Retrieve name of customer user.txt base on text file
                String [] customerName = admin.performSearch(email, userFile);
                String name = customerName[1];
                
                // Create and display the receipt window
                adminCusReceipt receiptWindow = new adminCusReceipt(Integer.parseInt(transID), email, name, currentAmount, topUpAmount, datePayment, timePayment);
                receiptWindow.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Transaction details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void displayAllTransactions(ArrayList<String> transactions) {
        // Clear existing rows in the table
        tableModel.setRowCount(0);

        // Populate the table with the transactions
        int transactionNumber = 1; 
        for (String transaction : transactions) {
            tableModel.addRow(new Object[]{transactionNumber++, transaction});
        }

        fillEmptyRowSpace();
    }
    
    private ArrayList<String> getFilteredTransactionsByMonth(ArrayList<String> allTransactions, int month) {
        ArrayList<String> filteredTransactions = new ArrayList<>();

        for (String transaction : allTransactions) {

            String[] parts = transaction.split("\\s+");
            for (String part : parts) {
                if (part.matches("\\d{4}-\\d{2}-\\d{2}")) { // Check for date format "YYYY-MM-DD"
                    String[] dateParts = part.split("-");
                    int transactionMonth = Integer.parseInt(dateParts[1]); // Extract the month
                    if (transactionMonth == month) {
                        filteredTransactions.add(transaction);
                    }
                }
            }
        }

        return filteredTransactions;
    }
    
    private void fillEmptyRowSpace() {           
        int rowCount = tableModel.getRowCount(); // Current row count in the table
        int tableHeight = notificationtable.getParent().getHeight(); // Get the height of the table's parent container
        int rowHeight= notificationtable.getRowHeight();

        int targetRowCount = tableHeight / rowHeight; // Calculate how many rows fit in the visible area

        // Add empty rows if needed to reach the target row count
        int emptyRowsNeeded = targetRowCount - rowCount;
        for (int i = 0; i < emptyRowsNeeded; i++) {
            tableModel.addRow(new Object[]{"", ""});  // Adding empty rows based on the column structure
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        Lnotification = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notificationtable = new javax.swing.JTable();
        backbtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        monthcbx = new javax.swing.JComboBox<>();
        searchbtn = new javax.swing.JButton();

        jButton2.setText("jButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Lnotification.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        Lnotification.setText("Notification");

        notificationtable.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        notificationtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Messages"
            }
        ));
        jScrollPane1.setViewportView(notificationtable);

        backbtn.setFont(new java.awt.Font("Constantia", 1, 18)); // NOI18N
        backbtn.setText("Main Menu");
        backbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backbtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        jLabel1.setText("Month:");

        monthcbx.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        monthcbx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please select", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        searchbtn.setFont(new java.awt.Font("Constantia", 0, 18)); // NOI18N
        searchbtn.setText("Search");
        searchbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(backbtn)
                .addGap(27, 27, 27))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(455, 455, 455)
                        .addComponent(Lnotification))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(56, 56, 56)
                                .addComponent(monthcbx, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(searchbtn))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1099, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addComponent(backbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(Lnotification)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(monthcbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchbtn))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backbtnActionPerformed
        this.dispose();
        new adminMain().setVisible(true);
    }//GEN-LAST:event_backbtnActionPerformed

    private void searchbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchbtnActionPerformed
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
            fillEmptyRowSpace();
            return;
        }
        Admin admin = new Admin();
        ArrayList<String> transactionMessages = admin.getTransactionMessages();

        // Filter transactions by the selected month
        ArrayList<String> filteredTransactions = getFilteredTransactionsByMonth(transactionMessages, monthNumber);
        
        tableModel.setRowCount(0);

        // If there are no transactions, show a message and return
        if (filteredTransactions.isEmpty()) {
            displayAllTransactions(transactionMessages);
            JOptionPane.showMessageDialog(this, "No data available for the selected month.");
            return;
        }else{
            displayAllTransactions(filteredTransactions);
            fillEmptyRowSpace();
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
            java.util.logging.Logger.getLogger(adminNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(adminNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(adminNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(adminNotification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adminNotification().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Lnotification;
    private javax.swing.JButton backbtn;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> monthcbx;
    private javax.swing.JTable notificationtable;
    private javax.swing.JButton searchbtn;
    // End of variables declaration//GEN-END:variables
}
