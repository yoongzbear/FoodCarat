package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
    private int runnerID;
    private int reasonID;
    
    private List<String[]> cart;
    private String orderFile = "resources/customerOrder.txt";
    
    public Order(){ //for deleteIncompleteOrder()
        System.out.println(orderID); //for checking                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
    }
    
    public Order(String choice){ //for initialOrder()
        this.orderType = choice;
        //this.customerEmail = UserSession.getCustomerEmail();
    }
    
    public Order(int orderID){ //for getting customer order feedback
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

    public int getRunnerID() {
        return runnerID;
    }

    public void setRunnerID(int runnerID) {
        this.runnerID = runnerID;
    }

    public int getReasonID() {
        return reasonID;
    }

    public void setReasonID(int reasonID) {
        this.reasonID = reasonID;
    }
    
    public void initialOrder(){ //initial order after customer choose orderType
        //generate orderID
        int lastOrder = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/customerOrder.txt"));
            String line;
            while((line=br.readLine()) != null){
                String[] record = line.split(",");
                lastOrder = Integer.parseInt(record[0]);
            }
            lastOrder = lastOrder + 1;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        this.orderID = lastOrder;
        //write order with orderID, orderType and customerEmail
        //1, Take Away, [1;1|2;1], Ordered, customerEmail, vendor@mail.com, NULL
        String newLine = lastOrder + "," + orderType + ",,," + "customerEmail" + ",null,null";
        try {
            FileWriter fw = new FileWriter("resources/customerOrder.txt", true); //true is use for appending data in new line
            fw.write(newLine + "\n");
            fw.close();
            if ("Delivery".equals(orderType)){
                JOptionPane.showMessageDialog(null, "Please take note that additional charges will be imposed as delivery fee.");
            }
            JOptionPane.showMessageDialog(null, "Order placed successfully! Please wait for Vendor and Runner to accept.");
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
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from order file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return allOrders;
    }
    
    //get all orders for the vendor
    //will change the logic after getting the correct order data
    public List<String[]> getAllOrders(String vendorEmail) {
        List<String[]> vendorOrders = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");

        //get all items from vendor
        Item item = new Item();
        List<String[]> vendorItems = item.getAllItems(vendorEmail);
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
    
    //get new orders with status "pending accept"
    public String[] getNewOrder(String vendorEmail) {
        String[] newOrder = null;
        List<String[]> vendorOrders = getAllOrders(vendorEmail);
        for (String[] orderData : vendorOrders) {
            //1,Take away,[1;1|2;1],Ordered,customerEmail,NULL,NULL,20.00,2025-01-01,27.80
            if (orderData[3].equals("pending accept")) {
                newOrder = orderData;
                break;
            }
        }
        
        return newOrder;
    }
    

    //helper method to determine if order contain items from the vendor
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
            String itemID = parts[0];
            int quantity = Integer.parseInt(parts[1]);
            //double price = getItemPrice(itemID, vendorItems);
            Item item = new Item();
            String priceStr = item.itemData(itemID)[3];
            double price = Double.parseDouble(priceStr);
            totalPrice += price * quantity;
        }

        return totalPrice;
    }
 
    //get order based on ID
    public String[] getOrder(String id) {
        String[] orderInfo = null;
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            FileReader fr = new FileReader(orderFile);
            BufferedReader br = new BufferedReader(fr);
            String read;

            while ((read = br.readLine()) != null) {
                String[] parts = read.split(",");
                if (parts[0].equals(id)) {
                    String orderItems = parts[2]; //[itemID;quantity|itemID;quantity]
                    double totalPrice = calculateTotalPrice(orderItems);
                    orderInfo = Arrays.copyOf(parts, parts.length + 1);
                    orderInfo[parts.length] = df.format(totalPrice); //add total price to the end of the row
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return orderInfo;
    }

    
    public void deleteIncompleteOrder(int orderID){ //delete order if customer back to main without completing the order
        try {
            //Reading the content of the file
            BufferedReader br = new BufferedReader(new FileReader("resources/customerOrder.txt"));
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
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources/customerOrder.txt"));
            bw.write(fileContent.toString());
            bw.close();

            JOptionPane.showMessageDialog(null, "Order deleted successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while deleting order: " + e.getMessage());
        }
    }
    
    //update order status 
    //user can manipulate status: vendor, runner (for delivery)
    //parameter: itemID, orderStatus, userType(not sure if needed, leaving it here first)
    public void updateStatus(String id, String orderStatus, String userType) {
        //get order info from order.txt using id, see order method
        //if delivery - vendor and runner
        //ordered, pending ____idk whats this___, accepted by vendor, accepted by runner, in kitchen, ready, pick up, delivered
        
        //if dine in - vendor
        //ordered, pending ____idk whats this___, accepted by vendor, in kitchen, ready, sent 
        
        //if take away - vendor
        //ordered, pending ____idk whats this___, accepted by vendor, in kitchen, ready, picked up
    }
    
    // Method to add item to cart
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

    // Method to remove item from cart
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

    // Method to get the total price of the order
    public double getTotalPrice() {
        double total = 0.0;
        for (String[] item : cart) {
            double price = Double.parseDouble(item[3]);
            int quantity = Integer.parseInt(item[2]);
            total += price * quantity;
        }
        return total;
    }

    public void writeOrderDetails(int orderID, List<String[]> cart, String orderStatus, String vendorEmail) {
        // Create a StringBuilder to hold the updated content
        StringBuilder stringBuilder = new StringBuilder();

        try {
            // Open the customerOrder.txt file to read its content
            BufferedReader reader = new BufferedReader(new FileReader(orderFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                int currentOrderID = Integer.parseInt(orderData[0]);

                // Check if the orderID matches
                if (currentOrderID == orderID) {
                    // Update order details

                    StringBuilder orderItems = new StringBuilder();
                    orderItems.append("[");

                    for (int i = 0; i < cart.size(); i++) {
                        String[] item = cart.get(i);  // Get the item at the current index
                        String itemID = item[0];  
                        String quantity = item[2]; 

                        // If it's not the first item, append a "|" separator
                        if (i > 0) {
                            orderItems.append("|");
                        }

                        // Append the itemID and quantity
                        orderItems.append(itemID).append(";").append(quantity);
                    }

                    orderItems.append("]");  // End the list with a closing bracket

                    //Update the fields in the orderData array
                    orderData[2] = orderItems.toString(); // orderItem
                    orderData[3] = orderStatus; // orderStatus
                    orderData[5] = vendorEmail; // vendorEmail

                    // Reconstruct the updated line and append it to the StringBuilder
                    line = String.join(",", orderData);
                }

                // Append the (possibly updated) line to the StringBuilder
                stringBuilder.append(line).append("\n");
            }

            // Close the reader
            reader.close();

            // Now write the updated content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter("resources/customerOrder.txt"));
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to clear the cart after the order is placed
    public void clearCart() {
        cart.clear();
    }

    // Method to view all items in the cart
    public void viewCart() {
        StringBuilder cartDetails = new StringBuilder("Items in your cart:\n");
        for (String[] item : cart) {
            cartDetails.append(item[1]).append(" - Quantity: ").append(item[2])
                    .append(" - Total: RM").append(Double.parseDouble(item[3]) * Integer.parseInt(item[2])).append("\n");
        }
        cartDetails.append("Total Price: RM").append(getTotalPrice());
        JOptionPane.showMessageDialog(null, cartDetails.toString());
    }

    // Getter for cart
    public List<String[]> getCart() {
        return cart;
    }
    
    public void writePaymentDetails(int orderID, double newPaymentTotal, String today) {
        // Create a StringBuilder to hold the updated content
        StringBuilder stringBuilder = new StringBuilder();

        try {
            // Open the customerOrder.txt file to read its content
            BufferedReader reader = new BufferedReader(new FileReader(orderFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] orderData = line.split(",");
                int currentOrderID = Integer.parseInt(orderData[0]);

                // Check if the orderID matches
                if (currentOrderID == orderID) {
                    // Update order details

                    StringBuilder orderItems = new StringBuilder();
                    orderItems.append("[");

                    for (int i = 0; i < cart.size(); i++) {
                        String[] item = cart.get(i);  // Get the item at the current index
                        String itemID = item[0];  
                        String quantity = item[2]; 

                        // If it's not the first item, append a "|" separator
                        if (i > 0) {
                            orderItems.append("|");
                        }

                        // Append the itemID and quantity
                        orderItems.append(itemID).append(";").append(quantity);
                    }

                    orderItems.append("]");  // End the list with a closing bracket

                    //Update the fields in the orderData array
                    orderData[2] = orderItems.toString(); // orderItem
                    orderData[3] = orderStatus; // orderStatus
                    orderData[5] = vendorEmail; // vendorEmail

                    // Reconstruct the updated line and append it to the StringBuilder
                    line = String.join(",", orderData);
                }

                // Append the (possibly updated) line to the StringBuilder
                stringBuilder.append(line).append("\n");
            }

            // Close the reader
            reader.close();

            // Now write the updated content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter("resources/customerOrder.txt"));
            writer.write(stringBuilder.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
