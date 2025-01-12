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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
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
    private String itemStatus;
    
    private String itemFile = "resources/item.txt";

    //constructor
    public Item() {
        
    }

    //for add new item
    public Item(int itemID, String itemName, String itemType, double itemPrice, String itemImgPath, String venEmail, String itemStatus) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemType = itemType;
        this.itemPrice = itemPrice;
        this.itemImgPath = itemImgPath;
        this.venEmail = venEmail;
        this.itemStatus = itemStatus;
    }       

    //setters
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemImgPath(String itemImgPath) {
        this.itemImgPath = itemImgPath;
    }

    public void setVenEmail(String venEmail) {
        this.venEmail = venEmail;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }
    
    //getters
    public int getItemID() {    
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public String getItemImgPath() {
        return itemImgPath;
    }

    public String getVenEmail() {
        return venEmail;
    }

    public String getItemStatus() {
        return itemStatus;
    }
    
    public void addItem() {
        //write into item text file
        try {
            FileWriter fw = new FileWriter(itemFile,true);
            BufferedWriter bw = new BufferedWriter(fw);
            //convert price to string
            DecimalFormat df = new DecimalFormat("0.00");
            String itemPriceStr = df.format(itemPrice).toString();
            String newRow = itemID + "," + itemName + "," + itemType + "," + itemPriceStr + "," + itemImgPath + "," + venEmail + "," + itemStatus;
            
            //write row and add new row
            bw.write(newRow);
            bw.newLine();
            JOptionPane.showMessageDialog(null, "Item successfully added to the file!", "Success", JOptionPane.INFORMATION_MESSAGE);            
            bw.close();
            fw.close();
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to write into the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //upload item image into images/menu
    public String uploadImage(String sourcePath) {
        try {
            //ensure folder exists
            Path destinationFolder = Paths.get("images/menu");
            if (!Files.exists(destinationFolder)) {
                Files.createDirectories(destinationFolder);
            }

            //get source file and create destination path
            Path sourceFile = Paths.get(sourcePath);
            Path destinationFile = destinationFolder.resolve(sourceFile.getFileName());

            //copy file into destination file
            Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);

            //path of uploaded image
            return "images/menu/" + sourceFile.getFileName();
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to upload image into the folder: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    //get item data
    public String[] itemData(String itemID) {
        String[] itemInfo = null;
        try {
            FileReader fr = new FileReader(itemFile);
            BufferedReader br = new BufferedReader(fr);
            String read;
            
            while ((read = br.readLine()) != null) {
                String[] parts = read.split(","); 
                if (parts[0].equals(itemID)) { 
                    itemInfo = parts;
                    break; 
                }
            }
            br.close();
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return itemInfo;
    }
    
    //get item data for latest row (last row)
    public String[] latestItem() {
        //get last row in text file
        String[] latestRow = null;
        try {
            FileReader fr = new FileReader(itemFile);
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
        
    //get data for all available items
    public List<String[]> getAllItems() { 
        List<String[]> allItems = new ArrayList<>();
        
        try {
            FileReader fr = new FileReader(itemFile);
            BufferedReader br = new BufferedReader(fr);
            String read;
            
            while ((read = br.readLine()) != null) {
                String[] itemData = read.split(",");
                if(itemData[6].equals("available")) {
                    allItems.add(itemData);
                }
            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return allItems;
    }
    
    //method overloading
    //get data for all items matching vendor email
    public List<String[]> getAllItems(String venEmail) { 
        List<String[]> allItems = new ArrayList<>();
        
        try {
            FileReader fr = new FileReader(itemFile);
            BufferedReader br = new BufferedReader(fr);
            String read;
            
            while ((read = br.readLine()) != null) {
                //get all rows where vendor email == venEmail
                String[] itemData = read.split(",");
                if(itemData[5].equals(venEmail) && itemData[6].equals("available")) {
                    allItems.add(itemData);
                }
            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return allItems;
    }
    
    //update item
    public void editItem(String id, String[] newInfo) {
        //get all data
        List<String[]> allItems = getAllItems();
        boolean isEdited = false;
        
        try {
            FileWriter fw = new FileWriter(itemFile);
            BufferedWriter bw = new BufferedWriter(fw);
            
            for (String[] itemData : allItems) {
                if (!itemData[0].equals(id)) {
                    //keep the row if the ID does not match
                    bw.write(String.join(",", itemData));
                    bw.newLine();
                } else {
                    //found row and rewrite the row with the new info
                    bw.write(String.join(",", newInfo));
                    bw.newLine();
                    isEdited = true;
                }
            }
            bw.close();
            fw.close();
            
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to update the item information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        if (isEdited) {
            JOptionPane.showMessageDialog(null, "Item successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to find the item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //delete item
    public void deleteItem(String id, String userType) {
        //get all data
        List<String[]> allItems = getAllItems();
        boolean isDeleted = false;

        try {
            FileWriter fw = new FileWriter(itemFile);
            BufferedWriter bw = new BufferedWriter(fw);

            for (String[] itemData : allItems) {
                if (!itemData[0].equals(id)) {
                    //keep the row if the ID does not match
                    bw.write(String.join(",", itemData));
                    bw.newLine();
                } else {
                    //found row change status at the end to deleted by userType
                    itemData[itemData.length - 1] = "deleted by " + userType;
                    bw.write(String.join(",", itemData));
                    bw.newLine();
                    isDeleted = true;
                }
            }
            bw.close();
            fw.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to delete the item information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (isDeleted) {
            JOptionPane.showMessageDialog(null, "Item successfully deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Failed to find the item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
