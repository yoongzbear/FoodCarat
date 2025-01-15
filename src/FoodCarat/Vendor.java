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
import java.util.stream.Collectors;

/**
 *
 * @author mastu
 */
public class Vendor {
    //constructor    
    private String email;
    //super(email,password,name);
            
    private String cuisine;
    private String photoLink;
    private String availableMethod;
    private double creditBalance;
    
    private String vendorFile = "resources/vendor.txt";
    
    public Vendor(String email) {
        //super(email)
        this.email = email;
        getVendorInfo(email);
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
    
    //get all vendor orders - access it here         
    public List<String[]> getVendorOrders(String venEmail) {
        Order order = new Order();
        List<String[]> vendorOrders = order.getAllOrders(venEmail);
        
        return vendorOrders;
    }
    
    //accept order status
    //call update order status in order class
    
    //reject order
    //call update order status in order class
    
    //get all reviews + orders + items for the vendor
    //items txt = calculate price, get vendor email
    public List<String[]> getAllReviewInfo(String venEmail) {
        List<String[]> allReviewInfo = new ArrayList<>();
        
        //review
        Review review = new Review();
        allReviewInfo = review.getAllReviews(venEmail, "vendor");      
                
        return allReviewInfo;
    }    
    
}
