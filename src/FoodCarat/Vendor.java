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

/**
 *
 * @author mastu
 */
public class Vendor extends User{
            
    private String cuisine;
    private String photoLink;
    private String availableMethod;
    private double creditBalance;
    
    private String vendorFile = "resources/vendor.txt";
    private String vendorCreditFile = "resources/transactionCredit.txt";
    
    private double totalRevenue;
    
    public Vendor(String email) {
        super(email);
        getVendorInfo(email);
    }
    
    //constructor for performance tracking
    public Vendor(String vendorName, boolean initializeName) {
        if (initializeName) {
            this.name = vendorName; // Set vendorName if flag is true
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
    
    public double getTotalRevenue() {
        return totalRevenue;
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to write to the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Add vendor Info to vendor.txt for First Login
    public void addVendorInfo(String email, String picturePath, String cuisine) {
        List<String> fileContent = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(vendorFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] vendorData = line.split(",");

                if (vendorData.length > 1 && vendorData[0].equalsIgnoreCase(email)) {
                    vendorData[1] = cuisine;
                    vendorData[2] = picturePath;
                    updated = true;
                }

                fileContent.add(String.join(",", vendorData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(vendorFile))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //vendor withdrawal transaction
    public List<String[]> getWithdrawalTransaction() {
        List<String[]> transaction = new ArrayList<>();
        try {
            File fileName = new File(vendorCreditFile);
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String read;
            boolean found = false; //flag for finding vendor record
            
            while((read=br.readLine()) !=null ) {
                //scanning the file until it meets null
                String[] data = read.split(",");
                if(data.length > 0 && data[1].equalsIgnoreCase(email)){
                    found = true;
                    transaction.add(data);
                }
            }
            fr.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }        
        
        return transaction;
    }
    
    //withdrawal details
    public String[] getWithdrawalTransaction(int transactionID) {
        String[] details = new String[7];
        List<String[]> transaction = getWithdrawalTransaction();

        for (String[] data : transaction) {
            if (data.length > 0 && Integer.parseInt(data[0]) == transactionID) {
                for (int i = 0; i < Math.min(data.length, details.length); i++) {
                    details[i] = data[i];
                }
                return details; 
            }
        }
        return details; //null or partially filled
    }
}
