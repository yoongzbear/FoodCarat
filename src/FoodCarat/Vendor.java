/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author mastu
 */
public class Vendor extends User{
    //constructor    
    private String email;
    //super(email,password,name);
            
    private String cuisine;
    private String photoLink;
    private String availableMethod;
    private double creditBalance;
    
    private String vendorFile = "resources/vendor.txt";
    
    private String vendorName;
    private double totalRevenue;
    
    public Vendor(String email) {
        //super(email)
        this.email = email;
        getVendorInfo(email);
    }
    
    //constructor for performance tracking
    public Vendor(String vendorName, boolean initializeName) {
        if (initializeName) {
            this.vendorName = vendorName; // Set vendorName if flag is true
        }
        this.totalRevenue = 0.0;
    }
    
    //setters - for current session (not including updating text field)
    public void setEmail(String email) {
        this.email = email;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public void setAvailableMethod(String availableMethod) {
        this.availableMethod = availableMethod;
    }

    public void setCreditBalance(double creditBalance) {
        this.creditBalance = creditBalance;
    }
        
    //getters
    public String getEmail() {
        return email;
    }

    public String getCuisine() {
        return cuisine;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    
    public String getAvailableMethod() {
        return availableMethod;
    }

    public double getCreditBalance() {
        return creditBalance;
    }
    
    public String getVendorName() {
        return vendorName;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getAverageValuePerOrder() {
         return getTotalOrders() == 0 ? 0.0 : totalRevenue / getTotalOrders();
    }

    public void addRevenue(double revenue) {
        this.totalRevenue += revenue; // Increment revenue only
    }
    
    //methods
    //setting vendor info
    private void getVendorInfo(String email){
        try {
            File fileName = new File(vendorFile);
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String read;
            boolean found = false; //flag for finding vendor record
            
            while((read=br.readLine()) !=null ) {
                //scanning the file until it meets null
                String[] data = read.split(",", -1);
                if(data.length > 0 && data[0].equalsIgnoreCase(email)){
                    found = true;
                    //setting the variables
                    setEmail(data[0]);
                    setCuisine(data[1]);
                    setPhotoLink(data[2]);
                    setAvailableMethod(data[3]);
                    setCreditBalance(Double.parseDouble(data[4]));
                }
            }
            fr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }        
    }
    
    //update method availability in text file
    public void updateMethodAvailable(String method) {
        //get the methods stored in the array
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(vendorFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(email)) {
                    parts[3] = method; //update the method
                }
                lines.add(String.join(",", parts)); 
            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
                
        //write into file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(vendorFile))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "Available methods updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to write to the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }  
    
    //insert new item 
//    public void uploadNewItem(int newItemID, String itemName, String itemType, double price, String newImagePath) {
//        Item newItem = new Item(newItemID, itemName, itemType, price, newImagePath, email, "available");
//        newItem.addItem();
//    }
    
    //upload item image
    public String getUploadedImagePath(String imageSource) {
        Item newItem = new Item();
        return newItem.uploadImage(imageSource);
    }
    
    //get latest item 
    public String[] latestItem() {
        Item item = new Item();
        return item.latestItem();
    }
    
    //update item information
    public void updateItemInfo(int itemID, String itemName, String itemType, double itemPrice, String newImagePath, String status) {
        Item updateItem = new Item(itemID, itemName, itemType, itemPrice, newImagePath, email, status);
        updateItem.editItem();
    }
    
    //delete item information
    public void deleteItem(int itemID) {
        Item item = new Item();
        item.deleteItem(itemID, "vendor");
    }
    
    //get order
    public String[] getSpecificOrder(int orderID) {
        return new Order().getOrder(orderID);
    }
    
    //get new order
    public String[] getNewOrder() {
        return new Order().getNewOrder(email);
    }
    
    //get order by status
    public List<String[]> getOrderByStatus(String status) {
        return new Order().getOrderByStatus(email, status);
    }
    
    //update order status
    public void updateOrderStatus(int orderID, String status) {
        new Order().updateStatus(orderID, status, "vendor");
    }
    
    //get all reviews + orders + items for the vendor
    public List<String[]> getAllReviewInfo(String venEmail) {
        List<String[]> allReviewInfo = new ArrayList<>();
        
        //review
        Review review = new Review();
        allReviewInfo = review.getAllReviews(venEmail, "vendor");      
                
        return allReviewInfo;
    }    
    
    //get feedback for order
    public String orderFeedback(int orderID) {
        try {
            return new Review(orderID).getFeedback();
        } catch (IOException ex) {
            Logger.getLogger(Vendor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    //get review based on ID
    public String[] getReview(String reviewID) {
        Review review = new Review();
        return review.getReview(reviewID);
    }
    
    //get rating count for vendor based on time range
    public int[] getVendorRatingCount(String type, String timeRange) {
        Review ratings = new Review();
        return ratings.ratingCount(email, "vendor", type, timeRange);
    }
    
}
