/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author mastu
 */
public class User {   
    private String email;
    private String password;
    private String name;
    private String userType;
    private String birth;
    private String contactNumber;
    
    private String userFile = "resources/user.txt";

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
    
    // METHOD
    
    // Login
    public boolean login(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(","); // Assuming data is comma-separated
                if (userData.length == 4) {
                    String fileEmail = userData[0]; 
                    String filePassword = userData[2];

                    if (fileEmail.equals(email) && filePassword.equals(password)) {
                        // Load user details into this instance
                        this.userType = userData[2];
                        this.email = fileEmail;
                        this.password = filePassword;
                        return true; // Login successful
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Login failed
    }
    
    // Validation For LOGIN
    public boolean emailExists(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean passwordMatches(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(email) && userDetails[2].equals(password)) {
                    this.name = userDetails[1]; // Assuming the name is in the first column
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    //Check first login (User Side) For Login
                        
    //Check first login For admin to CRUD user(Admin Side)
    public String checkFirstLogin(String email, String userType) {
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");

                if (details[0].equalsIgnoreCase(email)) {

                    if (!details[3].equalsIgnoreCase(userType)) {
                        return "roleMismatch";
                    }
                    // Check birth, contactNumber, gender are filled OR not
                    if (details.length >= 6 && !details[4].isEmpty() && !details[5].isEmpty()) {
                        // Set user data if email is found and first login is complete
                        this.email = details[0];
                        this.name = details[1];
                        this.birth = details[4];
                        this.contactNumber = details[5];
                        
                    // Get additional data based on the role
                    String additionalData = getRoleSpecificData(details[0], userType);
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
    
    // Get the specific role data if the user has perfor the first login (Admin Side)
    private String getRoleSpecificData(String email, String role) {
        String fileName = getRoleFileName(role);
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
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

    //(Admin Side)
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
