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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class Customer extends User{
    private int points;
    private String customerFile = "resources/customer.txt";
    private final static String cusFile = "resources/customer.txt";
    
    public Customer(String email){ //for set points
        super(email);
        //paolawan@mail.com,,0.0,13
        try{
            BufferedReader br = new BufferedReader(new FileReader(customerFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");
                if (email.equals(record[0])) {
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
        int earnablePoints = (int) Math.round(payment * 0.01);
        return earnablePoints;
    }
    
    public static String deliveryAddress(Component parentComponent, String email) {
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
                    "Invalid address. Please ensure it is non-empty, under 255 characters, and only contains letters, numbers, spaces, and commas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Validate the address
    private static boolean isValidAddress(String address) {
        // only letters, numbers, spaces, and commas
        String regex = "^[a-zA-Z0-9,\\s]+$";

        return address.length() > 20 && address.length() <= 255 && address.matches(regex);
    }

    // Save the delivery address
    private static void saveDeliveryAddress(String email, String address) {
        boolean emailFound = false;
        List<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(cusFile))) {
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
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/customer.txt"))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
