package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public Order(int orderID, String feedback){ //to save customer order feedback
        this.orderID = orderID;
        this.orderFeedback = feedback;
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
    
    public void initialOrder(){
        int lastOrder = 0;
        //Write order.txt based on orderType
        try {
            BufferedReader br = new BufferedReader(new FileReader("customerOrder.txt"));
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
        
        String newLine = lastOrder + "," + orderType + ",Ordered," + "customerEmail" + ",,";
        try {
            String filename = "customerOrder.txt";
            FileWriter fw = new FileWriter(filename, true); //true is use for appending data in new line
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
            BufferedReader br = new BufferedReader(new FileReader("customerOrder.txt"));
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
            BufferedWriter bw = new BufferedWriter(new FileWriter("customerOrder.txt"));
            bw.write(fileContent.toString());
            bw.close();

            JOptionPane.showMessageDialog(null, "Order deleted successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while deleting order: " + e.getMessage());
        }
    }
    
    public String getOrderFeedback() throws FileNotFoundException, IOException{
        try (BufferedReader reader = new BufferedReader(new FileReader("customerOrder.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(String.valueOf(orderID))) {
                    return parts[4];
                }
            }
        }
        return "";
    }
    
    public void saveOrderFeedback(){
        try{
            BufferedReader br = new BufferedReader(new FileReader("customerOrder.txt"));
            StringBuilder stringBuilder = new StringBuilder(); // Change to StringBuilder
            String line;
            while ((line = br.readLine()) != null) {
                String record[] = line.split(",");
                int checkBookingID = Integer.parseInt(record[0]);
                if (checkBookingID == orderID) {
                    record[4] = orderFeedback;
                    line = String.join(",", record);
                }
                stringBuilder.append(line).append("\n"); 
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter("customerOrder.txt"));
            bw.write(stringBuilder.toString()); 
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
