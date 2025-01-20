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

    private static String userFile = "resources/user.txt";
    private String cusFile = "resources/customer.txt";
    private String cuscreditFile = "resources/customerCredit.txt";
    
    public Admin(){
        
    }
    //clear fields after register, delete, update performed....
    public static void clearFields(java.awt.Component... components) {
        for (java.awt.Component component : components) {
            if (component instanceof javax.swing.text.JTextComponent) {
                ((javax.swing.text.JTextComponent) component).setText(""); // Clear text fields
            } else if (component instanceof javax.swing.JComboBox) {
                ((javax.swing.JComboBox<?>) component).setSelectedIndex(-1); // Clear combo box selection
            }
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
        // Generate sample password
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder samplePassword = new StringBuilder(email.split("@")[0]);
        for (int i = 0; i < 3; i++) {
            samplePassword.append(characters.charAt(random.nextInt(characters.length())));
        }
        String password = samplePassword.toString();
        String roleFileName = role + ".txt";
        
        // Write to user.txt
        try (FileWriter userWriter = new FileWriter(userFile, true)) {
            userWriter.write(email + "," + username + "," + samplePassword + "," + role + ",,\n");
        }

        // Write to role-specific file
        try (FileWriter roleWriter = new FileWriter(roleFileName, true)) {
            if ("customer".equalsIgnoreCase(role)) {
                roleWriter.write(email + ",,0.0,0\n"); //credit amount will be 0.0 after the registration and the point will be 0
            } else {
                roleWriter.write(email + ",,\n");
            }
        }

        return password; // Return the generated password
    }
    
    //Update user info
    //search for update user info
    public void searchUser(String searchEmail, String selectedRole, adminUpdateUser updateUserForm) {

        // Check the login status, role match, and email match
        String result = checkFirstLogin(searchEmail, selectedRole, userFile);

        // Handle based on the result of checkFirstLogin
        if (result.equals("notFound")) {
            JOptionPane.showMessageDialog(null, "Email not found. Please check the entered email.");
        } else if (result.equals("roleMismatch")) {
            JOptionPane.showMessageDialog(null, "The role does not match the selected role.");
        } else if (result.equals("notCompleted")) {
            JOptionPane.showMessageDialog(null, "User has not completed the first login. Admin can't perform the update for the user.");
        } else if (result.equals("notExisting")) {
            JOptionPane.showMessageDialog(null, "User account does not exist. Please try again.");
        }else {
        // If the user has completed the first login, update the user data
        updateUserForm.setUserData(getEmail(), getName(), getBirth(), getContactNumber(), result);
        }
    }    
    
    //update both user and role.txt when admin update user info
    public boolean performUpdate(
            String email, String name, String birthDate,
            String phone, String role, String additionalField) {

        boolean userUpdated = updateFile(userFile,
                email,
                new String[]{name, birthDate, phone},
                new int[]{1, 4, 5});

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
        
    // Remain the email and name at user.txt but remove others info (eg.email,name,,,)
    public boolean removeInfoUserFile(String fileName, String email) {
        List<String> fileContent = new ArrayList<>();
        boolean recordUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equalsIgnoreCase(email)) {
                    recordUpdated = true;
                    // Update the user record with email, name, and empty fields
                    fileContent.add(data[0] + "," + data[1] + ",,,,");
                } else {
                    fileContent.add(line); // Keep other records unchanged
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            return false;
        }

        if (!recordUpdated) {
            JOptionPane.showMessageDialog(null, "No matching record found in " + fileName + "!");
            return false;
        }

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
    //delete the info in role.txt
    public boolean deleteFromFile(String fileName, String email) {
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
    
    //Top up credit    
    // Utility method for calculating new amounts (withdraw or top up)
    public static double calculateNewAmount(double currentAmount, double amount, boolean isTopUp) {
        return isTopUp ? currentAmount + amount : currentAmount - amount;
    }
    
    // method for top up credit proccess
    public void processChangesCredit(String email, String name, double currentAmount, double topUpAmount) throws IOException {
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
        super.updateCredit(email, newAmount, cusFile, 2);
        
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
        try (FileWriter writer = new FileWriter(cuscreditFile, true)) { // Append mode
            writer.write(transactionDetails);
        }
    }
    
    //Notification
    // Method to retrieve the list of transaction messages
    public ArrayList<String> getTransactionMessages() {
        ArrayList<String> transactionMessages = new ArrayList<>();
        String filePath = cuscreditFile;
        
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
