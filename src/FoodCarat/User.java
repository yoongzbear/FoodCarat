/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author mastu
 */
public class User {   
    public String email;
    public String password;
    public String name;
    public String userType;
    public String birth;
    public String contactNumber;

    public User(String email, String password, String name, String userType, String birth, String contactNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userType = userType;
        this.birth = birth;
        this.contactNumber = contactNumber;
    }
        
    public User(){
        this.name = null;
        this.email = null;
        this.userType = null;
    }

    // Method to verify login

        
    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    // Log out
    public void logOut(){
        this.name = null;
        this.email = null;
        this.userType = null;
    }
    
    //check the user whether has performed the first login, before admin update the info
    public String checkFirstLogin(String searchEmail, String selectedRole, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");

                if (details[0].equalsIgnoreCase(searchEmail)) {

                    if (!details[3].equalsIgnoreCase(selectedRole)) {
                        return "roleMismatch";
                    }
                    // Check if the required fields (birth, contact, gender) are filled
                    if (details.length >= 6 && !details[4].isEmpty() && !details[5].isEmpty()) {
                        // Set user data if email is found and first login is complete
                        this.email = details[0];
                        this.name = details[1];
                        this.birth = details[4];
                        this.contactNumber = details[5];
                        
                    // Get additional data based on the role
                    String additionalData = getRoleSpecificData(details[0], selectedRole);
                    return additionalData;
                    } else {
                        return "notCompleted"; // User hasn't completed first login
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return "notFound"; // Email not found
    }
    
    //get the specific role data if the user has perfor the first login
    private String getRoleSpecificData(String email, String role) {
        String fileName = getRoleFileName(role);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(email)) {
                    switch (role.toLowerCase()) {
                        case "customer":
                            return details[1]; // Address for customer
                        case "vendor":
                            return details[1]; // Cuisine for vendor
                        case "runner":
                            return details[1]; // Plate number for runner
                        default:
                            return "Role not found";
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return "Data not found"; // Data not found for the given email and role
    }

    private String getRoleFileName(String role) {
        switch (role.toLowerCase()) {
            case "customer":
                return "customer.txt";
            case "vendor":
                return "vendor.txt";
            case "runner":
                return "runner.txt";
            default:
                return "";
        }
    }
}
