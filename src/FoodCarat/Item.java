/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

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
        //get last row in tetx file
        String[] latestRow = null;
        return latestRow;
    }
    
    //get data for all items for the vendor
    public void vendorItems(String venEmail) {
        //meow
    }
    
    //update item
    
    //delete item
    
    
}
