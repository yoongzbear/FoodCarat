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
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class Admin extends User {
    
    public Admin(){
        
    }
    //clear fields after register, delete, update performed....
    public static void clearFields(javax.swing.text.JTextComponent... components) {
        for (javax.swing.text.JTextComponent component : components) {
            component.setText("");
        }
    }
    //Registration
    //validate email is registered for Registration
    public static boolean isEmailRegistered(String email, String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(email)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public static String registerUser(String email, String username, String role) throws IOException {
        String samplePassword = generateSamplePassword(email);
        String userFileName = "user.txt";
        String roleFileName = role + ".txt";

        // Write to user.txt
        try (FileWriter userWriter = new FileWriter(userFileName, true)) {
            userWriter.write(email + "," + username + "," + samplePassword + "," + role + ",,\n");
        }

        // Write to role-specific file
        try (FileWriter roleWriter = new FileWriter(roleFileName, true)) {
            if ("customer".equalsIgnoreCase(role)) {
                roleWriter.write(email + ",,0.0\n"); //credit amount will be 0.0 after the registration
            } else {
                roleWriter.write(email + ",,\n");
            }
        }

        return samplePassword; // Return the generated password
    }
    
    private static String generateSamplePassword(String email) {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(email.split("@")[0]);
        for (int i = 0; i < 3; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }
    
    //Update user info
    //customize form for vendor, runner and customer in update user info part
    public static void customizeForm(String role, 
                                     javax.swing.JComponent[] customerComponents, 
                                     javax.swing.JComponent[] vendorComponents, 
                                     javax.swing.JComponent[] runnerComponents) {
        // Hide all components initially
        for (javax.swing.JComponent component : concatArrays(customerComponents, vendorComponents,runnerComponents)) {
            component.setVisible(false);
        }
        // Adjust visibility based on the role
        switch (role) {
            case "customer":
                for (javax.swing.JComponent component : customerComponents) {
                    component.setVisible(true); 
                }
                break;
            case "vendor":
                for (javax.swing.JComponent component : vendorComponents) {
                    component.setVisible(true); 
                }
                break;
            case "runner":
                for (javax.swing.JComponent component : runnerComponents) {
                    component.setVisible(true); // Show runner-specific components
                }
                break;
        }
    }

    // Utility method to concatenate multiple arrays of components (used in customize form)
    private static javax.swing.JComponent[] concatArrays(javax.swing.JComponent[]... arrays) {
        return java.util.Arrays.stream(arrays).flatMap(java.util.Arrays::stream).toArray(javax.swing.JComponent[]::new);
    }

    //search for update user info
    public void searchUser(String searchEmail, String selectedRole, adminUpdateUser updateUserForm) {
        String userFile = "user.txt";

        // Check the login status, role match, and email match
        String result = checkFirstLogin(searchEmail, selectedRole, userFile);

        // Handle based on the result of checkFirstLogin
        if (result.equals("notFound")) {
            JOptionPane.showMessageDialog(null, "Email not found. Please check the entered email.");
        } else if (result.equals("roleMismatch")) {
            JOptionPane.showMessageDialog(null, "The role does not match the selected role.");
        } else if (result.equals("notCompleted")) {
            JOptionPane.showMessageDialog(null, "User has not completed the first login. Admin can't perform the update for the user.");
        } else {
        // If the user has completed the first login, update the user data
        updateUserForm.setUserData(getEmail(), getName(), getBirth(), getContactNumber(), result);
        }
    }
    
    //validate fields before performing update and delete
    public static boolean validateFields(String email, String name, String birthDate, String phone, 
                                         String role, String additionalField) {
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Email field cannot be empty!");
            return false;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name field cannot be empty!");
            return false;
        }
        if (birthDate.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Birth date cannot be empty!");
            return false;
        } else if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", birthDate)) {
            JOptionPane.showMessageDialog(null, "Please enter a valid birth date in the format YYYY-MM-DD!");
            return false;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Phone number cannot be empty!");
            return false;
        } else if (!Pattern.matches("^\\d{3}-\\d{7}$", phone)) {
            JOptionPane.showMessageDialog(null, "Phone number must be in the format xxx-xxxxxxx!");
            return false;
        }
        if ("customer".equals(role) && additionalField.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Address field cannot be empty for customer!");
            return false;
        } else if ("vendor".equals(role) && additionalField.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cuisine field cannot be empty for vendor!");
            return false;
        } else if ("runner".equals(role) && additionalField.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plat number field cannot be empty for runner!");
            return false;
        }
        // If all checks passed, return true
        return true;
    }
    
    public boolean performUpdate(
            String email, String name, String birthDate,
            String phone, String role, String additionalField) {

        // Update user.txt
        boolean userUpdated = updateFile("user.txt",
                email,
                new String[]{name, birthDate, phone},
                new int[]{1, 4, 5});

        // Update role-specific file
        boolean roleUpdated = updateFile(role + ".txt",
                email,
                new String[]{additionalField},
                new int[]{1});

        if (userUpdated && roleUpdated) {
            JOptionPane.showMessageDialog(null, "Record updated successfully!");
            return true;
        } else if (!userUpdated) {
            JOptionPane.showMessageDialog(null, "Error updating user.txt!");
        } else {
            JOptionPane.showMessageDialog(null, "Error updating " + role + ".txt!");
        }

        return false;
    }
    
    private boolean updateFile(String fileName, String email, String[] updatedFields, int[] updateIndices) {
        List<String> fileContent = new ArrayList<>();
        boolean recordUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equalsIgnoreCase(email)) {
                    // Update specified fields based on indices
                    for (int i = 0; i < updateIndices.length; i++) {
                        data[updateIndices[i]] = updatedFields[i];
                    }

                    // Reconstruct the updated line
                    line = String.join(",", data);
                    recordUpdated = true;
                }

                fileContent.add(line);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            return false;
        }

        if (!recordUpdated) {
            JOptionPane.showMessageDialog(null, "No matching record found in " + fileName + "!");
            return false;
        }

        return writeFile(fileName, fileContent);
    }
    
    private boolean writeFile(String fileName, List<String> fileContent) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : fileContent) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error writing to file: " + ex.getMessage());
            return false;
        }

        return true;
    }
     
    public boolean performDelete(String email, String role) {
        // Delete from user.txt
        boolean userDeleted = deleteFromFile("user.txt", email);

        // Delete from role-specific file
        boolean roleDeleted = deleteFromFile(role + ".txt", email);

        if (userDeleted && roleDeleted) {
            JOptionPane.showMessageDialog(null, "Record deleted successfully!");
            return true;
        } else if (!userDeleted) {
            JOptionPane.showMessageDialog(null, "Error deleting from user.txt!");
        } else {
            JOptionPane.showMessageDialog(null, "Error deleting from " + role + ".txt!");
        }

        return false;
    }

    private boolean deleteFromFile(String fileName, String email) {
        List<String> fileContent = new ArrayList<>();
        boolean recordDeleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                // Skip the line with the matching email
                if (!data[0].equalsIgnoreCase(email)) {
                    fileContent.add(line);
                } else {
                    recordDeleted = true; // Mark as deleted
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            return false;
        }

        if (!recordDeleted) {
            JOptionPane.showMessageDialog(null, "No matching record found in " + fileName + "!");
            return false;
        }

        return writeFile(fileName, fileContent); // Reuse the writeFile method to save changes
    }
    
    //Top up credit
    //search from the file for the user info and the credit amount (customer.txt - email & credit amount) (user.txt - name)
    //notification also use this (transaction id and transaction details)
    public String[] performSearch(String searchItem, String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase(searchItem)) {
                    return data; // Return the matching record
                }
            }
        } catch (IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
        }
        return null; // Return null if no record is found
    }
    
    // method for top up credit proccess
    public void processTopUp(String email, String name, double currentAmount, double topUpAmount) throws IOException {
        double newAmount = currentAmount + topUpAmount;

        String transactionId = generateTransactionId();
        String dateTime = getCurrentDateTime();
        // Extract date and time separately
        String[] dateTimeParts = dateTime.split(" ");
        String date = dateTimeParts[0];
        String time = dateTimeParts[1];

        // Prepare transaction details
        String transactionDetails = String.format(
            "%s;%s;%.2f;%.2f;%s;%s\n",
            transactionId, email, topUpAmount, newAmount, date, time
        );

        // Save transaction details to the file
        saveTransactionToFile(transactionDetails);

        // Update the customer's credit in the file
        updateCustomerCredit(email, newAmount);
        
        // Notify the user of success
        javax.swing.JOptionPane.showMessageDialog(null, "Top-up completed successfully!\nThe receipt has been sent to the customer.", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        
        // Show receipt UI
        adminCusReceipt receipt = new adminCusReceipt(transactionId, email, name, currentAmount, topUpAmount, newAmount, date, time);
        receipt.setVisible(true);
    }

    // Generate a unique transaction ID for top up details
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    // Get the current date and time for top up details
    private String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }

    // Save transaction details to the file
    private void saveTransactionToFile(String transactionDetails) throws IOException {
        String filePath = "customer credit.txt";
        try (FileWriter writer = new FileWriter(filePath, true)) { // Append mode
            writer.write(transactionDetails);
        }
    }

    // Update the customer's credit in the file
    public void updateCustomerCredit(String email, double newAmount) throws IOException {
        String filePath = "customer.txt";
        StringBuilder updatedData = new StringBuilder();
        boolean customerFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1 && parts[0].equals(email)) {
                    // Update the credit amount
                    parts[2] = String.format("%.2f", newAmount);
                    customerFound = true;
                }
                updatedData.append(String.join(",", parts)).append("\n");
            }
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(updatedData.toString());
        }

        if (!customerFound) {
            throw new IOException("Customer with email " + email + " not found in " + filePath);
        }
    }
    
    //Notification
    // Method to retrieve the list of transaction messages
    public ArrayList<String> getTransactionMessages() {
        ArrayList<String> transactionMessages = new ArrayList<>();
        String filePath = "customer credit.txt";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 6) {
                    String transactionId = parts[0];
                    String email = parts[1];
                    double topUpAmount = Double.parseDouble(parts[2]);
                    double newAmount = Double.parseDouble(parts[3]);
                    String date = parts[4];
                    String time = parts[5];

                    // Create the message in the required format
                    String message = String.format(
                        "Top-up amount %.2f has been successfully credited into %s's account at %s %s (transaction id: %s)",
                        topUpAmount, email, date, time, transactionId
                    );

                    // Add message to the list
                    transactionMessages.add(message);
                }
            }
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        
        return transactionMessages;
    }
}
