/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.awt.Component;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author mastu
 */
public class User {   
    protected String email;
    private String password;
    private String name;
    private String userType;
    private String birth;
    private String contactNumber;
    
    private String userFile = "resources/user.txt";
    private String cusFile = "resources/customer.txt";
    private String vendorFile = "resources/vendor.txt";
    private String runnerFile = "resources/runner.txt";
    
    // Static variables to act as session
    private static String sessionEmail;
    private static String sessionName;
    private static String sessionRole;
    private static String sessionPassword;

    public User(String email, String password, String name, String userType, String birth, String contactNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userType = userType;
        this.birth = birth;
        this.contactNumber = contactNumber;
    }
    
    public User(String email) {
        this.email = email;
    }
    
    public static void setSession(String email, String password, String role, String name) {
        sessionEmail = email;
        sessionRole = role;
        sessionName = name;
        sessionPassword = password;
    }
    
    public static String getSessionEmail() {
        return sessionEmail;
    }

    public static String getSessionRole() {
        return sessionRole;
    }

    public static String getSessionName() {
        return sessionName;
    }
    
    public static String getSessionPassword() {
        return sessionPassword;
    }

    public static void clearSession() {
        sessionEmail = null;
        sessionRole = null;
        sessionName = null;
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
    public void logOut() {
        this.name = null;
        this.email = null;
        this.userType = null;
        clearSession();
    }
    
    // METHOD
    
    // Login
    public String login(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");

                if (userData.length >= 4) {
                    String fileEmail = userData[0];
                    String filePassword = userData[2];
                    String fileName = userData[1];
                    String fileRole = userData[3];

                    if (fileEmail.equals(email) && filePassword.equals(password)) {
                        this.email = fileEmail;
                        this.password = filePassword;
                        this.name = fileName;
                        this.userType = fileRole;

                        setSession(fileEmail, filePassword, fileRole, fileName);
                       
                        String nextPage = determinePageAfterLogin(email);
                        return nextPage;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "loginFailedPage";
    }
    
    // Validation For email&password
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
                    this.name = userDetails[1];
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    //Check first login (User Side) For Login
    public String determinePageAfterLogin(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");

                if (userData[0].equalsIgnoreCase(email)) {
                    String field5 = userData.length > 4 ? userData[4] : "";
                    String field6 = userData.length > 5 ? userData[5] : "";

                     if (field5.isEmpty() || field6.isEmpty()) {
                        String userType = userData[3].toLowerCase();
                        switch (userType) {
                            case "vendor":
                                return "vendor1AccInfo";
                            case "customer":
                            case "runner":
                                return "cusRunner1AccInfo";
                            default:
                                return "unknownRolePage";
                        }
                    }
                    
                    String userType = userData[3].toLowerCase();
                    switch (userType) {
                        case "vendor":
                            return "vendorMainPage";
                        case "admin":
                            return "adminMainPage";
                        case "customer":
                            return "customerMainPage";
                        case "runner":
                            return "runnerMainPage";
                        case "manager":
                            return "managerMainPage";
                        default:
                            return "unknownRolePage";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "loginFailedPage";
    }
    
    public void navigateToPage(String page, JFrame currentFrame) {
        currentFrame.dispose();
        switch (page) {
            case "vendor1AccInfo":
                new vendor1AccInfo().setVisible(true);
                break;
            case "cusRunner1AccInfo":
                new cusRunner1AccInfo().setVisible(true);
                break;
            case "vendorMainPage":
                new vendorMain().setVisible(true);
                break;
            case "adminMainPage":
                new adminMain().setVisible(true);
                break;
            case "customerMainPage":
                new customerMain().setVisible(true);
                break;
            case "runnerMainPage":
                new runnerMain().setVisible(true);
                break;
            case "managerMainPage":
                new managerMain().setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Unknown role or error occurred!", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
    
    // Add user data to user.txt for First Login
    public void userInfo(String email, String name, String password, String contactNumber, String date) {
        List<String> fileContent = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");

                if (userData.length > 3 && userData[0].equalsIgnoreCase(email)) {
                    if (userData.length > 1) {
                        userData[1] = name;
                    } else {
                        userData = Arrays.copyOf(userData, 6);
                        userData[1] = name;
                    }

                    if (userData.length > 2) {
                        userData[2] = password;
                    } else {
                        userData = Arrays.copyOf(userData, 6);
                        userData[2] = password;
                    }

                    if (userData.length > 4) {
                        // Convert date from dd/MM/yyyy to ddMMyyyy
                        String formattedDate = date.replace("/", "");
                        userData[4] = formattedDate;
                    } else {
                        userData = Arrays.copyOf(userData, 6);
                        String formattedDate = date.replace("/", "");
                        userData[4] = formattedDate;
                    }

                    if (userData.length > 5) {
                        userData[5] = contactNumber;
                    } else {
                        userData = Arrays.copyOf(userData, 6);
                        userData[5] = contactNumber;
                    }

                    updated = true;
                }

                fileContent.add(String.join(",", userData));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading user.txt: " + e.getMessage());
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(null, "Information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error writing to user.txt: " + e.getMessage());
            }
        } else {
            System.out.println("No matching email found to update.");
        }
    }
    
    // Save Picture to Images/vendors
    public String savePic(String name, String picturePath){
        try{
            File imageFolder = new File("images/vendors");

            String fileExtension = picturePath.substring(picturePath.lastIndexOf("."));
            String newFileName = name + "Logo" + fileExtension.toLowerCase();

            // Define the destination path for the new file
            File destFile = new File(imageFolder, newFileName);

            // Copy the file from source to destination
            Path sourcePath = Paths.get(picturePath);
            Path destPath = destFile.toPath();
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);

            return "images/vendors/" + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Add vendor Info to vendor.txt for First Login
    public void vendorInfo(String email, String picturePath, String cuisine) {
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
            System.out.println("Error reading vendor.txt: " + e.getMessage());
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(vendorFile))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
                System.out.println("Vendor information updated successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error writing to vendor.txt: " + e.getMessage());
            }
        } else {
            System.out.println("No matching email found to update.");
        }
    }
    
    // Prompt and validate plate number
    public String promptAndValidatePlateNumber(Component parentComponent, String email) {
        String plateNumber = null;

        while (true) {
            plateNumber = JOptionPane.showInputDialog(
                parentComponent,
                "Enter your plate number (letters and numbers, max 15 characters):",
                "Plate Number",
                JOptionPane.PLAIN_MESSAGE
            );

            if (plateNumber == null) {
                JOptionPane.showMessageDialog(parentComponent, "Plate number is required to proceed.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            plateNumber = plateNumber.trim();

            if (isValidPlateNumber(plateNumber)) {
                savePlateNumber(email, plateNumber);
                return plateNumber;
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Invalid plate number. Ensure it contains letters, numbers, and is max 15 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Save plate number
    public void savePlateNumber(String email, String plateNumber) {
        List<String> fileContent = new ArrayList<>();
        boolean emailFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(runnerFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] runnerData = line.split(",");
                if (runnerData.length > 0 && runnerData[0].equalsIgnoreCase(email)) {
                    runnerData = new String[]{runnerData[0], plateNumber.toUpperCase()};
                    emailFound = true;
                }
                fileContent.add(String.join(",", runnerData));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading runner.txt: " + e.getMessage());
        }

        if (emailFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(runnerFile))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
                System.out.println("Runner information updated successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error writing to runner.txt: " + e.getMessage());
            }
            } else {
        System.out.println("No matching email found to update in runner.txt.");
        }
    }

    // Validate plate number
    private boolean isValidPlateNumber(String plateNumber) {
        if (plateNumber.length() > 15) {
            return false;
        }

        boolean containsLetter = false;
        boolean containsNumber = false;

        for (char c : plateNumber.toCharArray()) {
            if (Character.isLetter(c)) {
                containsLetter = true;
            } else if (Character.isDigit(c)) {
                containsNumber = true;
            }
        }

        return containsLetter && containsNumber;
    }
                        
    //Check first login for admin before CRUD user(Admin Side)
    public String checkFirstLogin(String email, String userType, String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");

                if (details[0].equalsIgnoreCase(email)) {

                    if (details.length > 3) {
                        if (!details[3].equalsIgnoreCase(userType)) {
                            return "roleMismatch";
                        }
                    } else { //account has been deleted
                        return "notExisting"; 
                    }
                    // Check birth, contactNumber, gender are filled OR not
                    if (details.length >= 6 && !details[4].isEmpty() && !details[5].isEmpty()) {
                        // Set user data if email is found and first login is complete
                        this.email = details[0];
                        this.name = details[1];
                        this.birth = details[4];
                        this.contactNumber = details[5];
                        
                    // Get additional data based on the role
                    String additionalData = getRoleSpecificData(details[0], userType, 1); // the additional data for the specific role is at index 1
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
    //used for admin search to update user, credit top up, withdraw and customer payment
    public String getRoleSpecificData(String email, String role, int dataIndex) {
        String fileName = getRoleFileName(role);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details[0].equalsIgnoreCase(email)) {
                    switch (role.toLowerCase()) {
                        case "customer":
                            return details[dataIndex];
                        case "vendor":
                            return details[dataIndex];
                        case "runner":
                            return details[dataIndex];
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
    //used for admin search to update user, credit top up, withdraw and  customer payment
    private String getRoleFileName(String role) {
        switch (role.toLowerCase()) {
            case "customer":
                return cusFile;
            case "vendor":
                return vendorFile;
            case "runner":
                return runnerFile;
            default:
                return "";
        }
    }
    
    // Get the role based on email from user.txt - used for top up, withdraw and payment side
    public String getRoleByEmail(String email, String userFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Assuming CSV format
                if (parts.length > 3 && parts[0].trim().equalsIgnoreCase(email)) {
                    return parts[3].trim(); // Role is at index 3
                }
            }
        }
        return null; // Return null if email is not found
    }
    
    // Update the credit for vendor, cus, and runner (Admin, customer, vendor side)
    public void updateCredit(String email, double newAmount, String filePath, int index) throws IOException {
        StringBuilder updatedData = new StringBuilder();
        boolean customerFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[0].equals(email)) {
                    parts[index] = String.format("%.2f", newAmount);
                    customerFound = true;
                }
                updatedData.append(String.join(",", parts)).append("\n");
            }
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(updatedData.toString());
        }

        if (!customerFound) {
            throw new IOException("Email " + email + " not found. Please try again.");
        }
    }
    
     // Set the input not > max length
    public static DocumentFilter createLengthFilter(int maxLength) {
        return new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                if (fb.getDocument().getLength() - length + text.length() <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };
    }
}