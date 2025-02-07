/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class customerOrderHistory extends javax.swing.JFrame {

    private String email = User.getSessionEmail();

    /**
     * Creates new form customerOrderHistory
     */
    public customerOrderHistory() {
        initComponents();
        populateTable();
        addTableListener();
        //updateCbValueItems();
        bAction.setEnabled(false);
        bReceipt.setEnabled(false);
        bFeedback.setVisible(false);
        lRunnerNameTitle.setVisible(false);
        lRunnerName.setVisible(false);
        lRunnerRating.setVisible(false);
        cbRunnerRating.setVisible(false);
        labelCancel.setVisible(false);
        lcancelReason.setText("");

        //dont show the label first
        lOrderID.setText("");
        lOrderType.setText("");
        ltotalPrice.setText("");
        lVendorName.setText("");
        lOrderStatus.setText("");

        //invisible time range filter
        selectLabel.setVisible(false);
        dateChooser.setVisible(false);
        monthChooser.setVisible(false);
        yearChooser.setVisible(false);

        setLocationRelativeTo(null);
        jPanel1.setBackground(new java.awt.Color(180, 200, 234));
    }

    private Map<String, List<String[]>> orderDetailsMap = new HashMap<>();

    private void addTableListener() {
        tOrderHistory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tOrderHistory.getSelectedRow();
                    if (selectedRow != -1) {
                        try {
                            //Get the selected row data
                            String orderID = (String) tOrderHistory.getValueAt(selectedRow, 1);
                            String orderType = (String) tOrderHistory.getValueAt(selectedRow, 2);
                            String totalPrice = (String) tOrderHistory.getValueAt(selectedRow, 4);
                            String vendorName = (String) tOrderHistory.getValueAt(selectedRow, 5);
                            String orderStatus = (String) tOrderHistory.getValueAt(selectedRow, 6);
                            String cancelReason = (String) tOrderHistory.getValueAt(selectedRow, 7);
                            String rRunnerName = (String) tOrderHistory.getValueAt(selectedRow, 8);

                            //Get feedback
                            Review review = new Review(Integer.parseInt(orderID));
                            String feedback = review.getFeedback();
                            String[] feedbackParts = feedback.split(",");
                            String orderFeedback = feedbackParts[1];
                            String vendorRating = feedbackParts[2];
                            String vendorFeedback = feedbackParts[3];
                            String runnerRating = feedbackParts[4];
                            //Set the data to text fields
                            lOrderID.setText(orderID);
                            lOrderType.setText(orderType);
                            ltotalPrice.setText(totalPrice);
                            lVendorName.setText(vendorName);
                            lOrderStatus.setText(orderStatus);
                            bReceipt.setEnabled(true);
                            boolean validFeedback = true;
                            //Check feedback based on the reviewType
                            if ("null".equals(feedbackParts[1]) || "null".equals(feedbackParts[2]) || "null".equals(feedbackParts[3])) {
                                validFeedback = false;
                            }

                            if ("completed".equalsIgnoreCase(orderStatus)) {
                                labelCancel.setVisible(false);
                                lcancelReason.setText("");
                                bAction.setEnabled(true);
                                bAction.setText("Reorder");
                            } else if ("pending accept".equalsIgnoreCase(orderStatus)) {
                                labelCancel.setVisible(false);
                                lcancelReason.setText("");
                                bAction.setEnabled(true);
                                bAction.setText("Cancel Order");
                            } else if ("cancelled".equalsIgnoreCase(orderStatus)) {
                                bAction.setEnabled(false);
                                bAction.setText("Reorder");
                                labelCancel.setVisible(true);
                                lcancelReason.setText(cancelReason);
                            } else {
                                labelCancel.setVisible(false);
                                lcancelReason.setText("");
                                bAction.setEnabled(false);
                                bAction.setText("Reorder");
                            }
                            if ("delivery".equalsIgnoreCase(orderType.trim())) {
                                // Set visibility of components related to the runner
                                lRunnerName.setText(rRunnerName);
                                lRunnerNameTitle.setVisible(true);
                                lRunnerName.setVisible(true);
                                lRunnerRating.setVisible(true);
                                cbRunnerRating.setVisible(true);
                            } else {
                                // Set visibility of components when not a "Delivery" order
                                lRunnerNameTitle.setVisible(false);
                                lRunnerName.setVisible(false);
                                lRunnerRating.setVisible(false);
                                cbRunnerRating.setVisible(false);
                            }
                            //Feedback
                            if (validFeedback) {
                                //Feedback exists, set the text area to read-only and display feedback
                                taOrderFeedback.setText(orderFeedback);
                                taOrderFeedback.setEditable(false);
                                cbVendorRating.setSelectedItem(vendorRating + " 🌟");
                                cbVendorRating.setEditable(false);

                                taVendorFeedback.setText(vendorFeedback);
                                taVendorFeedback.setEditable(false);
                                cbRunnerRating.setSelectedItem(runnerRating + " 🌟");
                                cbRunnerRating.setEditable(false);
                                bFeedback.setVisible(false);
                            } else if (!validFeedback && "Completed".equals(orderStatus)) {
                                //No feedback, allow the user to enter feedback
                                taOrderFeedback.setText("");
                                taOrderFeedback.setEditable(true);
                                cbVendorRating.setEditable(true);

                                //Clear the selection (or reset it to a default choice, if desired)
                                cbVendorRating.setSelectedItem("Please Rate");
                                taVendorFeedback.setText("");
                                taVendorFeedback.setEditable(true);
                                cbRunnerRating.setEditable(true);
                                cbRunnerRating.setSelectedItem("Please Rate");
                                bFeedback.setEnabled(true);
                                bFeedback.setVisible(true);
                            } else {
                                //No feedback for incomplete feedback
                                taOrderFeedback.setText("");
                                taOrderFeedback.setEditable(false);
                                cbVendorRating.setEditable(false);

                                //clear section
                                cbVendorRating.setSelectedItem("Please Rate");
                                taVendorFeedback.setText("");
                                taVendorFeedback.setEditable(false);
                                cbRunnerRating.setEditable(false);
                                cbRunnerRating.setSelectedItem("Please Rate");
                                bFeedback.setEnabled(false);
                            }
                            //For the order items
                            DefaultTableModel model = (DefaultTableModel) tbOrderItem.getModel();
                            DecimalFormat df = new DecimalFormat("0.00");
                            model.setRowCount(0);
                            //Split the order list by semicolon (;) to get individual order items
                            List<String[]> orderItemDetails = orderDetailsMap.get(orderID);
                            //Loop through each item in the order and process it
                            for (String[] item : orderItemDetails) {
                                String itemName = item[1];
                                String itemQuantity = item[2];
                                String itemPrice = item[3];

                                model.addRow(new Object[]{itemName, itemQuantity, "RM" + df.format(Double.parseDouble(itemPrice))});
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(customerOrderHistory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }

    private void populateTable() {
        DefaultTableModel model = new DefaultTableModel();
        DecimalFormat df = new DecimalFormat("0.00");
        model.setRowCount(0);
        model.setColumnCount(0);
        // Header
        model.addColumn("Date");
        model.addColumn("Order ID");
        model.addColumn("Order Type");
        model.addColumn("Order Item");
        model.addColumn("Total Price");
        model.addColumn("Vendor Name");
        model.addColumn("Order Status");
        model.addColumn("Cancel Reason");
        model.addColumn("Runner Name");

        String selectedSearchBy = (String) cbSearchBy.getSelectedItem();
        String selectedValue = (String) cbValue.getSelectedItem();

        List<String[]> orderRecords = new ArrayList<>(); // To store records

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/customerOrder.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                String rOrderStatus = record[3];
                String rUser = record[4];
                if (rUser.equals(email) && rOrderStatus != "") {
                    String rOrderType = record[1];

                    String formattedOrderType = "";
                    switch (rOrderType.toLowerCase()) {
                        case "dine in":
                            formattedOrderType = "Dine In";
                            break;
                        case "take away":
                            formattedOrderType = "Take Away";
                            break;
                        case "delivery":
                            formattedOrderType = "Delivery";
                            break;
                        default:
                            formattedOrderType = "Unknown"; //invalid value
                    }

                    String rOrderList = record[2].replace("[", "").replace("]", "");
                    String rVendorName = null;
                    String rRunnerEmail = record[5];
                    String rRunnerName = "";
                    User user = new User();
                    if (!"null".equalsIgnoreCase(rRunnerEmail)) {
                        String[] runnerInfo = user.getUserInfo(rRunnerEmail);
                        rRunnerName = runnerInfo[1];
                    }
                    String rCancelReason = record[6];
                    String rTotalPaid = record[8];
                    String rOrderDate = record[9];
                    rOrderStatus = rOrderStatus.substring(0, 1).toUpperCase() + rOrderStatus.substring(1).toLowerCase();
                    if (rCancelReason.equalsIgnoreCase("null") || rCancelReason.equalsIgnoreCase("NULL") || rCancelReason.isEmpty()) {
                        rCancelReason = "-";
                    } else {
                        rCancelReason = rCancelReason.substring(0, 1).toUpperCase() + rCancelReason.substring(1).toLowerCase();
                    }

                    // Split the order items by semicolon
                    String[] orderItems = rOrderList.split("\\|");
                    StringBuilder orderItemsConcatenated = new StringBuilder();
                    double totalPrice = 0.0;

                    List<String[]> orderItemDetails = new ArrayList<>();

                    // Loop through each item in the order and concatenate them with a comma
                    for (int i = 0; i < orderItems.length; i++) {
                        String[] itemDetails = orderItems[i].split(";");
                        int rOrderItemID = Integer.parseInt(itemDetails[0]);
                        String rItemQuantity = itemDetails[1];

                        Item item1 = new Item();
                        String[] itemInfo = item1.itemData(rOrderItemID);
                        String itemID = itemInfo[0];
                        String itemName = itemInfo[1];
                        String itemPrice = itemInfo[3];

                        String[] vendorInfo = item1.getVendorInfoByItemID(Integer.parseInt(itemID));
                        rVendorName = vendorInfo[1];

                        orderItemDetails.add(new String[]{String.valueOf(rOrderItemID), itemName, rItemQuantity, itemPrice});

                        // Update total price
                        totalPrice = totalPrice + Double.parseDouble(itemPrice) * Integer.parseInt(rItemQuantity);

                        // Append item to the StringBuilder with a comma
                        if (i > 0) {
                            orderItemsConcatenated.append(", ");
                        }
                        orderItemsConcatenated.append(itemName);
                    }

                    // Get the final concatenated string
                    String allOrderItems = orderItemsConcatenated.toString();

                    String orderID = record[0];
                    orderDetailsMap.put(orderID, orderItemDetails);

                    // Add the record to the list
                    orderRecords.add(new String[]{
                        rOrderDate, orderID, formattedOrderType, allOrderItems, "RM" + df.format(Double.parseDouble(rTotalPaid)), rVendorName, rOrderStatus, rCancelReason, rRunnerName
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the records based on the order date in descending order
        orderRecords.sort((record1, record2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = dateFormat.parse(record1[0]); // rOrderDate is at index 0
                Date date2 = dateFormat.parse(record2[0]);
                return date2.compareTo(date1); // Compare in reverse order (newest first)
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });

        //filter
        for (String[] record : orderRecords) {
            String rOrderDate = record[0];
            String rOrderStatus = record[6]; // Order Status is at index 6

            // Filter by "Time Range"
            if ("Time Range".equals(selectedSearchBy) && !selectedValue.equals("Select Time Range")) {
                if ("Daily".equals(selectedValue)) {
                    String inputTime = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM d, yyyy");
                    SimpleDateFormat recordDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        Date selectedDate = inputDateFormat.parse(inputTime);
                        String formattedInputDate = recordDateFormat.format(selectedDate);
                        if (!formattedInputDate.equals(rOrderDate)) {
                            continue; // Skip this record
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if ("Monthly".equals(selectedValue)) {
                    String inputTime = (monthChooser.getMonth() + 1) + "-" + yearChooser.getYear(); // MM-YYYY format
                    String[] orderDateParts = rOrderDate.split("-");
                    int orderYear = Integer.parseInt(orderDateParts[0]);
                    int orderMonth = Integer.parseInt(orderDateParts[1]);

                    String[] inputTimeParts = inputTime.split("-");
                    int inputMonth = Integer.parseInt(inputTimeParts[0]);
                    int inputYear = Integer.parseInt(inputTimeParts[1]);

                    if (!(orderYear == inputYear && orderMonth == inputMonth)) {
                        continue; // Skip this record
                    }
                } else if ("Yearly".equals(selectedValue)) {
                    String inputTime = String.valueOf(yearChooser.getYear());
                    String[] orderDateParts = rOrderDate.split("-");
                    int orderYear = Integer.parseInt(orderDateParts[0]);

                    if (orderYear != Integer.parseInt(inputTime)) {
                        continue; // Skip this record
                    }
                }
            }

            // Filter by "Order Status"
            if ("Order Status".equals(selectedSearchBy) && !selectedValue.equals("Select Order Status")) {
                if (!selectedValue.equals(rOrderStatus)) {
                    continue; // Skip this record
                }
            }

            // Add the record to the model if it passes the filters
            model.addRow(record);
        }

        // Set the model to the table
        tOrderHistory.setModel(model);

        TableColumn runnerName = tOrderHistory.getColumnModel().getColumn(8);  // 8th column is index 7
        runnerName.setMaxWidth(0);
        runnerName.setMinWidth(0);
        runnerName.setPreferredWidth(0);
    }

    //for selected order from notif
    public void populateTable(String selectedOrderID) {
        DefaultTableModel model = new DefaultTableModel();
        DecimalFormat df = new DecimalFormat("0.00");
        model.setRowCount(0);
        model.setColumnCount(0);
        // Header
        model.addColumn("Date");
        model.addColumn("Order ID");
        model.addColumn("Order Type");
        model.addColumn("Order Item");
        model.addColumn("Total Price");
        model.addColumn("Vendor Name");
        model.addColumn("Order Status");
        model.addColumn("Cancel Reason");
        model.addColumn("runnerName");

        String selectedSearchBy = (String) cbSearchBy.getSelectedItem();
        String selectedValue = (String) cbValue.getSelectedItem();

        List<String[]> orderRecords = new ArrayList<>(); // To store records

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/customerOrder.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                String rOrderStatus = record[3];
                String rUser = record[4];
                if (rUser.equals(email) && rOrderStatus != "") {
                    String rOrderType = record[1];
                    String formattedOrderType = "";
                    switch (rOrderType.toLowerCase()) {
                        case "dine in":
                            formattedOrderType = "Dine In";
                            break;
                        case "take away":
                            formattedOrderType = "Take Away";
                            break;
                        case "delivery":
                            formattedOrderType = "Delivery";
                            break;
                        default:
                            formattedOrderType = "Unknown"; //invalid value
                    }
                    String rOrderList = record[2].replace("[", "").replace("]", "");
                    String rVendorName = null;
                    String rRunnerEmail = record[5];
                    String rRunnerName = "";
                    User user = new User();
                    if (!"null".equalsIgnoreCase(rRunnerEmail)) {
                        String[] runnerInfo = user.getUserInfo(rRunnerEmail);
                        rRunnerName = runnerInfo[1];
                    }
                    String rCancelReason = record[6];
                    String rTotalPaid = record[8];
                    String rOrderDate = record[9];
                    rOrderStatus = rOrderStatus.substring(0, 1).toUpperCase() + rOrderStatus.substring(1).toLowerCase();
                    if (rCancelReason.equalsIgnoreCase("null") || rCancelReason.equalsIgnoreCase("NULL") || rCancelReason.isEmpty()) {
                        rCancelReason = "-";
                    } else {
                        rCancelReason = rCancelReason.substring(0, 1).toUpperCase() + rCancelReason.substring(1).toLowerCase();
                    }

                    // Split the order items by semicolon
                    String[] orderItems = rOrderList.split("\\|");
                    StringBuilder orderItemsConcatenated = new StringBuilder();
                    double totalPrice = 0.0;

                    List<String[]> orderItemDetails = new ArrayList<>();

                    // Loop through each item in the order and concatenate them with a comma
                    for (int i = 0; i < orderItems.length; i++) {
                        String[] itemDetails = orderItems[i].split(";");
                        int rOrderItemID = Integer.parseInt(itemDetails[0]);
                        String rItemQuantity = itemDetails[1];
                        // String rItemPrice = itemDetails[2]; // Need to change based on the vendor ori price

                        Item item1 = new Item();
                        String[] itemInfo = item1.itemData(rOrderItemID);
                        String itemID = itemInfo[0];
                        String itemName = itemInfo[1];
                        String itemPrice = itemInfo[3];

                        String[] vendorInfo = item1.getVendorInfoByItemID(Integer.parseInt(itemID));
                        rVendorName = vendorInfo[1];

                        orderItemDetails.add(new String[]{String.valueOf(rOrderItemID), itemName, rItemQuantity, itemPrice});

                        // Update total price
                        totalPrice = totalPrice + Double.parseDouble(itemPrice) * Integer.parseInt(rItemQuantity);

                        // Append item to the StringBuilder with a comma
                        if (i > 0) {
                            orderItemsConcatenated.append(", ");
                        }
                        orderItemsConcatenated.append(itemName);
                    }

                    // Get the final concatenated string
                    String allOrderItems = orderItemsConcatenated.toString();

                    String orderID = record[0];
                    orderDetailsMap.put(orderID, orderItemDetails);

                    // Add the record to the list
                    orderRecords.add(new String[]{
                        rOrderDate, orderID, formattedOrderType, allOrderItems, "RM" + df.format(Integer.parseInt(rTotalPaid)), rVendorName, rOrderStatus, rCancelReason, rRunnerName
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the records based on the order date in descending order
        orderRecords.sort((record1, record2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = dateFormat.parse(record1[0]); // rOrderDate is at index 0
                Date date2 = dateFormat.parse(record2[0]);
                return date2.compareTo(date1); // Compare in reverse order (newest first)
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });

        //filter
        for (String[] record : orderRecords) {
            String rOrderDate = record[0];
            String rOrderStatus = record[6]; // Order Status is at index 6

            // Filter by "Time Range"
            if ("Time Range".equals(selectedSearchBy) && !selectedValue.equals("Select Time Range")) {
                if ("Daily".equals(selectedValue)) {
                    String inputTime = ((JTextField) dateChooser.getDateEditor().getUiComponent()).getText();
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMM d, yyyy");
                    SimpleDateFormat recordDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        Date selectedDate = inputDateFormat.parse(inputTime);
                        String formattedInputDate = recordDateFormat.format(selectedDate);
                        if (!formattedInputDate.equals(rOrderDate)) {
                            continue; // Skip this record
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if ("Monthly".equals(selectedValue)) {
                    String inputTime = (monthChooser.getMonth() + 1) + "-" + yearChooser.getYear(); // MM-YYYY format
                    String[] orderDateParts = rOrderDate.split("-");
                    int orderYear = Integer.parseInt(orderDateParts[0]);
                    int orderMonth = Integer.parseInt(orderDateParts[1]);

                    String[] inputTimeParts = inputTime.split("-");
                    int inputMonth = Integer.parseInt(inputTimeParts[0]);
                    int inputYear = Integer.parseInt(inputTimeParts[1]);

                    if (!(orderYear == inputYear && orderMonth == inputMonth)) {
                        continue; // Skip this record
                    }
                } else if ("Yearly".equals(selectedValue)) {
                    String inputTime = String.valueOf(yearChooser.getYear());
                    String[] orderDateParts = rOrderDate.split("-");
                    int orderYear = Integer.parseInt(orderDateParts[0]);

                    if (orderYear != Integer.parseInt(inputTime)) {
                        continue; // Skip this record
                    }
                }
            }

            // Filter by "Order Status"
            if ("Order Status".equals(selectedSearchBy) && !selectedValue.equals("Select Order Status")) {
                if (!selectedValue.equals(rOrderStatus)) {
                    continue; // Skip this record
                }
            }

            // Add the record to the model if it passes the filters
            model.addRow(record);
        }

        // Set the model to the table
        tOrderHistory.setModel(model);

        TableColumn runnerName = tOrderHistory.getColumnModel().getColumn(8);  // 8th column is index 7
        runnerName.setMaxWidth(0);
        runnerName.setMinWidth(0);
        runnerName.setPreferredWidth(0);

        if (selectedOrderID != null) {
            for (int row = 0; row < tOrderHistory.getRowCount(); row++) {
                if (tOrderHistory.getValueAt(row, 1).equals(selectedOrderID)) {
                    tOrderHistory.setRowSelectionInterval(row, row); // Select the row with the orderID
                    break; // Exit loop once the order is found
                }
            }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        bBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tOrderHistory = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        lVenFeedbackSection = new javax.swing.JLabel();
        lOrderType = new javax.swing.JLabel();
        lVenRateTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lOrderFeedbackSection = new javax.swing.JLabel();
        cbVendorRating = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        lVendorName = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taVendorFeedback = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lOrderFeedbackTitle = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taOrderFeedback = new javax.swing.JTextArea();
        bAction = new javax.swing.JButton();
        ltotalPrice = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbOrderItem = new javax.swing.JTable();
        lVenFeedbackTitle = new javax.swing.JLabel();
        lRunnerNameTitle = new javax.swing.JLabel();
        bFeedback = new javax.swing.JButton();
        lRunnerName = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lRunnerRating = new javax.swing.JLabel();
        lOrderStatus = new javax.swing.JLabel();
        cbRunnerRating = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        lOrderID = new javax.swing.JLabel();
        cbValue = new javax.swing.JComboBox<>();
        selectLabel = new javax.swing.JLabel();
        dateChooser = new com.toedter.calendar.JDateChooser();
        yearChooser = new com.toedter.calendar.JYearChooser();
        monthChooser = new com.toedter.calendar.JMonthChooser();
        cbSearchBy = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        bSearch = new javax.swing.JButton();
        labelCancel = new javax.swing.JLabel();
        lcancelReason = new javax.swing.JLabel();
        bReceipt = new javax.swing.JButton();
        bClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        bBack.setText("Main Menu");
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("FoodCarat Food Court");

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        jLabel2.setText("Order History");

        tOrderHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tOrderHistory.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tOrderHistory);

        jLabel9.setText("Order Type:");

        lVenFeedbackSection.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        lVenFeedbackSection.setText("Vendor Feedback Section");

        lOrderType.setText("orderType");

        lVenRateTitle.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        lVenRateTitle.setText("Rating:");

        jLabel3.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        jLabel3.setText("Order Details");

        lOrderFeedbackSection.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        lOrderFeedbackSection.setText("Order Feedback Section");

        cbVendorRating.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please Rate", "1 🌟", "2 🌟", "3 🌟", "4 🌟", "5 🌟" }));

        jLabel4.setText("Vendor Name :");

        lVendorName.setText("vendorName");

        taVendorFeedback.setColumns(20);
        taVendorFeedback.setRows(5);
        jScrollPane3.setViewportView(taVendorFeedback);

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        jLabel6.setText("Ordered Item(s) :");

        jLabel7.setText("Total Price Paid:");

        lOrderFeedbackTitle.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        lOrderFeedbackTitle.setText("Order Feedback :");

        taOrderFeedback.setColumns(20);
        taOrderFeedback.setRows(5);
        jScrollPane2.setViewportView(taOrderFeedback);

        bAction.setText("Reorder");
        bAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bActionActionPerformed(evt);
            }
        });

        ltotalPrice.setText("totalPrice");

        tbOrderItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order Item", "Quantity", "Price"
            }
        ));
        tbOrderItem.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tbOrderItem);

        lVenFeedbackTitle.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        lVenFeedbackTitle.setText("Feedback:");

        lRunnerNameTitle.setText("Runner Name:");

        bFeedback.setText("Save Feedback");
        bFeedback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFeedbackActionPerformed(evt);
            }
        });

        lRunnerName.setText("runnerName");

        jLabel5.setText("Order Status:");

        lRunnerRating.setFont(new java.awt.Font("Cooper Black", 0, 12)); // NOI18N
        lRunnerRating.setText("Runner Rating:");

        lOrderStatus.setText("orderStatus");

        cbRunnerRating.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Please Rate", "1 🌟", "2 🌟", "3 🌟", "4 🌟", "5 🌟" }));

        jLabel8.setText("Order ID:");

        lOrderID.setText("orderID");

        cbValue.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Order Status", "Completed", "Cancelled" }));
        cbValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbValueActionPerformed(evt);
            }
        });

        selectLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        selectLabel.setText("Select:");

        cbSearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Order Status", "Time Range" }));
        cbSearchBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSearchByActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        jLabel11.setText("Search By:");

        bSearch.setText("Search");
        bSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSearchActionPerformed(evt);
            }
        });

        labelCancel.setText("Cancel Reason:");

        lcancelReason.setText("cancelReason");

        bReceipt.setText("View Receipt");
        bReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bReceiptActionPerformed(evt);
            }
        });

        bClear.setText("Clear Filter");
        bClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(selectLabel)
                                    .addGap(18, 18, 18)
                                    .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel11)
                                    .addGap(18, 18, 18)
                                    .addComponent(cbSearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cbValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(monthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(bClear, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(bSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(bReceipt, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(bAction, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel9)
                                                .addComponent(jLabel7)
                                                .addComponent(jLabel5)
                                                .addComponent(lRunnerNameTitle)
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel8)
                                                .addComponent(labelCancel))
                                            .addGap(18, 18, 18)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(ltotalPrice)
                                                .addComponent(lOrderType)
                                                .addComponent(lRunnerName)
                                                .addComponent(lcancelReason)
                                                .addComponent(lVendorName)
                                                .addComponent(lOrderID)))))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(lRunnerRating)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(cbRunnerRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lOrderFeedbackSection, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lOrderFeedbackTitle, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lVenFeedbackSection, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(lVenRateTitle)
                                        .addGap(18, 18, 18)
                                        .addComponent(cbVendorRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lVenFeedbackTitle, javax.swing.GroupLayout.Alignment.LEADING))
                                .addComponent(bFeedback, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(123, 123, 123))
                    .addComponent(bBack))
                .addGap(104, 104, 104))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbSearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(cbValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(selectLabel)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(monthChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(yearChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bSearch)
                            .addComponent(bClear))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(lOrderID))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(lVendorName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(ltotalPrice))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lOrderStatus))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(lOrderType))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lRunnerNameTitle)
                            .addComponent(lRunnerName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelCancel)
                            .addComponent(lcancelReason))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bAction)
                            .addComponent(bReceipt)))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lOrderFeedbackSection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lOrderFeedbackTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lVenFeedbackSection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lVenRateTitle)
                    .addComponent(cbVendorRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lVenFeedbackTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lRunnerRating)
                    .addComponent(cbRunnerRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bFeedback)
                .addGap(205, 205, 205))
        );

        jScrollPane5.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 746, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBackActionPerformed
        this.dispose();
        customerMain frame = new customerMain();
        frame.setVisible(true);
    }//GEN-LAST:event_bBackActionPerformed

    private void bFeedbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFeedbackActionPerformed
        if (taOrderFeedback != null && !taOrderFeedback.getText().isEmpty()
                && taVendorFeedback != null && !taVendorFeedback.getText().isEmpty()
                && !cbVendorRating.getSelectedItem().equals("Please Rate")) {

            String runnerRating = null;
            if (cbRunnerRating.isVisible()) {
                if (cbRunnerRating.getSelectedItem() != null && cbRunnerRating.getSelectedItem().equals("Please Rate")) {
                    JOptionPane.showMessageDialog(null, "Please provide a rating for the runner.");
                    return;
                }
            }

            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to save the feedback? No changes are allowed after saving.");
            if (confirm == JOptionPane.YES_OPTION) {
                int selectedRow = tOrderHistory.getSelectedRow();

                if (selectedRow != -1) {
                    String orderIDString = (String) tOrderHistory.getValueAt(selectedRow, 1);
                    int orderID = Integer.parseInt(orderIDString);
                    String orderFeedback = taOrderFeedback.getText();
                    String selectedVendorRating = (String) cbVendorRating.getSelectedItem();
                    String vendorRating = selectedVendorRating.split(" ")[0];
                    String vendorFeedback = taVendorFeedback.getText();

                    if (cbRunnerRating.isVisible()) {
                        String selectedRunnerRating = (String) cbRunnerRating.getSelectedItem();
                        runnerRating = selectedRunnerRating.split(" ")[0];
                    }

                    Review review = new Review(orderID, orderFeedback, vendorRating, vendorFeedback, runnerRating);
                    review.saveOrderFeedback();

                    taOrderFeedback.setEditable(false);
                    bFeedback.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "No order selected.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Check: Please insert the feedback before saving it.");
        }
    }//GEN-LAST:event_bFeedbackActionPerformed

    private void bActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bActionActionPerformed
        Order order = new Order();
        List<String[]> allOrders = order.getAllOrders();
        String selectedOrderID = lOrderID.getText();
        String selectedOrderStatus = lOrderStatus.getText();

        if (!allOrders.isEmpty()) {
            // Loop through the orders (or access the first one based on your needs)
            for (String[] orderData : allOrders) {
                String orderID = orderData[0];
                // Reorder
                if ("completed".equalsIgnoreCase(selectedOrderStatus) && selectedOrderID.equals(orderID)) {
                    String itemIDString = orderData[2].replaceAll("[\\[\\]]", "");
                    String[] itemIDs = itemIDString.split(";");

                    String firstItemID = itemIDs[0];
                    Item item = new Item();
                    String[] userInfo = item.getVendorInfoByItemID(Integer.parseInt(firstItemID.trim()));
                    Vendor vendor = new Vendor(userInfo[0]);
                    String vendorInfo = vendor.getAvailableMethod();
                    String vendorMethod = vendorInfo.replaceAll("[\\[\\]]", "").trim();

                    if (vendorMethod.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "The store is not available for any order type.", "Unavailable", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String[] availableOrderTypes = vendorMethod.split(";");

                    // Convert order types to have first letter uppercase
                    String[] formattedOrderTypes = new String[availableOrderTypes.length];
                    for (int i = 0; i < availableOrderTypes.length; i++) {
                        formattedOrderTypes[i] = availableOrderTypes[i].substring(0, 1).toUpperCase() + availableOrderTypes[i].substring(1).toLowerCase();
                    }

                    // Show selection dialog for order type
                    int choiceIndex = JOptionPane.showOptionDialog(null, "Please select an order type:", "Start Order!",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, formattedOrderTypes, formattedOrderTypes[0]);

                    if (choiceIndex != JOptionPane.CLOSED_OPTION) {
                        String chosenOrderType = availableOrderTypes[choiceIndex];

                        // Initialize a new order
                        Order Order = new Order(chosenOrderType, email);
                        Order.initialOrder();
                        int newOrderID = Order.getOrderID();

                        String getOrderStr = orderData[2];

                        double reorderTotalPrice = 0.0;

                        DefaultTableModel itemsmodel = (DefaultTableModel) tbOrderItem.getModel();
                        int rowCount = itemsmodel.getRowCount();

                        for (int i = 0; i < rowCount; i++) {
                            String priceString = itemsmodel.getValueAt(i, 2).toString();
                            String quantityString = itemsmodel.getValueAt(i, 1).toString();

                            double itemsPrice = Double.parseDouble(priceString.replace("RM", "").trim());
                            int itemQuantity = Integer.parseInt(quantityString.trim());
                            double itemsTotalPrice = itemsPrice * itemQuantity;
                            reorderTotalPrice += itemsTotalPrice;
                            Order.writeReOrderDetails(newOrderID, getOrderStr, reorderTotalPrice);
                        }

                        customerPayment paymentFrame = new customerPayment(newOrderID, chosenOrderType);
                        paymentFrame.setVisible(true);
                        this.dispose();
                    }
                    return;
                } else if ("pending accept".equalsIgnoreCase(selectedOrderStatus) && selectedOrderID.equals(orderID)) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to cancel order?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            order.updateStatus(Integer.parseInt(selectedOrderID), "cancelled", "customer");
                            order.refund(Integer.parseInt(orderID), email);
                            JOptionPane.showMessageDialog(null, "Your order has been cancelled and refunded");
                            populateTable();
                        } catch (IOException ex) {
                            Logger.getLogger(customerOrderHistory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    break;
                }
            }
        } else {
            bAction.setEnabled(false);  // Disable button if no orders are available
        }
    }//GEN-LAST:event_bActionActionPerformed

    private void cbValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbValueActionPerformed
        String selectedSearchBy = (String) cbSearchBy.getSelectedItem();
        if ("Time Range".equals(selectedSearchBy)) {
            // Get the selected time range and input time
            String selectedRange = (String) cbValue.getSelectedItem();
            if ("Daily".equalsIgnoreCase(selectedRange)) {
                selectLabel.setVisible(true);
                dateChooser.setVisible(true);
                monthChooser.setVisible(false);
                yearChooser.setVisible(false);
            } else if ("Monthly".equalsIgnoreCase(selectedRange)) {
                selectLabel.setVisible(true);
                dateChooser.setVisible(false);
                monthChooser.setVisible(true);
                yearChooser.setVisible(true);
            } else if ("Yearly".equalsIgnoreCase(selectedRange)) {
                selectLabel.setVisible(true);
                dateChooser.setVisible(false);
                monthChooser.setVisible(false);
                yearChooser.setVisible(true);
            } else if ("Select Time Range".equalsIgnoreCase(selectedRange)) {
                selectLabel.setVisible(false);
                dateChooser.setVisible(false);
                monthChooser.setVisible(false);
                yearChooser.setVisible(false);
            }
        }
    }//GEN-LAST:event_cbValueActionPerformed

    private void cbSearchByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSearchByActionPerformed
        String selectedSearchBy = (String) cbSearchBy.getSelectedItem();

        // Clear the current items in cbValue
        cbValue.removeAllItems();

        if ("Time Range".equals(selectedSearchBy)) {
            // Add items for Time Range
            cbValue.addItem("Select Time Range");
            cbValue.addItem("Daily");
            cbValue.addItem("Monthly");
            cbValue.addItem("Yearly");
        } else if ("Order Status".equals(selectedSearchBy)) {
            // Add items for Order Status
            cbValue.addItem("Select Order Status");
            cbValue.addItem("Cancelled");
            cbValue.addItem("Completed");

            // Hide time range filter
            selectLabel.setVisible(false);
            dateChooser.setVisible(false);
            monthChooser.setVisible(false);
            yearChooser.setVisible(false);
        }
    }//GEN-LAST:event_cbSearchByActionPerformed

    private void bSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSearchActionPerformed
        populateTable();
    }//GEN-LAST:event_bSearchActionPerformed

    private void bReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bReceiptActionPerformed
        String orderIDStr = lOrderID.getText();
        int orderID = Integer.parseInt(orderIDStr);
        String orderType = lOrderType.getText();
        this.dispose();
        customerReceipt frame = new customerReceipt(orderID, orderType);
        frame.setVisible(true);
    }//GEN-LAST:event_bReceiptActionPerformed

    private void bClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bClearActionPerformed
        cbSearchBy.setSelectedIndex(0); //default value for cbSearchBy
        cbValue.setSelectedIndex(0);    //default value for cbValue

        dateChooser.setDate(null); //reset date chooser
        monthChooser.setMonth(0); //reset month chooser (January)
        yearChooser.setYear(Calendar.getInstance().get(Calendar.YEAR)); //reset to current year

        selectLabel.setVisible(false);
        dateChooser.setVisible(false);
        monthChooser.setVisible(false);
        yearChooser.setVisible(false);

        //re-populate the table without applying any filters
        populateTable();
    }//GEN-LAST:event_bClearActionPerformed

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
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(customerOrderHistory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new customerOrderHistory().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAction;
    private javax.swing.JButton bBack;
    private javax.swing.JButton bClear;
    private javax.swing.JButton bFeedback;
    private javax.swing.JButton bReceipt;
    private javax.swing.JButton bSearch;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbRunnerRating;
    private javax.swing.JComboBox<String> cbSearchBy;
    private javax.swing.JComboBox<String> cbValue;
    private javax.swing.JComboBox<String> cbVendorRating;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel lOrderFeedbackSection;
    private javax.swing.JLabel lOrderFeedbackTitle;
    private javax.swing.JLabel lOrderID;
    private javax.swing.JLabel lOrderStatus;
    private javax.swing.JLabel lOrderType;
    private javax.swing.JLabel lRunnerName;
    private javax.swing.JLabel lRunnerNameTitle;
    private javax.swing.JLabel lRunnerRating;
    private javax.swing.JLabel lVenFeedbackSection;
    private javax.swing.JLabel lVenFeedbackTitle;
    private javax.swing.JLabel lVenRateTitle;
    private javax.swing.JLabel lVendorName;
    private javax.swing.JLabel labelCancel;
    private javax.swing.JLabel lcancelReason;
    private javax.swing.JLabel ltotalPrice;
    private com.toedter.calendar.JMonthChooser monthChooser;
    private javax.swing.JLabel selectLabel;
    private javax.swing.JTable tOrderHistory;
    private javax.swing.JTextArea taOrderFeedback;
    private javax.swing.JTextArea taVendorFeedback;
    private javax.swing.JTable tbOrderItem;
    private com.toedter.calendar.JYearChooser yearChooser;
    // End of variables declaration//GEN-END:variables
}
