package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ASUS
 */
public class Order {

    private int orderID;
    private int orderTypeChoice;
    private String orderType;
    private int itemID;
    private int orderQuantity;
    private String orderStatus;
    private String customerEmail;
    private String runnerEmail;
    private int reasonID;
    private int totalOrders = 0;
    private double totalRevenue;

    private List<String[]> cart;
    private String orderFile = "resources/customerOrder.txt";
    private String itemFile = "resources/item.txt";
    private String reasonFile = "resources/cancellationReason.txt";
    private String runnerFile = "resources/runner.txt"; //for assign order
    private String customerFile = "resources/customer.txt";

    public Order() {
        this.orderID = 0;
    }

    public Order(String choice) { //for initialOrder()
        this.orderType = choice;
        this.customerEmail = User.getSessionEmail();
    }

    public Order(int orderID) { //for getting customer order feedback
        this.orderID = orderID;
    }

    public Order(String orderType, String customerEmail) { //for cart
        this.orderType = orderType;
        this.customerEmail = customerEmail;
        this.cart = new ArrayList<>();
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getOrderTypeChoice() {
        return orderTypeChoice;
    }

    public void setOrderTypeChoice(int orderTypeChoice) {
        this.orderTypeChoice = orderTypeChoice;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getRunnerEmail() {
        return runnerEmail;
    }

    public void setRunnerID(String runnerEmail) {
        this.runnerEmail = runnerEmail;
    }

    public int getReasonID() {
        return reasonID;
    }

    public void setReasonID(int reasonID) {
        this.reasonID = reasonID;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void incrementOrders() {
        this.totalOrders++;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void incrementRevenue(double revenue) {
        this.totalRevenue += revenue; // Increment revenue only
    }

    public double getAverageValuePerOrder() {
        return getTotalOrders() == 0 ? 0.0 : totalRevenue / getTotalOrders();
    }

    public void initialOrder() { //initial order after customer choose orderType
        //generate orderID
        int lastOrder = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/customerOrder.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");
                lastOrder = Integer.parseInt(record[0]);
            }
            lastOrder = lastOrder + 1;
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.orderID = lastOrder;
        //write order with orderID, orderType and customerEmail
        String newLine = lastOrder + "," + orderType.toLowerCase() + ",,," + customerEmail + ",null,null,0.0,0.0,null";
        try {
            FileWriter fw = new FileWriter(orderFile, true); //true is use for appending data in new line
            fw.write(newLine + "\n");
            fw.close();
            if ("Delivery".equals(orderType)) {
                JOptionPane.showMessageDialog(null, "Please take note that additional charges will be imposed as delivery fee.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    //get all orders 
    public List<String[]> getAllOrders() {
        List<String[]> allOrders = new ArrayList<>();

        try {
            FileReader fr = new FileReader(orderFile);
            BufferedReader br = new BufferedReader(fr);
            String read;

            while ((read = br.readLine()) != null) {
                String[] orderData = read.split(",");
                allOrders.add(orderData);
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from order file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return allOrders;
    }

    //get all orders for the vendor
    public List<String[]> getAllOrders(String vendorEmail) {
        List<String[]> vendorOrders = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        //get all items from vendor
        Item item = new Item();
        List<String[]> vendorItems = item.getAllItems(vendorEmail, false); //for all items
        List<String> allItemIDs = vendorItems.stream()
                .map(data -> data[0]) //get itemID
                .collect(Collectors.toList());

        //get all orders containing items from vendor
        List<String[]> allOrders = getAllOrders();
        //filter orders containing items from vendor
        for (String[] order : allOrders) {
            String orderItems = order[2]; //[itemID;quantity|itemID;quantity]
            if (containsVendorItems(orderItems, allItemIDs)) {
                double totalPrice = calculateTotalPrice(orderItems);
                String[] orderWithTotal = Arrays.copyOf(order, order.length + 1);
                orderWithTotal[order.length] = df.format(totalPrice);
                vendorOrders.add(orderWithTotal);
            }
        }
        return vendorOrders;
    }

    //get order IDs by vendor/runner email
    public List<Integer> getOrderIDsReview(String email, String type) {
        List<Integer> orderIDs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(orderFile)); BufferedReader itemReader = new BufferedReader(new FileReader(itemFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); //orderFile
                if (parts.length < 11 && !"".equals(parts[2])) {
                    if (type.equalsIgnoreCase("vendor")) {
                        String orderItems = parts[2];
                        String currentVendorEmail = "";
                        orderItems = orderItems.replace("[", "").replace("]", "");
                        String[] items = orderItems.split("\\|");
                        for (String item : items) {
                            String[] itemDetails = item.split(";");
                            int itemID1 = Integer.parseInt(itemDetails[0]);
                            Item item1 = new Item();
                            String[] itemData = item1.itemData(itemID1);
                            //currentVendorEmail = itemData[5];
                            if (itemData != null && itemData.length > 5) {
                                currentVendorEmail = itemData[5];
                            }
                        }
                        if (currentVendorEmail.equals(email)) {
                            int orderID1 = Integer.parseInt(parts[0]);
                            orderIDs.add(orderID1);
                        }
                    } else if (type.equalsIgnoreCase("runner")) {
                        String currentRunnerEmail = parts[5];

                        if (currentRunnerEmail.equals(email)) {
                            int orderID1 = Integer.parseInt(parts[0]);
                            orderIDs.add(orderID1);
                        }
                    }
                }
            }
            itemReader.close();
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not find " + email + " in " + type + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return orderIDs;
    }

    //get new orders with status "pending accept"
    public String[] getNewOrder(String vendorEmail) {
        List<String[]> vendorOrders = getAllOrders(vendorEmail);
        //get current date in yyyy-MM-dd
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        for (String[] orderData : vendorOrders) {
            try {
                LocalDate orderDate = LocalDate.parse(orderData[9].trim(), dateFormat);
                if (orderData[3].equalsIgnoreCase("Pending accept") && orderDate.isEqual(currentDate)) {
                    return orderData;
                }
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format for order ID " + orderData[0] + ": " + e.getMessage());
            }
        }
        return null; //no new order
    }

    //get orders based on status for the vendor
    public List<String[]> getOrderByStatus(String vendorEmail, String orderStatus) {
        List<String[]> orderData = new ArrayList<>();
        List<String[]> vendorOrders = getAllOrders(vendorEmail); //get all orders containing items from vendor
        for (String[] order : vendorOrders) { //filter orders containing items from vendor
            if (orderStatus.equalsIgnoreCase(order[3])) {
                orderData.add(order);
            }
        }
        return orderData;
    }

    //count number of items ordered
    public List<String[]> getOrderedItemQuantities(String vendorEmail, String type, String timeRange) { //type = weekly/month/quarter
        List<String[]> itemQuantities = new ArrayList<>();
        List<String[]> vendorItems = new Item().getAllItems(vendorEmail, false); //get items from vendor inlcuding deleted ones
        List<String[]> allOrders = getAllOrders(vendorEmail);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //get start and end date
        LocalDate startDate = null, endDate = null;
        String[] timeRangeParts = timeRange.split(",");
        try {
            if (type.equalsIgnoreCase("weekly")) {
                startDate = LocalDate.parse(timeRangeParts[0], dateFormat);
                endDate = LocalDate.parse(timeRangeParts[1], dateFormat);
            } else if (type.equalsIgnoreCase("monthly")) {
                int month = Integer.parseInt(timeRangeParts[0]);
                int year = Integer.parseInt(timeRangeParts[1]);
                startDate = LocalDate.of(year, month, 1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            } else if (type.equalsIgnoreCase("quarterly")) {
                startDate = LocalDate.parse(timeRangeParts[0], dateFormat);
                endDate = LocalDate.parse(timeRangeParts[1], dateFormat);
            }
        } catch (Exception e) {
            System.err.println("Invalid time range format: " + timeRange);
            return itemQuantities;
        }

        //add initial list of item's ID with 0 quantity
        for (String[] vendorItem : vendorItems) {
            itemQuantities.add(new String[]{vendorItem[0], "0"}); //[itemID, quantity]
        }

        for (String[] order : allOrders) {
            if (!order[3].trim().equalsIgnoreCase("pending accept") && !order[3].trim().equalsIgnoreCase("cancelled")) {
                try {
                    LocalDate orderDate = LocalDate.parse(order[9].trim(), dateFormat);

                    if ((orderDate.isEqual(startDate) || orderDate.isAfter(startDate))
                            && (orderDate.isEqual(endDate) || orderDate.isBefore(endDate))) {
                        String orderItems = order[2];
                        orderItems = orderItems.replaceAll("[\\[\\]]", ""); //remove [ ]

                        String[] orderItemsArray = orderItems.split("\\|");
                        for (String orderItem : orderItemsArray) {
                            String[] itemData = orderItem.split(";");
                            String itemID = itemData[0];
                            int quantity = Integer.parseInt(itemData[1]);

                            // Find the corresponding item from the vendor's items and increment its quantity
                            for (String[] item : itemQuantities) {
                                if (item[0].equals(itemID)) {
                                    item[1] = String.valueOf(Integer.parseInt(item[1]) + quantity);
                                }
                            }
                        }
                    }
                } catch (java.time.format.DateTimeParseException e) {
                    System.err.println("Invalid data in order: " + e.getMessage());
                }
            }

        }
        return itemQuantities;
    }

    //to determine if order contain items from the vendor
    private boolean containsVendorItems(String orderItems, List<String> itemIDs) {
        //[itemID;quantity|itemID;quantity]
        String[] itemDetails = orderItems.replace("[", "").replace("]", "").split("\\|");
        for (String detail : itemDetails) {
            String itemID = detail.split(";")[0];
            if (itemIDs.contains(itemID)) {
                return true;
            }
        }
        return false;
    }

    //helper method to calculate total price of each order
    private double calculateTotalPrice(String orderItems) {
        String[] itemDetails = orderItems.replace("[", "").replace("]", "").split("\\|");
        double totalPrice = 0.0;

        for (String detail : itemDetails) {
            String[] parts = detail.split(";");
            int itemID = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);
            Item item = new Item();
            String priceStr = item.itemData(itemID)[3];
            double price = Double.parseDouble(priceStr);
            totalPrice += price * quantity;
        }
        return totalPrice;
    }

    public String[] getOrder(int id) { //get order based on ID
        String[] orderInfo = new String[12]; //order and vendor information
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            FileReader fr = new FileReader(orderFile);
            BufferedReader br = new BufferedReader(fr);
            String read;

            while ((read = br.readLine()) != null) {
                String[] parts = read.split(",");
                int orderID = Integer.parseInt(parts[0]);
                if (orderID == id) {
                    String orderItems = parts[2]; //[itemID;quantity|itemID;quantity]
                    double totalPrice = calculateTotalPrice(orderItems);

                    //vendor email using first item info
                    String firstItem = orderItems.replace("[", "").replace("]", "").split("\\|")[0];
                    int firstItemID = Integer.parseInt(firstItem.split(";")[0]);
                    Item item = new Item();
                    String[] itemInfo = item.itemData(firstItemID);
                    String vendorEmail = (itemInfo != null) ? itemInfo[5] : "N/A";

                    orderInfo = Arrays.copyOf(parts, parts.length + 2); //extend array with extra 2
                    orderInfo[parts.length] = df.format(totalPrice); //add total price to the end of the row
                    orderInfo[parts.length + 1] = vendorEmail; //add vendor email to the end of the array
                    break;
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return orderInfo;
    }

    public void deleteIncompleteOrder(int orderID) { //delete order if customer back to main without completing the order
        try {
            //Reading the content of the file
            BufferedReader br = new BufferedReader(new FileReader(orderFile));
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                String currentOrderID = tokens[0].trim();

                //Skip the line if it matches the order ID to delete
                if (!currentOrderID.equals(String.valueOf(orderID))) {
                    fileContent.append(line).append("\n");
                }
            }
            br.close();

            //Overwriting the file with the updated content
            BufferedWriter bw = new BufferedWriter(new FileWriter(orderFile));
            bw.write(fileContent.toString());
            bw.close();

            JOptionPane.showMessageDialog(null, "Order cancelled successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while deleting order: " + e.getMessage());
        }
    }

    // add item to cart
    public void addItemToCart(int itemID, String itemName, int quantity, double unitPrice) {
        // Check if the item already exists in the cart
        for (String[] item : cart) {
            if (Integer.parseInt(item[0]) == itemID) {
                int existingQuantity = Integer.parseInt(item[2]);
                item[2] = String.valueOf(existingQuantity + quantity);  // Update quantity if the item already exists
                return;
            }
        }

        // If not, create a new cart entry
        String[] newItem = {String.valueOf(itemID), itemName, String.valueOf(quantity), String.valueOf(unitPrice)};
        cart.add(newItem);
    }

    // remove item from cart
    public void removeItemFromCart(int itemID) {
        cart.removeIf(item -> Integer.parseInt(item[0]) == itemID);
    }

    public void updateItemQuantity(int itemID, int newQuantity) {
        for (String[] item : cart) {
            if (Integer.parseInt(item[0]) == itemID) {
                item[2] = String.valueOf(newQuantity);  // Update the quantity
                break;
            }
        }
    }

    // get the total price of the order for cart
    public double getTotalPrice(List<String[]> cart) {
        double total = 0.0;

        for (String[] item : cart) {
            double price = Double.parseDouble(item[3]);
            int quantity = Integer.parseInt(item[2]);
            total = total + (price * quantity);
        }
        return total;
    }

    public void writeOrderDetails(int orderID, List<String[]> cart) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(orderFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                int currentOrderID = Integer.parseInt(orderData[0]);
                String currentOrderType = orderData[1];

                if (currentOrderID == orderID) {
                    // Calculate the total price and delivery fee
                    double originalPrice = getTotalPrice(cart);  // Get the original total price from the cart
                    double deliveryFee = 0.0;
                    if ("delivery".equals(currentOrderType)) {
                        deliveryFee = calculateDeliveryFee(originalPrice);
                    }
                    double totalPaid = originalPrice + deliveryFee;

                    // Build the order items list
                    StringBuilder orderItems = new StringBuilder();
                    orderItems.append("[");

                    for (int i = 0; i < cart.size(); i++) {
                        String[] item = cart.get(i);  // Get the item at the current index
                        String itemID = item[0];
                        String quantity = item[2];

                        if (i > 0) {
                            orderItems.append("|");
                        }

                        orderItems.append(itemID).append(";").append(quantity);
                    }

                    orderItems.append("]");

                    // Format the total price and delivery fee for display
                    String formattedTotalPaid = String.format("%.2f", totalPaid);
                    String formattedDeliveryFee = String.format("%.2f", deliveryFee);

                    // Update the order data with the new values
                    orderData[2] = orderItems.toString();  // Set order items
                    orderData[7] = formattedDeliveryFee;  // Set delivery fee
                    orderData[8] = formattedTotalPaid;  // Set total paid (price + delivery)

                    // Reconstruct the updated line
                    line = String.join(",", orderData);
                }
                stringBuilder.append(line).append("\n");
            }

            reader.close();

            // Write the updated content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile));
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeReOrderDetails(int newOrderID, String orderItems, double price) {
        try {
            List<String> updatedLines = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(orderFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                int currentOrderID = Integer.parseInt(orderData[0]);

                if (currentOrderID == orderID) {
                    String currentOrderType = orderData[1];
                    double originalPrice = price;
                    double deliveryFee = 0.0;

                    if ("delivery".equalsIgnoreCase(currentOrderType)) {
                        deliveryFee = calculateDeliveryFee(originalPrice);
                    }
                    double totalPaid = originalPrice + deliveryFee;

                    String formattedTotalPaid = String.format("%.2f", totalPaid);
                    String formattedDeliveryFee = String.format("%.2f", deliveryFee);
                    orderData[2] = orderItems;
                    orderData[7] = formattedDeliveryFee;
                    orderData[8] = formattedTotalPaid;

                    line = String.join(",", orderData);
                }
                updatedLines.add(line);

            }

            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile));
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter for cart
    public List<String[]> getCart() {
        return cart;
    }

    public void writePaymentDetails(int orderID, double newPaymentTotal, String today) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(orderFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                int currentOrderID = Integer.parseInt(orderData[0]);

                if (currentOrderID == orderID) {
                    // Update the totalPaid and date fields
                    String formattedPaymentTotal = String.format("%.2f", newPaymentTotal);
                    orderData[3] = "pending accept";
                    orderData[8] = formattedPaymentTotal;  // Set the new total paid (price after payment)
                    orderData[9] = today;  // Set the current date as the payment date

                    // Reconstruct the updated line
                    line = String.join(",", orderData);
                }

                // Append the updated (or unchanged) line to the StringBuilder
                stringBuilder.append(line).append("\n");
            }

            reader.close();

            // Write the updated content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(orderFile));
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double calculateDeliveryFee(double originalPrice) {
        double deliveryFee = 0.0;
        if ("delivery".equals(orderType)) {
            //calculate 15% of the total order amount
            double calculatedFee = originalPrice * 0.15;

            //set delivery fee to min/max based on condition
            if (calculatedFee < 5) {
                deliveryFee = 5;
            } else if (calculatedFee > 20) {
                deliveryFee = 20;
            } else {
                deliveryFee = calculatedFee;
            }
        }
        return deliveryFee;
    }

    public void updateStatus(int id, String newOrderStatus, String userType) {
        // Get order info from order.txt using id
        String[] order = getOrder(id);
        String method = order[1].trim();
        String currentStatus = order[3].trim();
        List<String[]> allOrders = getAllOrders();

        try {
            FileWriter fw = new FileWriter(orderFile);
            BufferedWriter bw = new BufferedWriter(fw);

            for (String[] orderData : allOrders) {
                int orderDataID = Integer.parseInt(orderData[0]);
                if (orderDataID != id) {
                    // Keep the row if the ID does not match
                    bw.write(String.join(",", orderData));
                    bw.newLine();
                } else {
                    // Found the row, update it
                    if (newOrderStatus.equalsIgnoreCase("cancelled") && userType.equalsIgnoreCase("vendor")) {
                        order[6] = "rejected by vendor";
                    }
                    if (newOrderStatus.equalsIgnoreCase("cancelled") && userType.equalsIgnoreCase("runner")) {
                        order[6] = "runner cancelled";
                    }
                    if (newOrderStatus.equalsIgnoreCase("cancelled") && userType.equalsIgnoreCase("customer")) {
                        order[6] = "cancelled by customer";
                    }
                    order[3] = newOrderStatus;
                    if (order.length > 10) { //resize array to standard 
                        order = Arrays.copyOf(order, 10);
                    }

                    bw.write(String.join(",", order));
                    bw.newLine();

                }
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Update order status failed: " + e.getMessage());
            return;
        }

        if ("assigning runner".equalsIgnoreCase(newOrderStatus)) {
            boolean orderAssigned = assignOrderToRunner(order, "");
            if (!orderAssigned) {
                updateStatus(id, "cancelled", "runner");
            }
        }
    }

    public boolean assignOrderToRunner(String[] orderData, String currentRunnerEmail) {
        boolean runnerAssigned = false;
        boolean startAssigning = currentRunnerEmail == null || currentRunnerEmail.isEmpty();
        int totalRunners = 0; // To count the total available runners
        int runnerCount = 0;  // To track the number of runners checked during iteration

        try (BufferedReader runnerReader = new BufferedReader(new FileReader(runnerFile))) {
            String runnerLine;

            // First pass: Count the total number of available runners
            while ((runnerLine = runnerReader.readLine()) != null) {
                String[] runnerData = runnerLine.split(",");

                if (runnerData.length > 2 && "available".equalsIgnoreCase(runnerData[2])) {
                    totalRunners++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try (BufferedReader secondRunnerReader = new BufferedReader(new FileReader(runnerFile))) {
            String runnerLine;

            // Second pass: Assign the task to the next available runner (starting after the current one)
            while ((runnerLine = secondRunnerReader.readLine()) != null) {
                String[] runnerData = runnerLine.split(",");

                if (runnerData.length > 2 && "available".equalsIgnoreCase(runnerData[2])) {
                    runnerCount++;

                    // Start assigning after encountering the current runner email
                    if (!startAssigning) {
                        if (runnerData[0].equalsIgnoreCase(currentRunnerEmail)) {
                            startAssigning = true; // Start processing from the next runner
                        }
                        continue; // Skip this runner if still before the current runner
                    }

                    // Assign the task to the next available runner
                    updateRunnerEmailInOrder(orderData[0], runnerData[0]);
                    runnerAssigned = true;

                    break;
                }
            }

            // If the task was not assigned after iterating all runners, cancel the order
            if (!runnerAssigned && runnerCount == totalRunners) {
                updateStatus(Integer.parseInt(orderData[0]), "cancelled", "runner");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return runnerAssigned;
    }

    // Update runner email in orderFile
    private void updateRunnerEmailInOrder(String orderID, String runnerEmail) {
        List<String[]> allOrders = getAllOrders();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(orderFile))) {
            for (String[] order : allOrders) {
                if (order[0].equals(orderID)) {
                    order[5] = runnerEmail;
                }
                bw.write(String.join(",", order));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getCompletedTask(String runnerEmail) {
        List<String[]> completedTasks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(orderFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] taskData = line.split(",");
                if (taskData.length > 5 && taskData[5].equals(runnerEmail) && taskData[3].equals("completed")) {
                    completedTasks.add(taskData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return completedTasks;
    }

    public void refund(int orderID, String userEmail) throws IOException {
        String[] order = getOrder(orderID);
        String orderEmail = order[0];
        String orderType = order[1];
        String orderItems = order[2];
        String orderStatus = order[3];
        String customerEmail = order[4];
        String runnerEmail = order[5];
        String cancelReason = order[6];
        double deliveryFee = Double.parseDouble(order[7]);
        double totalPaid = Double.parseDouble(order[8]);
        String orderDate = order[9];

        // Calculate the refund amount (for example, 100% refund of total price)
        double priceExcludeDelivery = totalPaid - deliveryFee;
        double orderTotalPrice = calculateTotalPrice(orderItems); //calculate original price before delivery fee and redeem points
        int redeemedPoints = (int) Math.round((orderTotalPrice + deliveryFee - totalPaid) / 0.01); //calculate redeemed points
        Customer customer = new Customer(userEmail);
        int earnedPoints = customer.calculateEarnablePoints(priceExcludeDelivery); //calculate earned points from the order
        customer.addPoints(redeemedPoints); //refund the points redeemed
        customer.deductPoints(earnedPoints); //deduct the points earned

        //refund full credit
        //User user = new User();
        String[] userInfo = customer.customerInfo(userEmail);
        double currentCredit = Double.parseDouble(userInfo[2]);
        double updatedCredit = currentCredit + totalPaid;
        customer.updateCredit(userEmail, updatedCredit, customerFile, 2);
    }
}
