/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JOptionPane;
import java.io.IOException;

/**
 *
 * @author mastu
 */
public class Vendor {
    //alya@mail.com,Restaurant,/FoodCarat/images/vendor/ChageeLogo.png,[Dine In,Take Away,Delivery],
    //constructor
    //super(email,password,name);
    
    private String email;
    
    public Vendor(String email) {
        this.email = email;
    }
    
    //setters
    
    //getters
    
    //methods
    //home page
    //get vendor information in array
    public String[] getVendorInfo(String email){
        try {
            //String fileName = "/resources/vendor.txt"; "C:\Users\mastu\OneDrive\Documents\NetBeansProjects\FoodCarat\resources\vendor.txt"
            File fileName = new File("resources/vendor.txt");
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String read;
            boolean found = false; //flag for finding vendor record
            
            while((read=br.readLine()) !=null ) {
                //scanning the file until it meets null
                String[] data = read.split(",", -1);
                if(data.length > 0 && data[0].equalsIgnoreCase(email)){
                    found = true;
                    return data;
                }
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }        
        return null; //if no data is matching - add validation soon
    }
    
    //update method availability
    
    
    
    //    protected String email;
//    protected String password;
//    protected String name;
    
//    public user(String email, String password, String name) {
//        this.email = email;
//        this.password = password;
//        this.name = name;
//    }
    
    //method
    //login - verify user
    
    //set user session - name, email, type
    
    //logout
}
