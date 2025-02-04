/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class Customer extends User{
    private int points;
    private String customerFile = "resources/customer.txt";
    private String creditFile = "resources/transactionCredit.txt";
    
    public Customer(String email){ //for set points
        super(email);
        try{
            BufferedReader br = new BufferedReader(new FileReader(customerFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");
                if (record.length > 1 && email.equals(record[0])) {
                    this.points = Integer.parseInt(record[3]); 
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //point system related
    public void addPoints(int add){
        this.points += add;
        try{
            //Writing to file
            BufferedReader br = new BufferedReader(new FileReader(customerFile));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null){
                String record[] = line.split(",");
                String checkUsername = record[0];
                if (checkUsername.equals(email)){
                    line = record[0] + "," + record[1] + ","+ record[2] + "," + points;
                }
                String bufferLine = line + "\n";
                buffer.append(bufferLine);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(customerFile));
            bw.write(buffer.toString());
            bw.close();   
    }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    
    public void deductPoints(int sub){
        this.points -= sub;
        try{
            //Writing to file
            BufferedReader br = new BufferedReader(new FileReader(customerFile));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null){
                String record[] = line.split(",");
                String checkUsername = record[0];
                if (checkUsername.equals(email)){
                    line = record[0] + "," + record[1] + ","+ record[2] + "," + points;
                }
                String bufferLine = line + "\n";
                buffer.append(bufferLine);
            }
            br.close();
            BufferedWriter bw = new BufferedWriter(new FileWriter(customerFile));
            bw.write(buffer.toString());
            bw.close();   
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    
    public int getPoints(){
        return points;
    }
    
    public int calculateEarnablePoints(double payment){
        int earnablePoints = (int)payment;
        return earnablePoints;
    }
    
    public String[] customerInfo(String email) {
        String[] userInfo = null;
        try {
            FileReader fr = new FileReader(customerFile);
            BufferedReader br = new BufferedReader(fr);
            String read;

            while ((read = br.readLine()) != null) {
                String[] userParts = read.split(",");
                String userEmail = userParts[0];

                if (email.equals(userEmail)) {
                    userInfo = userParts;
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the user file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return userInfo;
    }
    
    public List<String[]> creditRecord(String email){ //get customer topup record
        List<String[]> records = new ArrayList<>();
        
        // Define the formatter to parse date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(creditFile));
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");  // Split the line into an array
                
                if (email.equals(record[1])) {  // If email matches
                    // Add the record to the list
                    records.add(record);
                }
            }
            br.close();
            
            // Sort records based on the date/time (newest to oldest)
            Collections.sort(records, (record1, record2) -> {
                // Combine date and time parts and parse into LocalDateTime
                String dateTimeStr1 = record1[4] + " " + record1[5];  // Combine date and time for record 1
                String dateTimeStr2 = record2[4] + " " + record2[5];  // Combine date and time for record 2
                dateTimeStr1 = dateTimeStr1.trim();
            dateTimeStr2 = dateTimeStr2.trim();
                LocalDateTime dateTime1 = LocalDateTime.parse(dateTimeStr1, formatter);
                LocalDateTime dateTime2 = LocalDateTime.parse(dateTimeStr2, formatter);
                
                // Compare in descending order (newest first)
                return dateTime2.compareTo(dateTime1);
            });
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return records;  
    }
    
    public String[] getTopupInfo(int transactionID) {
        String[] topupInfo = null;
        try {
            FileReader fr = new FileReader(creditFile);
            BufferedReader br = new BufferedReader(fr);
            String read;

            while ((read = br.readLine()) != null) {
                String[] record = read.split(",");
                String selectedTransactID = record[0];

                if (selectedTransactID.equals(String.valueOf(transactionID))) {
                    topupInfo = record;
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the credit file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return topupInfo;
    }
    
    // for contrustor
    public Customer(){
        super();
    }
    
    public String deliveryAddress(Component parentComponent, String email) {
        String address = null;

        while (true) {
            address = JOptionPane.showInputDialog(
                parentComponent,
                "Enter your delivery address (max 255 characters):",
                "Delivery Address",
                JOptionPane.PLAIN_MESSAGE
            );

            if (address == null) {
                // User canceled input
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Delivery address is required to proceed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return null;
            }

            address = address.trim();

            // Validate the address
            if (isValidAddress(address)) {
                saveDeliveryAddress(email, address);
                return address;
            } else {
                JOptionPane.showMessageDialog(
                    parentComponent,
                    "Invalid address. Please ensure it is non-empty, under 255 characters.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Validate the address
    private boolean isValidAddress(String address) {
        return address.length() > 20 && address.length() <= 255;
    }

    // Save the delivery address
    private void saveDeliveryAddress(String email, String address) {
        boolean emailFound = false;
        List<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(customerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equalsIgnoreCase(email)) {
                    data = new String[]{data[0], "[" + address.replace(",", ";") + "]", "0.0", "0"};
                    emailFound = true;
                }
                fileContent.add(String.join(",", data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (emailFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerFile))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get customer address
    public String getCustomerAddress(String customerEmail) {
        try (BufferedReader br = new BufferedReader(new FileReader(customerFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] customerData = line.split(",");
                if (customerData.length > 1 && customerData[0].equals(customerEmail)) {
                    return customerData[1].replace("[", "").replace("]", "").replace(";", ",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
    }
}