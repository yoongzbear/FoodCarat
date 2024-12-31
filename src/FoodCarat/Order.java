package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
    private String orderFeedback;
    private String vendorRating;
    private String vendorFeedback;
    private String runnerRating;
    private String customerEmail;
    private int runnerID;
    private int reasonID;
    
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
    
    public Order(int orderID, String orderFeedback, String vendorRating, String vendorFeedback, String runnerRating){ //to save customer order feedback with runner
        this.orderID = orderID;
        this.orderFeedback = orderFeedback;
        this.vendorRating = vendorRating;
        this.vendorFeedback = vendorFeedback;
        this.runnerRating = runnerRating;
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

    public String getVendorRating() {
        return vendorRating;
    }

    public void setVendorRating(String vendorRating) {
        this.vendorRating = vendorRating;
    }

    public String getVendorFeedback() {
        return vendorFeedback;
    }

    public void setVendorFeedback(String vendorFeedback) {
        this.vendorFeedback = vendorFeedback;
    }

    public String getRunnerRating() {
        return runnerRating;
    }

    public void setRunnerRating(String runnerRating) {
        this.runnerRating = runnerRating;
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
    
    public void initialOrder(){
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
        String newLine = lastOrder + "," + orderType + ",,Ordered," + "customerEmail" + ",,";
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
    
    public void deleteIncompleteOrder(int orderID){
        try {
            //Reading the content of the file
            BufferedReader br = new BufferedReader(new FileReader("resources/customerOrder.txt"));
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                String currentOrderID = tokens[0].trim();

                // Skip the line if it matches the order ID to delete
                if (!currentOrderID.equals(String.valueOf(orderID))) {
                    fileContent.append(line).append("\n");
                }
            }
            br.close();

            // Overwriting the file with the updated content
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources/customerOrder.txt"));
            bw.write(fileContent.toString());
            bw.close();

            JOptionPane.showMessageDialog(null, "Order deleted successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while deleting order: " + e.getMessage());
        }
    }
    
    public String getOrderFeedback() throws FileNotFoundException, IOException {
        StringBuilder result = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/review.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String reviewID = parts[0];
                //String orderID = parts[1];  
                String reviewType = parts[2];  
                String rating = parts[3];   
                String review = parts[4];  
                String date = parts[5];  

                //Check if orderID matches the current order's ID
                if (parts[1].equals(String.valueOf(this.orderID))) {
                    System.out.println("OrderID: " + orderID);
                    if (reviewType.equals("order")) {
                        orderFeedback = review != null && !review.trim().isEmpty() ? review : "No feedback provided";  
                    } else if (reviewType.equals("vendor")) {
                        vendorRating = rating != null && !rating.trim().isEmpty() ? rating : "No rating";
                        vendorFeedback = review != null && !review.trim().isEmpty() ? review : "No feedback provided"; 
                    } else if (reviewType.equals("runner")) {
                        runnerRating = rating != null && !rating.trim().isEmpty() ? rating : "No rating"; 
                    }
                }
            }
        }

        if (orderFeedback != null || vendorRating != null || vendorFeedback != null || runnerRating != null) {
            //Format the result in the requested order
            result.append(orderID).append(",")  // orderID
                  .append(orderFeedback != null ? orderFeedback : "No feedback provided").append(",") 
                  .append(vendorRating != null ? vendorRating : "No rating").append(",") 
                  .append(vendorFeedback != null ? vendorFeedback : "No feedback provided").append(",")
                  .append(runnerRating != null ? runnerRating : "No rating");
        }
        System.out.println(result);
        //Return the formatted result (or empty if nothing found)
        return result.length() > 0 ? result.toString() : "";
    }
    
    public void saveOrderFeedback() {
        int lastFeedback = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/review.txt"));
            String line;
            while((line=br.readLine()) != null){
                String[] record = line.split(",");
                lastFeedback = Integer.parseInt(record[0]);
            }
            lastFeedback = lastFeedback + 1;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        String reviewDate = LocalDate.now().toString(); //Current date for the review
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/review.txt"));
            StringBuilder stringBuilder = new StringBuilder(); 
            String line;

            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");
                int checkBookingID = Integer.parseInt(record[0]);

                if (checkBookingID == orderID) {
                    //1. Write Order Review Record
                    String orderReviewRecord = String.join(",", 
                        String.valueOf(lastFeedback), 
                        String.valueOf(orderID), 
                        "order", 
                        "null",
                        orderFeedback, 
                        reviewDate
                    );
                    stringBuilder.append(orderReviewRecord).append("\n");
                    
                    lastFeedback = lastFeedback + 1;

                    //2. Write Vendor Review Record
                    String vendorReviewRecord = String.join(",", 
                        String.valueOf(lastFeedback), 
                        String.valueOf(orderID), 
                        "vendor", 
                        vendorRating, 
                        vendorFeedback, 
                        reviewDate
                    );
                    stringBuilder.append(vendorReviewRecord).append("\n");

                    //3. Write Runner Review Record
                    if (runnerRating != null) {
                        lastFeedback = lastFeedback + 1;
                        String runnerReviewRecord = String.join(",", 
                            String.valueOf(lastFeedback), 
                            String.valueOf(orderID), 
                            "runner", 
                            runnerRating,
                            "null", 
                            reviewDate
                        );
                        stringBuilder.append(runnerReviewRecord).append("\n");
                    }
                } else {
                    //keep the existing record
                    stringBuilder.append(line).append("\n");
                }
            }
            br.close();

            //Write the updated content back to the file
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources/review.txt"));
            bw.write(stringBuilder.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
