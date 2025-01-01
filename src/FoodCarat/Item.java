/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author mastu
 */
public class Item {
    private int itemID;
    private String itemName;
    private String itemType;
    private double itemPrice;
    private String itemImgPath;
    private String venEmail;

    //constructor
    public Item() {
        
    }
    
    //setters
    
    //getters
    
    //method
    //add new item
    public void addItem(String[] itemInfo) {
        //write into item text file
    }
    
    //get item data
    //public String[] itemData() {}
    
    //get item data for latest row (last row)
    public String[] latestItem() {
        //get last row in text file
        String[] latestRow = null;
        try {
            FileReader fr = new FileReader("resources/item.txt");
            BufferedReader br = new BufferedReader(fr);
            String read;
            
            while ((read = br.readLine()) != null) {
                //get last row
                latestRow = read.split(",");
            }
            
            br.close();
            fr.close();
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return latestRow;
    }
    
//    public void vendorItems(String venEmail) {       
//        try {
//            FileReader fr = new FileReader("resources/item.txt");
//            BufferedReader br = new BufferedReader(fr);
//            String read;
//            
//            while ((read = br.readLine()) != null) {
//                //get all rows where vendor email == venEmail
//            }
//        } catch(IOException e) {
//            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
    
    //get data for all items
    public List<String[]> getAllItems() { 
        List<String[]> allItems = new ArrayList<>();
        
        try {
            FileReader fr = new FileReader("resources/item.txt");
            BufferedReader br = new BufferedReader(fr);
            String read;
            
            while ((read = br.readLine()) != null) {
                //get all rows where vendor email == venEmail
                String[] itemData = read.split(",");
                allItems.add(itemData);
            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return allItems;
    }
    
    //update item
    
    //delete item
    
    
}
